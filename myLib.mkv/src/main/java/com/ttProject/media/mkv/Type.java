package com.ttProject.media.mkv;

/**
 * mkvのデータタイプ
 * @author taktod
 */
public enum Type {
	EBML(0x1A45DFA3),
		EBMLVersion(0x4286),
		EBMLReadVersion(0x42F7),
		EBMLMaxIDLength(0x42F2),
		EBMLMaxSizeLength(0x42F3),
		DocType(0x4282),
		DocTypeVersion(0x4287),
		DocTypeReadVersion(0x4285),
	Segment(0x18538067),
		SeekHead(0x114D9B74),
			Seek(0x4DBB),
				SeekID(0x53AB), //
				SeekPosition(0x53AC), //
		Info(0x1549A966),
			SegmentUID(0x73A4),
			SegmentFilename(0x7384), //
			PrevUID(0x3CB923), //
			PrevFilename(0x3C83AB), //
			NextUID(0x3EB923), //
			NextFilename(0x3E83BB), //
			SegmentFamily(0x4444), //
			ChapterTranslate(0x6924), //
				ChapterTranslateEditionUID(0x69FC), //
				ChapterTranslateCodec(0x69BF), //
				ChapterTranslateID(0x69A5), //
			TimecodeScale(0x2AD7B1),
			Duration(0x4489),
			DateUTC(0x4461), //
			Title(0x7BA9), //
			MuxingApp(0x4D80),
			WritingApp(0x5741),
		Cluster(0x1F43B675),
			Timecode(0xE7),
			SimpleBlock(0xA3),
		Tracks(0x1654AE6B),
			TrackEntry(0xAE),
				TrackNumber(0xD7),
				TrackUID(0x73C5),
				TrackType(0x83),
				FlagLacing(0x9C),
				DefaultDuration(0x23E383),
				Language(0x22B59C),
				CodecID(0x86),
				CodecPrivate(0x63A2),
				Video(0xE0),
					PixelWidth(0xB0),
					PixelHeight(0xBA),
					DisplayWidth(0x54B0),
					DisplayHeight(0x54BA),
					DisplayUnit(0x54B2),
				Audio(0xE1),
					SamplingFrequency(0xB5),
					Channels(0x9F),
					BitDepth(0x6264),
				
	Void(0xEC),
	CRC32(0xBF);
	private final int value;
	private Type(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
}
