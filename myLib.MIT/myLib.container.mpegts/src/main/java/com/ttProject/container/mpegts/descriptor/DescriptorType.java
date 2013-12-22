package com.ttProject.container.mpegts.descriptor;

public enum DescriptorType {
	// 以下mpeg transport stream descriptorsより
	// @see http://www.etherguidesystems.com/help/sdos/mpeg/semantics/mpeg-2/descriptors/Default.aspx
	// 0x00 0x01 reserved
	video_stream_descriptor(0x02),
	audio_stream_descriptor(0x03),
	hierarchy_descriptor(0x04),
	registration_descriptor(0x05),
	data_stream_alignment_descriptor(0x06),
	target_background_grid_descriptor(0x07),
	video_window_descriptor(0x08),
	CA_descriptor(0x09),
	ISO_639_language_descriptor(0x0A),
	system_clock_descriptor(0x0B),
	multiplex_buffer_utilization_descriptor(0x0C),
	copyright_descriptor(0x0D),
	maximum_bitrate_descriptor(0x0E),
	private_date_indicator_descriptor(0x0F),
	smoothing_buffer_descriptor(0x10),
	STD_descriptor(0x11),
	IBP_descriptor(0x12),
	// 0x13 - 0x1A reserved(たぶん)
	MPEG_4_video_descriptor(0x1B),
	MPEG_4_audio_descriptor(0x1C),
	IOD_descriptor(0x1D),
	SL_descriptor(0x1E),
	FMC_descriptor(0x1F),
	External_ES_ID_descriptor(0x20),
	MuxCode_descriptor(0x21),
	FmxBufferSize_descriptor(0x22),
	MultiplexBuffer_descriptor(0x23),
	FlexMuxTiming_descriptor(0x24),
	// 0x13 - 0x3F reserved(たぶん)
	// mpeg transport stream descriptorsからすると以下はuser privateらしい。
	// 以下sdtInformationだと思う。
	network_name_descriptor(0x40),
	service_list_descriptor(0x41),
	stuffing_descriptor(0x42),
	satellite_delivery_system_descriptor(0x43),
	cable_delivery_system_descriptor(0x44),
	VBI_data_descriptor(0x45),
	VBI_teletext_descriptor(0x46),
	bouquet_name_descriptor(0x47),
	service_descriptor(0x48),
	country_availability_descriptor(0x49),
	linkage_descriptor(0x4A),
	NVOD_reference_descriptor(0x4B),
	time_shifted_service_descriptor(0x4C),
	short_event_descriptor(0x4D),
	extended_event_descriptor(0x4E),
	time_shifted_event_descriptor(0x4F),
	component_descriptor(0x50),
	mosaic_descriptor(0x51),
	stream_identifier_descriptor(0x52),
	CA_identifier_descriptor(0x53),
	content_descriptor(0x54),
	parental_rating_descriptor(0x55),
	teletext_descriptor(0x56),
	telephone_descriptor(0x57),
	local_time_offset_descriptor(0x58),
	subtitling_descriptor(0x59),
	terrestrial_delivery_system_descriptor(0x5A),
	multilingual_network_name_descriptor(0x5B),
	multilingual_bouquet_name_descriptor(0x5C),
	multilingual_service_name_descriptor(0x5D),
	multilingual_component_descriptor(0x5E),
	private_data_specifier_descriptor(0x5F),
	service_move_descriptor(0x60),
	short_smoothing_buffer_descriptor(0x61),
	frequency_list_descriptor(0x62),
	partial_transport_stream_descriptor(0x63),
	data_broadcast_descriptor(0x64),
	scrambling_descriptor(0x65),
	data_broadcast_id_descriptor(0x66),
	transport_stream_descriptor(0x67),
	DSNG_descriptor(0x68),
	PDC_descriptor(0x69),
	AC3_descriptor(0x6A),
	ancillary_data_descriptor(0x6B),
	cell_list_descriptor(0x6C),
	cell_frequency_link_descriptor(0x6D),
	announcement_support_descriptor(0x6E),
	application_signalling_descriptor(0x6F),
	adaptation_field_data_descriptor(0x70),
	service_identifier_descriptor(0x71),
	service_availability_descriptor(0x72),
	default_authority_descriptor(0x73),
	related_content_descriptor(0x74),
	TVA_id_descriptor(0x75),
	content_identifier_descriptor(0x76),
	time_slice_fec_identifier_descriptor(0x77),
	ECM_repetition_rate_descriptor(0x78),
	S2_satellite_delivery_system_descriptor(0x79),
	enhanced_AC3_descriptor(0x7A),
	DTS_descriptor(0x7B),
	AAC_descriptor(0x7C),
	XAIT_location_descriptor(0x7D),
	FTA_content_management_descriptor(0x7E),
	extension_descriptor(0x7F),
	forbidden(0xFF);
	private final int value;
	private DescriptorType(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static DescriptorType getType(int value) throws Exception {
		for(DescriptorType t : values()) {
			if(t.intValue() == value) {
				return t;
			}
		}
		throw new Exception("例外が発生しました。");
	}

}
