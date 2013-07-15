package com.ttProject.media.mpegts.field;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.media.extra.Bit;
import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit3;
import com.ttProject.media.extra.Bit4;
import com.ttProject.media.extra.Bit6;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.mpegts.descriptor.Descriptor;
import com.ttProject.nio.channels.IReadChannel;

/**
 * sdtの中にあるデータfield部
 * @author taktod
 */
public class SdtServiceField {
	private short serviceId; // 16バイト
	private Bit6 reservedFutureUse;
	private Bit1 eitScheduleFlag;
	private Bit1 eitPresentFollowingFlag;
	private Bit3 runningStatus;
	private Bit1 freeCAMode;
	private short descriptorsLoopLength; // 12バイト
	private List<Descriptor> descriptors = new ArrayList<Descriptor>();
	/**
	 * コンストラクタ
	 */
	public SdtServiceField() {
		// デフォルト値は以下とします。
		serviceId = 1;
		reservedFutureUse = new Bit6(0x3F);
		eitScheduleFlag = new Bit1();
		eitPresentFollowingFlag = new Bit1();
		runningStatus = new Bit3(0x4);
		freeCAMode = new Bit1();
		// descriptorsLoopLengthは保持データ依存
	}
	/**
	 * 保持データサイズを応答しておく
	 * @return
	 */
	public int getSize() {
		return descriptorsLoopLength + 5;
	}
	/**
	 * 解析しておきます。
	 * @param ch
	 * @throws Exception
	 */
	public void analyze(IReadChannel ch) throws Exception {
		Bit8 serviceId_1 = new Bit8();
		Bit8 serviceId_2 = new Bit8();
		reservedFutureUse = new Bit6();
		eitScheduleFlag = new Bit1();
		eitPresentFollowingFlag = new Bit1();
		runningStatus = new Bit3();
		freeCAMode = new Bit1();
		Bit4 descriptorsLoopLength_1 = new Bit4();
		Bit8 descriptorsLoopLength_2 = new Bit8();
		Bit.bitLoader(ch, serviceId_1, serviceId_2, reservedFutureUse,
				eitScheduleFlag, eitPresentFollowingFlag, runningStatus,
				freeCAMode, descriptorsLoopLength_1, descriptorsLoopLength_2);
		serviceId = (short)((serviceId_1.get() << 8) | serviceId_2.get());
		descriptorsLoopLength = (short)((descriptorsLoopLength_1.get() << 8) | descriptorsLoopLength_2.get());
		int size = descriptorsLoopLength;
		while(size > 0) {
			// Descriptorを読み込む必要あり。
			Descriptor descriptor = Descriptor.getDescriptor(ch);
			size -= descriptor.getDescriptorLength().get() + 2; // データ長 + データtype&length定義分
			descriptors.add(descriptor);
		}
		System.out.println(dump3());
	}
	public String dump3() {
		StringBuilder data = new StringBuilder("sdtServiceField:");
		data.append(" si:").append(Integer.toHexString(serviceId));
		data.append(" rfu:").append(reservedFutureUse);
		data.append(" esf:").append(eitScheduleFlag);
		data.append(" epff:").append(eitPresentFollowingFlag);
		data.append(" rs:").append(runningStatus);
		data.append(" fcam:").append(freeCAMode);
		data.append(" dll:").append(Integer.toHexString(descriptorsLoopLength));
		return data.toString();
	}
}
