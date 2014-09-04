/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.Lacing;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.frame.vp8.Vp8Frame;
import com.ttProject.frame.vp9.Vp9Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.EbmlValue;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.util.BufferUtil;

/**
 * SimpleBlockタグ
 * データは次のようになっています。
 * A3 44 B4 81 00 00 80 00 00 02 6C ...
 *  A3[SimpleBlockタグ]
 *  44 B4[このTagのサイズデータ]
 * ここまでは読み込み動作実装済み
 *  81[EbmlValue] 動作トラックデータ
 *  00 00[16bit固定]このCluster上でのtimestamp差分量
 *  1000 0000
 *  . keyFrameであるか指定
 *   ... reserved0設定
 *       . 非表示フレームであるか？ 1なら非表示
 *        .. lacing設定(データが複数のフレームの塊の場合にどのようにわかれるかの指定がはいっている*1)
 *          . discardable:なんだろう？
 * *1:h264のnalはフレームの塊ではあるけど、lacingではなくnal構造で分かれるようになっています。
 * @see http://matroska.org/technical/specs/index.html#simpleblock_structure
 * @author taktod
 */
public class SimpleBlock extends MkvBlockTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SimpleBlock.class);
	private Bit1 keyFrameFlag       = new Bit1();
	private Bit3 reserved           = new Bit3();
	private Bit1 invisibleFrameFlag = new Bit1();
	private Bit2 lacing             = new Bit2();
	private Bit1 discardableFlag    = new Bit1();
	/**
	 * コンストラクタ
	 * @param size
	 */
	public SimpleBlock(EbmlValue size) {
		super(Type.SimpleBlock, size);
	}
	/**
	 * コンストラクタ
	 */
	public SimpleBlock() {
		this(new EbmlValue());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		loader.load(keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Lacing getLacingType() throws Exception {
		return Lacing.getType(lacing.get());
	}
	public boolean isKeyFrame() {
		return keyFrameFlag.get() == 1;
	}
	public boolean isInvisibleFrame() {
		return invisibleFrameFlag.get() == 1;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getRemainedSize() {
		return getMkvSize() - (getTrackId().getBitCount() + 24) / 8;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// ここでframeを参照しながら必要な形にくみ上げ直すことができたらそれでOK
		BitConnector connector = new BitConnector();
		ByteBuffer buffer = connector.connect(getTrackId(), getTimestampDiff(),
				keyFrameFlag, reserved, invisibleFrameFlag, lacing, discardableFlag);
		// このあとはframeの実データがはいっている。// h264の場合はdataNalの形ではいっている
		IFrame frame = getFrame();
		switch(frame.getCodecType()) {
		case AAC:
			AacFrame aacFrame = (AacFrame)frame;
			buffer = BufferUtil.connect(buffer, aacFrame.getBuffer());
			break;
		case H264:
			// dataNalの形で応答しないとだめ
			if(frame instanceof SliceFrame) {
				SliceFrame sliceFrame = (SliceFrame) frame;
				buffer = BufferUtil.connect(buffer, sliceFrame.getDataPackBuffer());
			}
			else {
				throw new Exception("slice以外のh264データについて、データ化しようとしました。");
			}
			break;
//		case H265:
		default:
			buffer = BufferUtil.connect(buffer, frame.getData());
			break;
		}
		getTagSize().set(buffer.remaining());
		buffer = BufferUtil.connect(connector.connect(getTagId(), getTagSize()), buffer);
		setSize(buffer.remaining());
		setData(buffer);
	}
	/**
	 * フレームを追加する
	 * @param trackId
	 * @param frame
	 */
	public void addFrame(int trackId, IFrame frame, long clusterTimestamp) throws Exception {
		// とりあえずつくるか・・・
		super.getTrackId().set(trackId);
		super.addFrame(frame);
		// trackIdとframeをいれた。
		// trackEntryの開始位置のtimediffについて、参照する必要あり。
		// mkvはいまのところtimebase = 1000で動作しているものとするので、ptsを変換しておく
		long timestampDiff = frame.getPts() * 1000L / frame.getTimebase();
		getTimestampDiff().set((int)(timestampDiff - clusterTimestamp));
		// 音声はkeyFrame扱いっぽい。
		if(frame instanceof IAudioFrame) {
			keyFrameFlag.set(1);
		}
		else if(frame instanceof IVideoFrame) {
			IVideoFrame vFrame = (IVideoFrame)frame;
			// 映像はframeによってかわるっぽい
			if(vFrame.isKeyFrame()) {
				keyFrameFlag.set(1);
			}
			else {
				keyFrameFlag.set(0);
			}
			switch(frame.getCodecType()) {
			case VP8:
				@SuppressWarnings("unused")
				Vp8Frame vp8Frame = (Vp8Frame)frame;
				// invisibleであるか判定
				break;
			case VP9:
				@SuppressWarnings("unused")
				Vp9Frame vp9Frame = (Vp9Frame)frame;
				// invisibleであるか判定
				break;
			default:
				break;
			}
		}
		// lacingについては、サンプルのデータでmp3の複数フレームを混入したものがあったが、別に単体でも問題ないことがわかったので、とりあえず、lacingは考えないことにする
		super.update();
	}
}
