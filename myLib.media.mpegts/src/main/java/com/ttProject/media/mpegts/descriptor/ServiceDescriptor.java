package com.ttProject.media.mpegts.descriptor;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit8;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * SDT等に乗っているserviceDescriptorの定義
 * @author taktod
 *
 */
public class ServiceDescriptor extends Descriptor {
	private Bit8 serviceType; // 00000001
	private Bit8 serviceProviderNameLength;
	private String serviceProviderName;
	private Bit8 serviceNameLength;
	private String serviceName;
	/**
	 * コンストラクタ
	 * @param descriptorLength
	 */
	public ServiceDescriptor(Bit8 descriptorLength) {
		super(new Bit8(DescriptorType.service_descriptor.intValue()), descriptorLength);
		// typeだけ1に設定しておく。
		serviceType = new Bit8(1);
	}
	/**
	 * 解析動作
	 * @param channel
	 * @throws Exception
	 */
	public void analyze(IReadChannel channel) throws Exception {
		serviceType = new Bit8();
		serviceProviderNameLength = new Bit8();
		Bit.bitLoader(channel, serviceType, serviceProviderNameLength);
		serviceProviderName = new String(BufferUtil.safeRead(channel, serviceProviderNameLength.get()).array());
		serviceNameLength = new Bit8();
		Bit.bitLoader(channel, serviceNameLength);
		serviceName = new String(BufferUtil.safeRead(channel, serviceNameLength.get()).array());
	}
	public String dump4() {
		StringBuilder data = new StringBuilder("serviceDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" type:").append(serviceType);
		data.append(" providerName:").append(serviceProviderName);
		data.append(" name:").append(serviceName);
		return data.toString();
	}
}
