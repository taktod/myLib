package com.ttProject.transcode.xuggle.packet;

import java.nio.ByteBuffer;

import com.ttProject.media.Unit;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.frame.PictureParameterSet;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.media.h264.frame.Slice;
import com.ttProject.media.h264.frame.SliceIDR;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * h264Frameからpacketを生成します。
 * @author taktod
 */
@SuppressWarnings("unused")
public class H264Packetizer implements IPacketizer {
	/** 最終調査フレーム(いらないか？) */
	private Frame lastH264Frame = null;
	/** spsの保持 */
	private SequenceParameterSet sps = null;
	/** ppsの保持 */
	private PictureParameterSet pps = null;
	/**
	 * 入力データの整合性を確認する。
	 */
	@Override
	public boolean check(Unit unit) {
		if(!(unit instanceof Frame)) {
			return true;
		}
		if(lastH264Frame == null) {
			return true;
		}
		Frame h264Frame = (Frame) unit;
		// サイズとか比較したいけど、いい方法が見つからないのでとりあえずスルー
		return true;
	}
	/**
	 * frameからpacketを生成して応答します。
	 */
	@Override
	public IPacket getPacket(Unit unit, IPacket packet) throws Exception {
		if(!(unit instanceof Frame)) {
			return null;
		}
/*		if(unit instanceof SequenceParameterSet) {
			sps = (SequenceParameterSet)unit;
			return null;
		}
		if(unit instanceof PictureParameterSet) {
			pps = (PictureParameterSet)unit;
			return null;
		}
		// フレームでなければ処理する必要がないので、捨てる
		if(!(unit instanceof Slice) && !(unit instanceof SliceIDR)) {
			return null;
		}
		if(sps == null || pps == null) {
			throw new Exception("spsとppsが決定していない状態でSliceのデータを受け取りました。");
		}
		if(packet == null) {
			packet = IPacket.make();
		}
		ByteBuffer buffer = null;
		if(unit instanceof Slice) {
			// innerFrame
			Slice slice = (Slice) unit;
			ByteBuffer sliceData = slice.getData();
			buffer = ByteBuffer.allocate(4 + sliceData.remaining());
			buffer.putInt(1);
			buffer.put(sliceData);
			buffer.flip();
		}
		else {
			// keyFrame
			SliceIDR sliceIDR = (SliceIDR) unit;
			ByteBuffer spsData = sps.getData();
			ByteBuffer ppsData = pps.getData();
			ByteBuffer sliceIDRData = sliceIDR.getData();
			buffer = ByteBuffer.allocate(4 + spsData.remaining()
					+ 4 + ppsData.remaining()
					+ 4 + sliceIDRData.remaining());
			buffer.putInt(1);
			buffer.put(spsData);
			buffer.putInt(1);
			buffer.put(ppsData);
			buffer.putInt(1);
			buffer.put(sliceIDRData);
			buffer.flip();
		}
		int size = buffer.remaining();
		IBuffer bufData = IBuffer.make(null, buffer.array(), 0, size);
		packet.setData(bufData);
		packet.setFlags(1);
		// 
//		packet.setDts();
		return null;*/
		throw new Exception("h264のframeはtimestamp値を保持していないので、このままでは処理できない。pts dtsが決定しない");
	}
	@Override
	public IStreamCoder createDecoder() throws Exception {
		return null;
	}
}
