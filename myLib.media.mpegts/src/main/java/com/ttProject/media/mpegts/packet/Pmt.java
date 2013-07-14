package com.ttProject.media.mpegts.packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ttProject.media.mpegts.CodecType;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.nio.channels.IReadChannel;

/**
 * Pmt
 * @author taktod
 */
public class Pmt extends Packet {
	private int pcrPid;
	private Map<Integer, CodecType> esMap = new HashMap<Integer, CodecType>();
	public Pmt(ByteBuffer buffer) {
		this(0, buffer);
	}
	public Pmt(int position, ByteBuffer buffer) {
		super(position, buffer);
	}
	@Override
	public void analyze(IReadChannel ch) throws Exception {
		// pcrとメディアPidを取得する必要あり。
		ByteBuffer buffer = getBuffer();
		buffer.position(5);
		if(!analyzeHeader(buffer, (byte)0x02)) {
			throw new Exception("ヘッダ部の読み込み時に不正なデータを検出しました。");
		}
		int size = getDataSize();
		size -= 5;
		int data;
		data = buffer.getShort() & 0xFFFF;
		size -= 2;
		// reserved3bit
		if(data >>> 13 != Integer.parseInt("111", 2)) {
			throw new Exception("PCRPID用の指示bitがおかしいです。");
		}
		// PCRPID(13bit)
		pcrPid = data & 0x1FFF;
		data = buffer.getShort() & 0xFFFF;
		size -= 2;
		// reserved(4bit)
		if(data >>> 12 != Integer.parseInt("1111", 2)) {
			throw new Exception("番組情報長の指示ビットがおかしいです。");
		}
		// programInfoLength(2+10bit)
		int programInfoLength = data & 0x0FFF;
		// programDescriptor(N * 8) // programInfoLengthの長さと一致するサイズ(とりあえず飛ばしておく)
		size -= programInfoLength;
		buffer.position(buffer.position() + programInfoLength);
		// Track情報
		while(size > 4) {
			// type(8bit)
			int type = buffer.get();
			CodecType codec = CodecType.getType(type);
			size -= 1;
			data = buffer.getShort() & 0xFFFF;
			size -= 2;
			// reserved(3bit)
			// mediaPid(13bit)
			int pid = data & 0x1FFF;
			esMap.put(pid, codec);
			data = buffer.getShort() & 0xFFFF;
			size -= 2;
			// reserved(4bit)
			// esInfoLength(12bit)
			int esInfoLength = data & 0x0FFF;
			// esDescriptor(esInfoLength)
			size -= esInfoLength;
			buffer.position(buffer.position() + esInfoLength);
		}
		// crc32
	}
	public int getPcrPid() {
		return pcrPid;
	}
	public Map<Integer, CodecType> getEsMap() {
		return new HashMap<Integer, CodecType>(esMap);
	}
	@Override
	public String toString() {
		return "Pmt: ";
	}
}
