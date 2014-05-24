/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.flv.CodecType;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.aac.AacDsiFrameAnalyzer;
import com.ttProject.frame.aac.AacDsiFrameSelector;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.adpcmswf.AdpcmswfFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * 音声用のtag
 * @author taktod
 * nellymoserの16や8の場合はsampleRate 0 bitCount 1 channels 0になるっぽいです。
 * speexは16khzのはずだけど、sampleRate 1 bitCount 1 channels 0になるっぽいです。
 */
public class AudioTag extends FlvTag {
	/** ロガー */
	private Logger logger = Logger.getLogger(AudioTag.class);
	private Bit4 codecId            = new Bit4();
	private Bit2 sampleRate         = new Bit2();
	private Bit1 bitCount           = new Bit1();
	private Bit1 channels           = new Bit1();
	private Bit8 sequenceHeaderFlag = null;
	
	private ByteBuffer    frameBuffer   = null;
	private IAudioFrame   frame         = null;
	private AudioAnalyzer frameAnalyzer = null;
	private boolean       frameAppendFlag = false; // フレームが追加されたことを検知するフラグ
	/**
	 * コンストラクタ
	 * @param tagType
	 */
	public AudioTag(Bit8 tagType) {
		super(tagType);
	}
	/**
	 * デフォルトコンストラクタ
	 */
	public AudioTag() {
		this(new Bit8(0x08));
	}
	/**
	 * フレーム解析オブジェクト保持
	 * @param analyzer
	 */
	public void setFrameAnalyzer(AudioAnalyzer analyzer) {
		this.frameAnalyzer = analyzer;
	}
	/**
	 * サンプルレート参照
	 * @return
	 * @throws Exception
	 */
	public int getSampleRate() throws Exception {
		if(frame == null) {
			switch(getCodec()) {
			case NELLY_16:
			case SPEEX:
				return 16000;
			case NELLY_8:
			case MP3_8:
				return 8000;
			default:
				switch(sampleRate.get()) {
				case 0:
					return 5512;
				case 1:
					return 11025;
				case 2:
					return 22050;
				case 3:
					return 44100;
				default:
					throw new Exception("想定外の数値がでました。");
				}
			}
		}
		return frame.getSampleRate();
	}
	/**
	 * サンプル数参照(frame依存)
	 * @return
	 * @throws Exception
	 */
	public int getSampleNum() throws Exception {
		if(frame == null) {
			analyzeFrame(); // 解析させる
		}
		return frame.getSampleNum();
	}
	/**
	 * チャンネル数
	 * @return
	 */
	public int getChannels() {
		if(frame == null) {
			if(channels.get() == 1) {
				return 2;
			}
			else {
				return 1;
			}
		}
		return frame.getChannel();
	}
	/**
	 * ビット数
	 * @return
	 */
	public int getBitCount() {
		if(frame == null) {
			if(bitCount.get() == 1) {
				return 16;
			}
			else {
				return 8;
			}
		}
		return frame.getBit();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		if(codecId != null) {
			switch(getCodec()) {
			case AAC:
				channel.position(getPosition() + 13);
				frameBuffer = BufferUtil.safeRead(channel, getSize() - 13 - 4);
				if(sequenceHeaderFlag.get() == 0) {
					if(frameAnalyzer == null || !(frameAnalyzer instanceof AacDsiFrameAnalyzer)) {
						throw new Exception("frameAnalyzerがaac(dsi)対応ではないみたいです。");
					}
					DecoderSpecificInfo dsi = new DecoderSpecificInfo();
					dsi.minimumLoad(new ByteReadChannel(frameBuffer));
					((AacDsiFrameSelector)frameAnalyzer.getSelector()).setDecoderSpecificInfo(dsi);
				}
				break;
			default:
				if(getSize() - 12 - 4 > 0) {
					channel.position(getPosition() + 12);
					frameBuffer = BufferUtil.safeRead(channel, getSize() - 12 - 4);
				}
				else {
					channel.position(getPosition() + 11);
				}
				break;
			}
		}
		// prevTagSizeを確認しておく。
		if(getPrevTagSize() != BufferUtil.safeRead(channel, 4).getInt()) {
			throw new Exception("終端タグのデータ量がおかしいです。");
		}
	}
	/**
	 * コーデック参照
	 * @return
	 */
	public CodecType getCodec() {
		return CodecType.getAudioCodecType(codecId.get());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		if(getSize() == 15) {
			logger.warn("empty data is captured.");
			return;
		}
		BitLoader loader = new BitLoader(channel);
		loader.load(codecId, sampleRate, bitCount, channels);
		if(getCodec() == CodecType.AAC) {
			sequenceHeaderFlag = new Bit8();
			loader.load(sequenceHeaderFlag);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		if(frameBuffer == null && frame == null) {
			throw new Exception("データ更新の要求がありましたが、内容データが決定していません。");
		}
		ByteBuffer frameBuffer = null;
		if(frameAppendFlag) {
			// TODO このframe解析の部分は邪魔なので、別の関数にしたいですね。
			// codecId, sampleRate, bitCount, channelsをframeから解析する必要があります。
			IAudioFrame codecCheckFrame = frame;
			if(frame instanceof AudioMultiFrame) {
				// コーデック判定用の一番はじめのframeを参照しなければならない。
				codecCheckFrame = ((AudioMultiFrame) frame).getFrameList().get(0); // 先頭のフレームがあればそれでよい
			}
			// 以下のデータはコーデック情報取得時に決定される可能性がある
			sampleRate   = null;
			bitCount     = null;
			channels     = null;
			int sizeEx = 0;
			// コーデック判定
			if(codecCheckFrame instanceof AacFrame) {
				codecId.set(CodecType.getAudioCodecNum(CodecType.AAC));
				sequenceHeaderFlag = new Bit8(1);
				sizeEx = 1;
			}
			else if(codecCheckFrame instanceof Mp3Frame) {
				if(frame.getSampleRate() == 8000) {
					// mp3 8はデータが手元にないので、どうなるかわからない。
					// とりあえず0xD2にでもしておくか・・・
					codecId.set(CodecType.getAudioCodecNum(CodecType.MP3_8));
					sampleRate = new Bit2();
				}
				else {
					codecId.set(CodecType.getAudioCodecNum(CodecType.MP3));
				}
			}
			else if(codecCheckFrame instanceof NellymoserFrame) {
				if(frame.getSampleRate() == 16000) {
					// nelly16 0x42
					codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY_16));
					sampleRate = new Bit2();
				}
				else if(frame.getSampleRate() == 8000) {
					// nelly8の場合0x52になる。
					codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY_8));
					sampleRate = new Bit2();
				}
				else {
					codecId.set(CodecType.getAudioCodecNum(CodecType.NELLY));
				}
			}
			else if(codecCheckFrame instanceof SpeexFrame) {
				// 0xB6みたい。
				codecId.set(CodecType.getAudioCodecNum(CodecType.SPEEX));
				if(frame.getSampleRate() != 16000) {
					throw new Exception("speexのsampleRateは16kHzのみサポートします。");
				}
				if(frame.getChannel() != 1) {
					throw new Exception("speexはmonoralのみサポートします。");
				}
				sampleRate = new Bit2(1);
			}
			else if(codecCheckFrame instanceof AdpcmswfFrame) {
				codecId.set(CodecType.getAudioCodecNum(CodecType.ADPCM));
			}
			else {
				throw new Exception("未対応なaudioFrameでした:" + frame);
			}
			// チャンネル対応
			if(channels == null) {
				channels = new Bit1();
				switch(frame.getChannel()) {
				case 1:
					channels.set(0);
					break;
				case 2:
					channels.set(1);
					break;
				default:
					throw new Exception("音声チャンネル数がflvに適合しないものでした。");
				}
			}
			// bitCount
			if(bitCount == null) {
				bitCount = new Bit1();
				switch(frame.getBit()) {
				case 8:
					bitCount.set(0);
					break;
				case 16:
					bitCount.set(1);
					break;
				default:
					// bit深度情報はもっていないコンテナもあるみたいです。(というか基本的に圧縮データにbit深度という情報はないみたい。(復元したらどうなるか・・・の問題っぽい。))
					bitCount.set(1);
//					throw new Exception("ビット深度が適合しないものでした。:" + frame.getBit());
				}
			}
			// sampleRate
			if(sampleRate == null) {
				sampleRate = new Bit2();
				switch((int)(frame.getSampleRate() / 100)) {
				case 55:
					sampleRate.set(0);
					break;
				case 110:
					sampleRate.set(1);
					break;
				case 220:
					sampleRate.set(2);
					break;
				case 441:
					sampleRate.set(3);
					break;
				default:
					throw new Exception("frameRateが適合しないものでした。");
				}
			}
			frameAppendFlag = false;
			// データの再構成はgetFrameBufferで実行すればよいと思います。
			// sizeとかの調整も必要です。
			frameBuffer = getFrameBuffer();
			setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
			setTimebase(1000);
			setSize(11 + 1 + sizeEx + frameBuffer.remaining() + 4);
		}
		else {
			frameBuffer = getFrameBuffer();
		}
		BitConnector connector = new BitConnector();
		ByteBuffer startBuffer = getStartBuffer();
		ByteBuffer audioInfoBuffer = connector.connect(
				codecId, sampleRate, bitCount, channels,
				sequenceHeaderFlag /* aac用の追加データ */
		);
		ByteBuffer tailBuffer = getTailBuffer();
		setData(BufferUtil.connect(
				startBuffer,
				audioInfoBuffer,
				frameBuffer,
				tailBuffer
		));
	}
	/**
	 * frameBuffer参照
	 * @return
	 */
	private ByteBuffer getFrameBuffer() throws Exception {
		if(frameBuffer == null) {
			// frameから復元する必要あり
			if(frame != null) {
				// TODO nellymoserでaudioMultiFrameになっている可能性があるので、その場合は単純連結する必要あり
				// multiFrameになっている場合は結合する必要あり。
				if(frame instanceof AudioMultiFrame) {
					List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
					for(IAudioFrame aFrame : ((AudioMultiFrame) frame).getFrameList()) {
						buffers.add(aFrame.getData());
					}
					frameBuffer = BufferUtil.connect(buffers);
				}
				else if(frame instanceof AacFrame) {
					// aacの場合は先頭の7byteをドロップする必要あり。(mshでglobalHeaderがあるので、header部分を落とします)
					ByteBuffer frameData = frame.getData();
					frameData.position(7);
					frameBuffer = ByteBuffer.allocate(frameData.remaining());
					frameBuffer.put(frameData);
					frameBuffer.flip();
				}
				else {
					frameBuffer = frame.getData();
				}
			}
		}
		if(frameBuffer == null) {
			return null;
		}
		else {
			return frameBuffer.duplicate();
		}
	}
	/**
	 * フレーム解析
	 * @throws Exception
	 */
	private void analyzeFrame() throws Exception {
		if(frameBuffer == null) {
			throw new Exception("frameデータが読み込まれていません");
		}
		if(getCodec() == CodecType.AAC && sequenceHeaderFlag.get() != 1) {
			// aacのmshも処理しません。
			return;
		}
		if(frameAnalyzer == null) {
			throw new Exception("frameの解析プログラムが設定されていません。");
		}
		IReadChannel channel = new ByteReadChannel(frameBuffer);
		AudioSelector selector = frameAnalyzer.getSelector();
		selector.setBit(getBitCount());
		selector.setChannel(getChannels());
//		selector.setSampleNum(getSampleNum()); // sampleNumは無限ループになるのでやらない
		selector.setSampleRate(getSampleRate());
		do {
			AudioFrame audioFrame = (AudioFrame)frameAnalyzer.analyze(channel);
			audioFrame.setPts(getPts());
			audioFrame.setTimebase(getTimebase());
			if(frame != null) {
				if(!(frame instanceof AudioMultiFrame)) {
					AudioMultiFrame multiFrame = new AudioMultiFrame();
					multiFrame.addFrame(frame);
					frame = multiFrame;
				}
				((AudioMultiFrame)frame).addFrame((IAudioFrame)audioFrame);
			}
			else {
				frame = (IAudioFrame)audioFrame;
			}
		} while(channel.size() != channel.position());
	}
	/**
	 * フレーム参照
	 * @return
	 * @throws Exception
	 */
	public IAudioFrame getFrame() throws Exception {
		if(frame == null) {
			analyzeFrame();
		}
		return frame;
	}
	/**
	 * frameを追加する
	 * @param frame
	 */
	public void addFrame(IAudioFrame tmpFrame) throws Exception {
		logger.info("フレームの設定が呼び出されました。");
		if(tmpFrame == null) {
			// 追加データがないなら、放置
			return;
		}
		if(!(tmpFrame instanceof IAudioFrame)) {
			throw new Exception("audioTagの追加バッファとして、audioFrame以外を受けとりました。");
		}
		frameAppendFlag = true;
		if(frame == null) {
			// 空だったらそのまま追加
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			((AudioMultiFrame)frame).addFrame(tmpFrame);
		}
		else {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame(frame);
			if(tmpFrame instanceof AudioMultiFrame) {
				for(IAudioFrame aFrame : ((AudioMultiFrame) tmpFrame).getFrameList()) {
					multiFrame.addFrame(aFrame);
				}
			}
			else {
				multiFrame.addFrame(tmpFrame);
			}
			frame = multiFrame;
		}
		super.update();
	}
	/**
	 * mshであるかの確認
	 * @return
	 */
	public boolean isSequenceHeader() {
		return getCodec() == CodecType.AAC && sequenceHeaderFlag.get() == 0;
	}
	/**
	 * aacのmediaSequenceHeaderとして初期化します
	 * @param dsi
	 */
	public void setAacMediaSequenceHeader(AacFrame frame, DecoderSpecificInfo dsi) throws Exception {
		codecId.set(CodecType.getAudioCodecNum(CodecType.AAC));
		switch(frame.getChannel()) {
		case 1:
			channels.set(0);
			break;
		case 2:
			channels.set(1);
			break;
		default:
			throw new Exception("音声チャンネル数がflvに適合しないものでした。");
		}
		switch(frame.getBit()) {
		case 8:
			bitCount.set(0);
			break;
		case 16:
			bitCount.set(1);
			break;
		default:
			// bit深度情報はもっていないコンテナもあるみたいです。(というか基本的に圧縮データにbit深度という情報はないみたい。(復元したらどうなるか・・・の問題っぽい。))
			bitCount.set(1);
		}
		switch((int)(frame.getSampleRate() / 100)) {
		case 55:
			sampleRate.set(0);
			break;
		case 110:
			sampleRate.set(1);
			break;
		case 220:
			sampleRate.set(2);
			break;
		case 441:
			sampleRate.set(3);
			break;
		default:
			throw new Exception("frameRateが適合しないものでした。");
		}
		sequenceHeaderFlag = new Bit8(0);
		frameBuffer = dsi.getData();
		// サイズの計算が必要
		setPts((long)(1.0D * frame.getPts() / frame.getTimebase() * 1000));
		setTimebase(1000);
		setSize(11 + 1 + 1 + frameBuffer.remaining() + 4);
		super.update();
	}
	@Override
	public void setPts(long pts) {
		// ptsを設定する前にframeのptsを更新しておく。
		if(frame != null && frame instanceof AudioFrame) {
			AudioFrame aFrame = (AudioFrame)frame;
			aFrame.setPts(pts * aFrame.getTimebase() / 1000);
		}
		super.setPts(pts);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("AudioTag:");
		data.append(" timestamp:").append(getPts());
		data.append(" codec:").append(getCodec());
		try {
			int sampleRate = getSampleRate();
			data.append(" sampleRate:").append(sampleRate);
			int sampleNum = getSampleNum();
			data.append(" sampleNum:").append(sampleNum);
		}
		catch(Exception e) {
		}
		return data.toString();
	}
}
