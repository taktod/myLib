package com.ttProject.media.mpegts;

/**
 * mpegtsのtableIDの指定データ(tableSignatureともいっていた。)
 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
 * @author taktod
 */
public enum TableId {
	/*
	 * @see http://pda.etsi.org/exchangefolder/en_300468v011301p.pdf
	 * テーブルシグネチャのメモ(table_idとなっている)
	 * 0x00:ProgramAssociationSection
	 * 0x01:ConditionalAccessSection
	 * 0x02:ProgramMapSection
	 * 0x03:TransportStreamDescriptionSection
	 * 0x04～0x3F:reserved
	 * 0x40:NetworkInformationSection(actual_network)
	 * 0x41:NetworkInformationSection(other network)
	 * 0x42:ServiceDescriptionSection(actualTransportStream)
	 * 0x43～0x45:reserved for future use
	 * 0x46:ServiceDescriptionSection(otherTransportStream)
	 * 0x47～0x49:reserved for future use
	 * 0x4A:BouquetAssociationSection
	 * 0x4B～0x4D:reserved for future use
	 * 0x4E:EventInformationSection(actualTransportStream,presend/following)
	 * 0x4F:EventInformationSection(otherTransportStream,presend/following)
	 * 0x50～0x5F:EventInformationSection(actualTransportStream,schedule)
	 * 0x60～0x6F:EventInformationSection(otherTransportStream,schedule)
	 * 0x70:TimeDateSection
	 * 0x71:RunningStatusSection
	 * 0x72:StuffingSection
	 * 0x73:TimeOffsetSection
	 * 0x74:ApplicationInformationSection
	 * 0x75:ContainerSection
	 * 0x76:RelatedContentSection
	 * 0x77:ContentIdentifierSection
	 * 0x78:MPE-FEC section
	 * 0x79:ResolutionNotificationSection
	 * 0x7A:MPE-IFEC section
	 * 0x7B～0x7D:reserved for future use
	 * 0x7E:DiscontinuityInformationSection
	 * 0x7F:SelectionInformationSection
	 * 0x80～0xFE:userDefined
	 * 0xFF:Reserved
	 */
	ProgramAssociationSection(0x00),
	ConditionalAccessSection(0x01),
	ProgramMapSection(0x02),
	TransportStreamDescriptionSection(0x03),
	NetworkInformationSection(0x40),
	NetworkInformationSection_oth(0x41),
	ServiceDescriptionSection(0x42),
	ServiceDescriptionSection_oth(0x46),
	BouquetAssociationSection(0x4A),
	EventInformationSection(0x4E),
	EventInformationSection_oth(0x4F),
	TimeDateSection(0x70),
	RunningStatusSection(0x71),
	StuffingSection(0x72),
	TimeOffsetSection(0x73),
	ApplicationInformationSection(0x74),
	ContainerSection(0x75),
	RelatedContentSection(0x76),
	ContentIdentifierSection(0x77),
	MPE_FECSection(0x78),
	ResolutionNotificationSection(0x79),
	MPE_IFECSection(0x7A),
	DiscontinuityInformationSection(0x7E),
	SelectionInformationSection(0x7F);
	private final int value;
	private TableId(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static TableId getSection(int value) throws Exception {
		for(TableId s : values()) {
			if(s.intValue() == value) {
				return s;
			}
		}
		throw new Exception("解析不能なsectionをうけとりました。");
	}
}
