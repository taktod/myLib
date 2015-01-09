/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.MpegtsPacket;
import com.ttProject.container.mpegts.field.AdaptationField;
import com.ttProject.container.mpegts.field.DtsField;
import com.ttProject.container.mpegts.field.PtsField;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.frame.h264.type.AccessUnitDelimiter;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit13;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * ElementaryStreamPacket
 * (packetizedElementaryStream Packet)
 * 
 * 47 [abcd dddd] [dddd dddd] [eefg hhhh]
 * a:error indicator
 * b:payload unit start indicator
 * c:transport priority indicator
 * e:scranblingControl
 * f:has adaptation field
 * g:has payload data
 * h:continuty count
 * 
 * adaptationFieldをもっている場合
 * 次の1バイトがadaptation fieldの長さっぽい。
 * flagsとかがはいっているけど、とりあえずいらないので、捨てればよさそう。
 * 
 * payloadDataがはいっているものが、メディアデータとして必要なもの。
 * payLoadUnitStartIndicatorのフラグが立っている場合
 *  h.264の場合は、先頭の4バイトが0x000001E0になっている。
 *  次の3バイトにpacketLengthとmarkerBitがはいっている。(とりあえず無視してる)
 *  次の2ビットがPTSフラグとDTSフラグ(両方ないと、処理できないとする。)
 *  とりあえず、PTSは必須、DTSはなくてもなんとかなるっぽい。
 * 47 41 00 30 07 50 00 00 6F 51 7E 00 00 00 01 E0 06 01 80 C0 0A 31 00 07 D8 61 11 00 07 A9 75
 * 
 * @see http://en.wikipedia.org/wiki/Packetized_elementary_stream
 * @see http://en.wikipedia.org/wiki/Elementary_stream
 * 
 * getBuffer()を実行すると、前から順にpacketデータが取り出せていって・・・というのがよさそうです。
 * →getBufferで自動的に次のデータとしておくとファイルから読み取った場合とかの処理が詰まってしまうので、変更する必要あり。
 * よって次のようにしたいとおもいます。
 * 連続するデータの場合はnextPesというメソッドを作成して、次のpesデータがある場合は、それを応答する形にしておく。
 * 
 * データの読み込み時の動作を改良する必要がある。
 * 現状では、対象パケットのデータを読み込んでおわりだが、変更したら、読み込みデータから本当のデータ量を取り出す必要がでてくる。
 * 
 * @see http://ameblo.jp/sogoh/entry-10560067493.html
 * 
 * @author taktod
 * 
 * frameの追加はいけるけど、requestUpdateが長すぎるので、調整しておきたい。
 */
