package com.ttProject.container.mp4.esds;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit32;
import com.ttProject.unit.extra.bit.Bit8;

public class DecoderConfig extends Tag {
	/** ロガー */
	private Logger logger = Logger.getLogger(DecoderConfig.class);
	private Bit8  objectType = new Bit8();
	private Bit8  flags      = new Bit8();
	private Bit24 bufferSize = new Bit24();
	private Bit32 maxBitRate = new Bit32();
	private Bit32 avgBitRate = new Bit32();
	private Tag   decoderSpecific = null;
	public static enum ObjectType {
		SystemV1(0x01),
		SystemV2(0x02),
		Mpeg4Video(0x20),
		Mpeg4AvcSps(0x21),
		Mpeg4AvcPps(0x22),
		Mpeg4Audio(0x40), // aac?
		Mpeg2SimpleVideo(0x60),
		Mpeg2MainVideo(0x61),
		Mpeg2SnrVideo(0x62),
		Mpeg2SpecialVideo(0x63),
		Mpeg2HighVideo(0x64),
		Mpeg2_422Video(0x65),
		Mpeg4AdtsMain(0x66),
		Mpeg4AdtsLowComplexity(0x67),
		Mpeg4AdtsScalableSamplingRate(0x68),
		Mpeg2Adts(0x69),
		Mpeg1Video(0x6A),
		Mpeg1Adts(0x6B),
		JpegVideo(0x6C),
		PrivateAudio(0xC0),
		PrivateVideo(0xD0),
		PcmLeAudio16Bit(0xE0),
		VorbisAudio(0xE1),
		DolbyV3Ac3Audio(0xE2),
		AlowAudio(0xE3),
		MulawAudio(0xE4),
		AdpcmAudio(0xE5),
		PcmBigEndianAudio16Bit(0xE6),
		YCbCr420Video(0xF0), // YV12 video
		H264Video(0xF1),
		H263Video(0xF2),
		H261Video(0xF3);
		private final int value;
		private ObjectType(int value) {
			this.value = value;
		}
		public int intValue() {
			return value;
		}
		public static ObjectType getType(Bit8 tag) throws Exception {
			for(ObjectType t : values()) {
				if(t.intValue() == tag.get()) {
					return t;
				}
			}
			throw new Exception("未定義のtypeが応答されました。");
		}
	}
	/**
	 * コンストラクタ
	 * @param tag
	 */
	public DecoderConfig(Bit8 tag) {
		super(tag);
	}
	public DecoderConfig() {
		super(new Bit8(0x04));
	}
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
		BitLoader loader = new BitLoader(channel);
		Bit8 tag = new Bit8();
		loader.load(objectType, flags, bufferSize, maxBitRate, avgBitRate, tag);
		// DecoderSpecificのデータを読み込む
		switch(TagType.getType(tag)) {
		case DecoderSpecific:
			decoderSpecific = new DecoderSpecific(tag);
			break;
		default:
			throw new Exception("DecoderConfigが保持しないと思われるデータが取得されました。");
		}
		decoderSpecific.minimumLoad(channel);
		logger.info(decoderSpecific);
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
