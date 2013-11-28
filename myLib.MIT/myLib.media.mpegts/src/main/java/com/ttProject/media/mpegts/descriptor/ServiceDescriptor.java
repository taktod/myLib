package com.ttProject.media.mpegts.descriptor;

import java.nio.ByteBuffer;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.extra.BitLoader;
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
	 * コンストラクタ
	 */
	public ServiceDescriptor() {
		super(new Bit8(DescriptorType.service_descriptor.intValue()));
		serviceType = new Bit8(1);
	}
	/**
	 * 名称を追加
	 * @param providerName
	 * @param name
	 */
	public void setName(String providerName, String name) {
		serviceProviderName = providerName;
		serviceProviderNameLength = new Bit8(providerName.length());
		serviceName = name;
		serviceNameLength = new Bit8(name.length());
		setDescriptorLength(new Bit8(3 + providerName.length() + name.length()));
	}
	public String getProviderName() {
		return serviceProviderName;
	}
	public String getName() {
		return serviceName;
	}
	@Override
	public List<Bit> getBits() {
		List<Bit> list = super.getBits();
		list.add(serviceType);
		list.add(serviceProviderNameLength);
		// stringの値をbitに変換して投入する必要あり。
		ByteBuffer buffer;
		buffer = ByteBuffer.wrap(serviceProviderName.getBytes());
		while(buffer.remaining() > 0) {
			list.add(new Bit8(buffer.get() & 0xFF));
		}
		list.add(serviceNameLength);
		buffer = ByteBuffer.wrap(serviceName.getBytes());
		while(buffer.remaining() > 0) {
			list.add(new Bit8(buffer.get() & 0xFF));
		}
		return list;
	}
	/**
	 * 解析動作
	 * @param channel
	 * @throws Exception
	 */
	public void analyze(IReadChannel channel) throws Exception {
		serviceType = new Bit8();
		serviceProviderNameLength = new Bit8();
		BitLoader bitLoader = new BitLoader(channel);
		bitLoader.load(serviceType, serviceProviderNameLength);
		serviceProviderName = new String(BufferUtil.safeRead(channel, serviceProviderNameLength.get()).array());
		serviceNameLength = new Bit8();
		bitLoader = new BitLoader(channel);
		bitLoader.load(serviceNameLength);
		serviceName = new String(BufferUtil.safeRead(channel, serviceNameLength.get()).array());
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("    ");
		data.append("serviceDescriptor:");
		data.append(" length:").append(Integer.toHexString(getDescriptorLength().get()));
		data.append(" type:").append(serviceType);
		data.append(" providerName:").append(serviceProviderName);
		data.append(" name:").append(serviceName);
		return data.toString();
	}
}
