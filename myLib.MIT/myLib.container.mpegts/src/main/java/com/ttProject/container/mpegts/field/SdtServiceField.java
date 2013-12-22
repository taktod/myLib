package com.ttProject.container.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.container.mpegts.descriptor.Descriptor;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit12;
import com.ttProject.unit.extra.bit.Bit16;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * sdtの中にあるデータfield部
 * @author taktod
 */
public class SdtServiceField {
	private Bit16 serviceId; // 16ビット
	private Bit6 reservedFutureUse;
	private Bit1 eitScheduleFlag;
	private Bit1 eitPresentFollowingFlag;
	private Bit3 runningStatus;
	private Bit1 freeCAMode;
	private Bit12 descriptorsLoopLength; // 12ビット
	private List<Descriptor> descriptors = new ArrayList<Descriptor>();
	/**
	 * コンストラクタ
	 */
	public SdtServiceField() {
		// デフォルト値は以下とします。
		serviceId = new Bit16(1); // とりあえず1を指定しておく。
		reservedFutureUse = new Bit6(0x3F);
		eitScheduleFlag = new Bit1(0);
		eitPresentFollowingFlag = new Bit1(0);
		runningStatus = new Bit3(0x4);
		freeCAMode = new Bit1(0);
		descriptorsLoopLength = new Bit12(0);
	}
	public void setServiceId(short id) {
		this.serviceId.set(id);
	}
	public short getServiceId() {
		return (short)serviceId.get();
	}
	/**
	 * 保持descriptorを応答する。
	 * @return
	 */
	public List<Descriptor> getDescriptors() {
		return new ArrayList<Descriptor>(descriptors);
	}
	public void addDescriptor(Descriptor descriptor) {
		// すでに保持済みのオブジェクトなら多重で保持しない。
		if(!descriptors.contains(descriptor)) {
			descriptors.add(descriptor);
		}
		descriptorsLoopLength.set(0);
		for(Descriptor desc : descriptors) {
			descriptorsLoopLength.set(descriptorsLoopLength.get() + desc.getSize());
		}
	}
	public boolean removeDescripor(Descriptor descriptor) {
		boolean result = descriptors.remove(descriptor);
		for(Descriptor desc : descriptors) {
			descriptorsLoopLength.set(descriptorsLoopLength.get() + desc.getSize());
		}
		return result;
	}

	/**
	 * 保持データサイズを応答しておく
	 * @return
	 */
	public int getSize() {
		return descriptorsLoopLength.get() + 5;
	}
	/**
	 * 解析しておきます。
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		serviceId = new Bit16();
		reservedFutureUse = new Bit6();
		eitScheduleFlag = new Bit1();
		eitPresentFollowingFlag = new Bit1();
		runningStatus = new Bit3();
		freeCAMode = new Bit1();
		descriptorsLoopLength = new Bit12();
		BitLoader bitLoader = new BitLoader(ch);
		bitLoader.load(serviceId, reservedFutureUse,
				eitScheduleFlag, eitPresentFollowingFlag, runningStatus,
				freeCAMode, descriptorsLoopLength);
		int size = descriptorsLoopLength.get();
		while(size > 0) {
			// Descriptorを読み込む必要あり。
			Descriptor descriptor = Descriptor.getDescriptor(ch);
			size -= descriptor.getDescriptorLength().get() + 2; // データ長 + データtype&length定義分
			descriptors.add(descriptor);
		}
	}
	public List<Bit> getBits() {
		List<Bit> list = new ArrayList<Bit>();
		list.add(serviceId);
		list.add(reservedFutureUse);
		list.add(eitScheduleFlag);
		list.add(eitPresentFollowingFlag);
		list.add(runningStatus);
		list.add(freeCAMode);
		list.add(descriptorsLoopLength);
		for(Descriptor descriptor : descriptors) {
			list.addAll(descriptor.getBits());
		}
		return list;
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("  ");
		data.append("sdtServiceField:");
		data.append(" si:").append(Integer.toHexString(serviceId.get()));
		data.append(" rfu:").append(reservedFutureUse);
		data.append(" esf:").append(eitScheduleFlag);
		data.append(" epff:").append(eitPresentFollowingFlag);
		data.append(" rs:").append(runningStatus);
		data.append(" fcam:").append(freeCAMode);
		data.append(" dll:").append(Integer.toHexString(descriptorsLoopLength.get()));
		for(Descriptor descriptor : descriptors) {
			data.append("\n");
			data.append(descriptor);
		}
		return data.toString();
	}
}
