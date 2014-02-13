package com.ttProject.frame.vorbis.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.vorbis.VorbisFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit48;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * vorbisのheaderフレーム
 * packetType: 1byte 0x01 identification header
 * string: 6Byte "vorbis"
 * vorbisVersion 32bit integer
 * audioChannels 8bit unsignedInteger
 * audioSampleRate 32bit integer
 * bitrateMaximum 32bit integer
 * bitrateNominal 32bit integer
 * bitrateMinimum 32bit integer
 * blockSize0 2^x 4bit unsigned integer(samples per frameがとれるっぽい)
 * blockSize1 2^x 4bit unsigned integer(不明)
 * framing flag 1bit(実際は1byteになってるっぽい)
 * 
 * @see http://www.xiph.org/vorbis/doc/Vorbis_I_spec.html#x1-620004.2.2
 * @author taktod
 * 
 * サンプルデータ
 * 01 76 6F 72 62 69 73 00 00 00 00 02 44 AC 00 00 FF FF FF FF 00 77 01 00 FF FF FF FF B8 01
 * 
 * どうやらsampleNumもここの値で決定されるみたいです。
 * はじめのframeは(blockSize0 + blockSize1) / 4
 * それ以降のframeは(blockSize1 + blockSize1) / 4
 * になっている模様
 */
public class IdentificationHeaderFrame extends VorbisFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(IdentificationHeaderFrame.class);
	private Bit8  packetType      = new Bit8();
	private Bit48 string          = new Bit48();
	private Bit32 vorbisVersion   = new Bit32();
	private Bit8  audioChannels   = new Bit8();
	private Bit32 audioSampleRate = new Bit32();
	private Bit32 bitrateMaximum  = new Bit32();
	private Bit32 bitrateNormal   = new Bit32();
	private Bit32 bitrateMinimum  = new Bit32();
	private Bit4  blockSize0      = new Bit4();
	private Bit4  blockSize1      = new Bit4();
	private Bit1  framingFlag     = new Bit1();
	private Bit   extraBit = null;

	private boolean isFirstPassed = false;
	private int blockSize0Value = 0;
	private int blockSize1Value = 0;
	
	private CommentHeaderFrame commentHeaderFrame = null;
	private SetupHeaderFrame   setupHeaderFrame   = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.setLittleEndianFlg(true);
		loader.load(packetType, string, vorbisVersion,
				audioChannels, audioSampleRate,
				bitrateMaximum, bitrateNormal, bitrateMinimum,
				blockSize0, blockSize1, framingFlag);
		extraBit = loader.getExtraBit();
		// データの確認
		if(packetType.get() != 1) {
			throw new Exception("packetTypeが不正です。");
		}
		if(string.getLong() != 0x736962726F76L) {
			throw new Exception("string文字列が不正です。");
		}
		blockSize0Value = (1 << blockSize0.get());
		blockSize1Value = (1 << blockSize1.get());
		super.setSize(channel.size());
		super.setBit(64);
		super.setChannel(audioChannels.get());
		super.setSampleRate(audioSampleRate.get());
		logger.info(getSize());
		logger.info(getChannel());
		logger.info(getSampleRate());
		logger.info(blockSize0Value);
		logger.info(blockSize1Value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSampleNum() {
		if(!isFirstPassed) {
			isFirstPassed = true;
			return (blockSize0Value + blockSize1Value) / 4;
		}
		else {
			return (blockSize1Value + blockSize1Value) / 4;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() {
		return null;
	}
	/**
	 * CommentHeaderFrame設定
	 * @param frame
	 */
	public void setCommentHeaderFrame(CommentHeaderFrame frame) {
		this.commentHeaderFrame = frame;
	}
	/**
	 * SetupHeaderFrame設定
	 * @param frame
	 */
	public void setSetupHeaderFrame(SetupHeaderFrame frame) {
		this.setupHeaderFrame = frame;
	}
}