public class Pes extends MpegtsPacket {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Pes.class);
	private Bit24 prefix = new Bit24(1); // 0x000001 fixed
	private Bit8  streamId = new Bit8(); // audio:0xC0 - 0xDF video:0xE0 - 0xEF (output from vlc is out of this order.)
	private Bit16 pesPacketLength = new Bit16();
	private Bit2  markerBits = new Bit2(2); // 10 fixed
	private Bit2  scramblingControl = new Bit2(); // 00
	private Bit1  priority = new Bit1(); // 0
	private Bit1  dataAlignmentIndicator = new Bit1(); // 0
	private Bit1  copyright = new Bit1(); // 0
	private Bit1  originFlg = new Bit1(); // 0:original 1:copy

	private Bit2 ptsDtsIndicator = new Bit2(); // from wiki, 11 for both, 01 for pts only. however, I think 10 for pts only
	private Bit1 escrFlag = new Bit1(); // 0
	private Bit1 esRateFlag = new Bit1(); // 0
	private Bit1 DSMTrickModeFlag = new Bit1(); // 0
	private Bit1 additionalCopyInfoFlag = new Bit1(); // 0
	private Bit1 CRCFlag = new Bit1(); // 0
	private Bit1 extensionFlag = new Bit1(); // 0

	private Bit8 PESHeaderLength = new Bit8(); // 10byte?
	private PtsField pts = null;
	private DtsField dts = null;
	
	/** data size on thie pes */
	private int pesDeltaSize = 0;

	/** pes with unit start flag, (this will hold frames.) */
	private Pes unitStartPes = null;
	private IFrame frame = null;
	private List<ByteBuffer> pesBufferList = null;
	private IAnalyzer frameAnalyzer = null;
	private boolean pcrFlag;
	/**
	 * constructor
	 * @param syncByte
	 * @param transportErrorIndicator
	 * @param payloadUnitStartIndicator
	 * @param transportPriority
	 * @param pid
	 * @param scramblingControl
	 * @param adaptationFieldExist
	 * @param payloadFieldExist
	 * @param continuityCounter
	 * @param pcrFlag
	 */
	public Pes(Bit8 syncByte, Bit1 transportErrorIndicator,
			Bit1 payloadUnitStartIndicator, Bit1 transportPriority,
			Bit13 pid, Bit2 scramblingControl, Bit1 adaptationFieldExist,
			Bit1 payloadFieldExist, Bit4 continuityCounter, boolean isPcr) {
		super(syncByte, transportErrorIndicator, payloadUnitStartIndicator,
				transportPriority, pid, scramblingControl, adaptationFieldExist,
				payloadFieldExist, continuityCounter);
		this.pcrFlag = isPcr;
	}
	/**
	 * constructor
	 */
	public Pes(int pid, boolean isPcr) {
		// 基本unitStartにしておきます。
		super(new Bit8(0x47), new Bit1(), new Bit1(1), new Bit1(),
				new Bit13(pid), new Bit2(), new Bit1(0), new Bit1(1), new Bit4());
		unitStartPes = this;
		this.pcrFlag = isPcr;
	}
	/**
	 * hold the unit start pes
	 * @param pes
	 */
	public void setUnitStartPes(Pes pes) {
		unitStartPes = pes;
	}
	public void setFrameAnalyzer(IAnalyzer analyzer) {
		frameAnalyzer = analyzer;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		int startPos = channel.position();
		super.minimumLoad(channel);
		if(isPayloadUnitStart()) {
			// payloadUnitStarrt information.
			BitLoader loader = new BitLoader(channel);
			loader.load(prefix, streamId, pesPacketLength, markerBits,
					scramblingControl, priority, dataAlignmentIndicator,
					copyright, originFlg, ptsDtsIndicator, escrFlag,
					esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
					CRCFlag, extensionFlag, PESHeaderLength);
			// BufferList for frame loading.
			pesBufferList = new ArrayList<ByteBuffer>();
			int length = PESHeaderLength.get();
			switch(ptsDtsIndicator.get()) {
			case 0x03:
				pts = new PtsField();
				dts = new DtsField();
				pts.load(channel);
				dts.load(channel);
				length -= 10;
				break;
			case 0x02:
				pts = new PtsField();
				pts.load(channel);
				length -= 5;
				break;
			case 0x00:
				break;
			default:
				throw new Exception("ptsDtsIndicator is invalid");
			}
			if(escrFlag.get() != 0x00) {
				throw new Exception("escrFlag analyzation is not suppoted yet.");
			}
			if(esRateFlag.get() != 0x00) {
				throw new Exception("esRateFlag analyzation is not supported yet.");
			}
			if(DSMTrickModeFlag.get() != 0x00) {
				throw new Exception("DSMTrickModeFlag analyzation is not supported yet.");
			}
			if(additionalCopyInfoFlag.get() != 0x00) {
				throw new Exception("additionalCopyInfoFlag analyzation is not supported yet.");
			}
			if(CRCFlag.get() != 0x00) {
				throw new Exception("CRCFlag analyzation is not supported yet.");
			}
			if(extensionFlag.get() != 0x00) {
				throw new Exception("extensionFlag analyzation is not supported yet.");
			}
			if(length != 0) {
				throw new Exception("there are some unloaded data.");
			}
			unitStartPes = this;
		}
		pesDeltaSize = 184 - (channel.position() - startPos);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		// load frame data fill be store in unitStartPes.
		// TODO in the case of segmented mpegts file, unitStartPes can be missing.
		// then got NullPointerException.
		unitStartPes.pesBufferList.add(BufferUtil.safeRead(channel, pesDeltaSize));
	}
	/**
	 * analyze frame.
	 */
	public void analyzeFrame() throws Exception {
		if(unitStartPes.frameAnalyzer != null) {
			IReadChannel pesBufferChannel = new ByteReadChannel(BufferUtil.connect(unitStartPes.pesBufferList));
			IFrame frame = null;
			long audioSampleNum = 0;
			while((frame = unitStartPes.frameAnalyzer.analyze(pesBufferChannel)) != null) {
				if(frame instanceof NullFrame) {
					continue;
				}
				if(frame instanceof VideoFrame) {
					if(!(frame instanceof SliceFrame)) { // for h264, need only sliceFrame.
						continue;
					}
					VideoFrame vFrame = (VideoFrame)frame;
					vFrame.setPts(unitStartPes.pts.getPts());
					vFrame.setTimebase(90000);
				}
				else if(frame instanceof AudioFrame) {
					AudioFrame aFrame = (AudioFrame)frame;
					aFrame.setPts(unitStartPes.pts.getPts() + audioSampleNum * 90000 / aFrame.getSampleRate());
					aFrame.setTimebase(90000);
					audioSampleNum += aFrame.getSampleNum();
				}
				unitStartPes.addFrame(frame);
			}
		}
	}
	/**
	 * ref frame.
	 * @return
	 */
	public IFrame getFrame() {
		return unitStartPes.frame;
	}
	/**
	 * add frame.
	 * @param tmpFrame
	 */
	public void addFrame(IFrame tmpFrame) throws Exception {
		if(tmpFrame == null) {
			return;
		}
		if(frame == null) {
			frame = tmpFrame;
		}
		else if(frame instanceof AudioMultiFrame) {
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("try to add non-audioFrame for audioMultiFrame.");
			}
			((AudioMultiFrame)frame).addFrame((IAudioFrame)tmpFrame);
		}
		else if(frame instanceof VideoMultiFrame) {
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("try to add non-videoFrame for videoMultiFrame.:" + tmpFrame);
			}
			((VideoMultiFrame)frame).addFrame((IVideoFrame)tmpFrame);
		}
		else if(frame instanceof IAudioFrame) {
			AudioMultiFrame multiFrame = new AudioMultiFrame();
			multiFrame.addFrame((IAudioFrame)frame);
			if(!(tmpFrame instanceof IAudioFrame)) {
				throw new Exception("try to add non-audioFrame for audioFrame.");
			}
			multiFrame.addFrame((IAudioFrame)tmpFrame);
			frame = multiFrame;
		}
		else if(frame instanceof IVideoFrame) {
			VideoMultiFrame multiFrame = new VideoMultiFrame();
			multiFrame.addFrame((IVideoFrame)frame);
			if(!(tmpFrame instanceof IVideoFrame)) {
				throw new Exception("try to add non-videoFrame for videoFrame.");
			}
			multiFrame.addFrame((IVideoFrame)tmpFrame);
			frame = multiFrame;
		}
		else {
			throw new Exception("unexpected frame is found.");
		}
		super.update();
	}
	@Override
	protected void requestUpdate() throws Exception {
		if(frame == null) {
			throw new Exception("frame data is not defined.");
		}
		if(!isPayloadUnitStart()) {
			throw new Exception("data have to get from unitStart.");
		}
		// update information of time.
		setupTime();
		// get frame data.
		ByteBuffer frameBuffer = frameBuffer();
		// update pesPacketLength
		if(3 + PESHeaderLength.get() + frameBuffer.remaining() > 0x010000) {
			pesPacketLength.set(0);
		}
		else {
			pesPacketLength.set(3 + PESHeaderLength.get() + frameBuffer.remaining());
		}

		// need to know the header size.
		/*
		 * first 4byte
		 * if adaptationField exists, getLength
		 * 9 + PESHeaderLength.get()
		 * this is the length.
		 */
		// adaptationField data missing the adaptationFieldSize(1byte), so + 1;
		int headerLength = (isAdaptationFieldExist() ? getAdaptationField().getLength() + 1 : 0) + 9 + PESHeaderLength.get();
		// calcurate necessary size.
		ByteBuffer pesChunk = ByteBuffer.allocate((int)(Math.ceil((frameBuffer.remaining() + headerLength) / 184f) * 188));
		if(headerLength + frameBuffer.remaining() < 184) {
			// fit to 1 packet.
			// fill zero with adaptation field.
			setAdaptationFieldExist(1); // adaptationField will be active.
			AdaptationField aField = getAdaptationField();
			aField.setLength(aField.getLength() + 183 - (headerLength + frameBuffer.remaining()));
			pesChunk.put(getHeaderBuffer());
			BitConnector connector = new BitConnector();
			connector.feed(prefix, streamId, pesPacketLength, markerBits,
					scramblingControl, priority, dataAlignmentIndicator,
					copyright, originFlg, ptsDtsIndicator, escrFlag,
					esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
					CRCFlag, extensionFlag, PESHeaderLength);
			switch(ptsDtsIndicator.get()) {
			case 0:
				break;
			case 2:
				connector.feed(pts.getBits());
				break;
			case 3:
				connector.feed(pts.getBits());
				connector.feed(dts.getBits());
				break;
			default:
			}
			pesChunk.put(connector.connect());
			pesChunk.put(frameBuffer);
			pesChunk.flip();
			super.setData(pesChunk);
			return;
		}
		// calcurate data size.
		// firstly, read header 4byte and adaptation Field.
		pesChunk.put(getHeaderBuffer());
		BitConnector connector = new BitConnector();
		connector.feed(prefix, streamId, pesPacketLength, markerBits,
				scramblingControl, priority, dataAlignmentIndicator,
				copyright, originFlg, ptsDtsIndicator, escrFlag,
				esRateFlag, DSMTrickModeFlag, additionalCopyInfoFlag,
				CRCFlag, extensionFlag, PESHeaderLength);
		switch(ptsDtsIndicator.get()) {
		case 2: // pts only
			connector.feed(pts.getBits());
			break;
		case 3: // pts and dts.
			connector.feed(pts.getBits());
			connector.feed(dts.getBits());
			break;
		default:
			break;
		}
		pesChunk.put(connector.connect());
		byte[] data = new byte[(188 - (pesChunk.position() % 188))];
		frameBuffer.get(data);
		pesChunk.put(data);
		// nextData.
		// adaptationField - off
		AdaptationField aField = getAdaptationField();
		aField.setPcrFlag(0);
		aField.setRandomAccessIndicator(0);
		setAdaptationFieldExist(0);
		// payloadUnitStart - no
		setPayloadUnitStart(0);
		// write pes units.
		while(frameBuffer.remaining() > 0) {
			setContinuityCounter(getContinuityCounter() + 1);
			if(frameBuffer.remaining() < 184) {
				setAdaptationFieldExist(1);
				getAdaptationField().setLength(183 - frameBuffer.remaining());
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[frameBuffer.remaining()];
				frameBuffer.get(data);
				pesChunk.put(data);
				break;
			}
			else {
				pesChunk.put(getHeaderBuffer().array());
				data = new byte[184];
			}
			frameBuffer.get(data);
			pesChunk.put(data);
		}
		pesChunk.flip();
		setData(pesChunk);
	}
	/**
	 * ref frameBuffer
	 * @return
	 * @throws Exception
	 */
	private ByteBuffer frameBuffer() throws Exception {
		ByteBuffer frameBuffer = null;
		if(frame instanceof IAudioFrame) {
			// audioFrame
			frameBuffer = ByteBuffer.allocate(frame.getSize());
			if(frame instanceof AudioMultiFrame) {
				// multiFrame need to be connect.
				for(IAudioFrame audioFrame : ((AudioMultiFrame)frame).getFrameList()) {
					if(audioFrame.getData() == null) { // XXX sometimes audioFrame body data is null. program error?
						continue;
					}
					frameBuffer.put(audioFrame.getData());
				}
			}
			else {
				// in single frame case, just add itself.
				frameBuffer.put(frame.getData());
			}
			frameBuffer.flip();
			return frameBuffer;
		}
		else if(frame instanceof IVideoFrame) {
			// videoFrame
			if(frame instanceof H264Frame) {
				// aud + sps + pps + sliceIDR
				// aud + slice
				List<ByteBuffer> bufferList = new ArrayList<ByteBuffer>();
				BitConnector connector = new BitConnector();
				bufferList.add(connector.connect(new Bit32(1)));
				bufferList.add(new AccessUnitDelimiter().getData());
				bufferList.add(frame.getPackBuffer());
				return BufferUtil.connect(bufferList);
			}
			else if(frame instanceof VideoMultiFrame) {
				throw new Exception("multiFrame is unexpect here.。");
			}
			else {
				throw new Exception("non-h264 data, under construction, I need sample.:" + frame);
			}
		}
		else {
			throw new Exception("pes have neither audio nor video.:" + frame);
		}
	}
	/**
	 * setup time setting.
	 */
	private void setupTime() throws Exception {
		if(frame instanceof IAudioFrame) {
			// pts only
			setPts(frame.getPts(), frame.getTimebase());
			ptsDtsIndicator.set(2);
			PESHeaderLength.set(5);
			if(pcrFlag) {
				// if pcr Data, set random access flg, pcrFlg.
				setAdaptationFieldExist(1);
				AdaptationField aField = getAdaptationField();
				aField.setRandomAccessIndicator(1);
				aField.setPcrFlag(1);
				aField.setPcrBase((long)(1.0D * frame.getPts() / frame.getTimebase() * 90000));
				// 63000
			}
		}
		else if(frame instanceof IVideoFrame) {
			// pts
			setPts(frame.getPts(), frame.getTimebase());
			// video can have dts information.
			IVideoFrame vFrame = (IVideoFrame)frame;
			if(vFrame.getDts() != -1 && vFrame.getDts() != 0) {
				// write dts.
				setDts(vFrame.getDts(), vFrame.getTimebase());
				ptsDtsIndicator.set(3);
				PESHeaderLength.set(10);
			}
			else {
				ptsDtsIndicator.set(2);
				PESHeaderLength.set(5);
			}
			// for keyFrame, set adaptation field to indicate pcr and randomAccess.
			if(vFrame.isKeyFrame() && pcrFlag) {
				setAdaptationFieldExist(1);
				AdaptationField aField = getAdaptationField();
				aField.setRandomAccessIndicator(1);
				aField.setPcrFlag(1);
				aField.setPcrBase((long)(1.0D * frame.getPts() / frame.getTimebase() * 90000));
			}
		}
		else {
			throw new Exception("frame have neithor video nor audio.:" + frame);
		}
	}
	private void setPts(long timestamp, long timebase) {
		pts = new PtsField();
		pts.setPts((long)(1.0D * timestamp / timebase * 90000));
	}
	private void setDts(long timestamp, long timebase) {
		dts = new DtsField();
		dts.setDts((long)(1.0D * timestamp / timebase * 90000));
	}
	public void setStreamId(int id) {
		streamId.set(id);
	}
	public int getStreamId() {
		return streamId.get();
	}
}
