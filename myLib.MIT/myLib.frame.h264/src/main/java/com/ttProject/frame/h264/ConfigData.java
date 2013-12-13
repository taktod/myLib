package com.ttProject.frame.h264;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.frame.h264.type.PictureParameterSet;
import com.ttProject.frame.h264.type.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * flvのmediaSequenceHeaderやmp4のdecodeBox(Avcc)の内容から、
 * nalデータを取り出す動作
 * 01 4D 40 1E FF E1 00 19 67 4D 40 1E 92 42 01 40 5F F2 E0 22 00 00 03 00 C8 00 00 2E D5 1E 2C 5C 90 01 00 04 68 EE 32 C8
 * [] avcC version 1
 *    [      ] profile compatibility level
 *             [] 111111 + 2bit nal size - 1(ff固定とおもっててOKでしょう)
 *                [] number of SPS e1固定？
 *                   [   ] spsLength
 *                         [spsNalデータ                                                             ]
 *                                                                                                    [] number of PPS
 *                                                                                                       [   ] ppsLength
 *                                                                                                             [         ] ppsData
 * もしくはnalデータからconfigDataを作り出す動作
 * @author taktod
 */
public class ConfigData {
	/**
	 * spsとppsを取り出す
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public List<IUnit> getNals(IReadChannel channel) throws Exception {
		ISelector selector = new H264FrameSelector();
		List<IUnit> list = new ArrayList<IUnit>();
		if(channel.size() - channel.position() < 8) {
			throw new Exception("先頭データの読み込み部のサイズが小さすぎます。");
		}
		ByteBuffer buffer = BufferUtil.safeRead(channel, 6);
		if(buffer.get() != 0x01) {
			throw new Exception("avccVersionが1でないみたいです。");
		}
		short spsSize = BufferUtil.safeRead(channel, 2).getShort();
		IReadChannel byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, spsSize));
		IUnit sps = selector.select(byteChannel);
		if(!(sps instanceof SequenceParameterSet)) {
			throw new Exception("取得データがspsではありませんでした。");
		}
		sps.load(byteChannel);
		list.add(sps);
		BufferUtil.safeRead(channel, 1);
		short ppsSize = BufferUtil.safeRead(channel, 2).getShort();
		byteChannel = new ByteReadChannel(BufferUtil.safeRead(channel, ppsSize));
		IUnit pps = selector.select(byteChannel);
		if(!(pps instanceof PictureParameterSet)) {
			throw new Exception("取得データがppsではありませんでした。");
		}
		pps.load(byteChannel);
		list.add(pps);
		return list;
	}
	/**
	 * configDataをspsとppsから作成します。
	 * @param sps
	 * @param pps
	 * @return
	 * @throws Exception
	 */
	public ByteBuffer makeConfigData(SequenceParameterSet sps, PictureParameterSet pps) throws Exception {
		ByteBuffer spsBuffer = sps.getData();
		ByteBuffer ppsBuffer = pps.getData();
		ByteBuffer data = ByteBuffer.allocate(11 + spsBuffer.remaining() + ppsBuffer.remaining());
		data.put((byte)1);
		spsBuffer.position(1);
		data.put(spsBuffer.get());
		data.put(spsBuffer.get());
		data.put(spsBuffer.get());
		spsBuffer.position(0);
		data.put((byte)0xFF);
		data.put((byte)0xE1);
		data.putShort((short)spsBuffer.remaining());
		data.put(spsBuffer);
		data.put((byte)1);
		data.putShort((short)ppsBuffer.remaining());
		data.put(ppsBuffer);
		data.flip();
		return data;
	}
}
