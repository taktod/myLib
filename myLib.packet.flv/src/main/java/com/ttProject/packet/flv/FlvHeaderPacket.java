package com.ttProject.packet.flv;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;

/**
 * flvHeaderPacketは先頭にHashデータを保持させておく。(4バイト)
 * http経由でアクセスする場合は、どのファイルにアクセスすればいいかわからないので、インデックス番号を応答するものも準備しておく。(そのインデックス番号以降のデータをうけとるみたいな感じ)
 * @author taktod
 */
public class FlvHeaderPacket extends FlvPacket {
	private ByteBuffer buffer;
	private ByteBuffer flvHeader = null;
	private ByteBuffer videoSequenceHeader = null;
	private ByteBuffer audioSequenceHeader = null;
	private CodecType videoCodec = CodecType.NONE;
	private CodecType audioCodec = CodecType.NONE;
	private boolean isSaved = false;
	public FlvHeaderPacket(FlvPacketManager manager) {
		super(manager);
	}
	@Override
	public boolean isHeader() {
		return true;
	}
	public boolean isSaved() {
		return isSaved;
	}
	public CodecType getVideoCodec() {
		return videoCodec;
	}
	public void setVideoCodec(CodecType codec) {
		videoCodec = codec;
	}
	public CodecType getAudioCodec() {
		return audioCodec;
	}
	public void setAudioCodec(CodecType codec) {
		audioCodec = codec;
	}
	/**
	 * 解析を実施します。
	 * ここにくるデータは、mediaPacket側でみつけた、単一パケットのコピーとしますので、終端等は調べる必要なし。
	 */
	@Override
	public boolean analize(ByteBuffer buffer) {
		byte type = buffer.get();
		buffer.rewind();
		switch(type) {
		case FlvPacketManager.AUDIO_TAG:
			audioSequenceHeader = buffer;
			isSaved = false;
			break;
		case FlvPacketManager.VIDEO_TAG:
			videoSequenceHeader = buffer;
			isSaved = false;
			break;
		case FlvPacketManager.FLV_TAG:
			flvHeader = buffer;
			videoSequenceHeader = null;
			audioSequenceHeader = null;
			break;
		default:
			return false;
		}
		ByteBuffer data = ByteBuffer.allocate(
				flvHeader.limit() + 
				(videoSequenceHeader == null ? 0 : videoSequenceHeader.limit()) +
				(audioSequenceHeader == null ? 0 : audioSequenceHeader.limit())
		);
		data.put(flvHeader);
		flvHeader.rewind();
		if(videoSequenceHeader != null) {
			data.put(videoSequenceHeader);
			videoSequenceHeader.rewind();
		}
		if(audioSequenceHeader != null) {
			data.put(audioSequenceHeader);
			audioSequenceHeader.rewind();
		}
		this.buffer = data;
		return true;
	}
	@Override
	public void writeData(String targetFile, boolean append) {
		try {
			WritableByteChannel channel = Channels.newChannel(new FileOutputStream(targetFile, append));
			// 先頭にcrc値をいれておく必要あり。
			ByteBuffer header = ByteBuffer.allocate(4);
			header.putInt(getManager().getCRC());
			header.flip();
			channel.write(header);
			// データ実体を書き込む
			buffer.flip();
			channel.write(buffer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			isSaved = true;
		}
	}
}
