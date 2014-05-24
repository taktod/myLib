/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.packet.flv;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.CodecType;
import com.ttProject.util.HexUtil;

public class FlvMediaPacket extends FlvPacket {
	private Logger logger = Logger.getLogger(FlvMediaPacket.class);
	private final FlvHeaderPacket headerPacket;
	private final long startPos;
	public FlvMediaPacket(FlvPacketManager manager, FlvHeaderPacket headerPacket) {
		super(manager);
		this.headerPacket = headerPacket;
		this.startPos = manager.getCurrentPos();
	}
	@Override
	public boolean isHeader() {
		return false;
	}
	@Override
	public boolean analize(ByteBuffer buffer) {
		while(buffer.remaining() > 0) {
			int position = buffer.position();
			Boolean result = null;
			byte header = buffer.get();
			buffer.position(position);
			switch(header) {
			case FlvPacketManager.AUDIO_TAG:
				result = analizeAudioData(buffer);
				break;
			case FlvPacketManager.VIDEO_TAG:
				result = analizeVideoData(buffer);
				break;
			case FlvPacketManager.META_TAG:
				result = analizeMetaData(buffer);
				break;
			case FlvPacketManager.FLV_TAG:
				result = analizeFlvHeader(buffer);
				break;
			default:
				logger.warn("解析不能なデータがきました。");
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);
				logger.warn("position:" + position);
				logger.warn(HexUtil.toHex(data, true));
				throw new RuntimeException("解析不能なデータがきました。" + header);
			}
			if(result != null) {
				buffer.position(position);
				return result;
			}
		}
		return false;
	}
	/**
	 * 音声データを解析する。
	 * @param buffer
	 * @return
	 */
	protected Boolean analizeAudioData(ByteBuffer buffer) {
		if(buffer.remaining() < 11) {
			// header部分取得に満たない場合
			return false;
		}
		// ヘッダ情報を取得
		byte[] header = new byte[11];
		buffer.get(header);
		// データサイズを確認する。
		int size = getSizeFromHeader(header);
		if(buffer.remaining() < size + 4) {
			// 十分な量のデータがない。
			return false;
		}
		long time = getTimeFromHeader(header);
		getManager().setCurrentPos(time);
		// データを取り出す
		byte[] body = new byte[size];
		buffer.get(body);
		// 4byte終端データを確認する。
		byte[] tail = new byte[4];
		buffer.get(tail);
		headerPacket.setAudioCodec(CodecType.getAudioCodecType(body[0]));
		// コーデック確認
		boolean isSequenceHeader = false;
		if(headerPacket.getAudioCodec() == CodecType.AAC) {
			// AACなら次のパケットを確認して、headerであるか確認する。
			if(body[1] == 0x00) {
				// headerだった
				ByteBuffer sequenceHeader = ByteBuffer.allocate(size + 4 + 11);
				sequenceHeader.put(header);
				sequenceHeader.putInt(4, 0); // timestampの位置を強制的に0にしておく
				sequenceHeader.put(body);
				sequenceHeader.put(tail);
				sequenceHeader.flip();
				headerPacket.analize(sequenceHeader);
				isSequenceHeader = true;
			}
		}
		// sequenceデータではなく
		// キーフレームだった場合はパケットの境目と判定しなければいけない。
		if(headerPacket.getVideoCodec() == CodecType.NONE && !isSequenceHeader) {
			float passedTime = (getManager().getCurrentPos() - startPos) / 1000;
			if(passedTime >= getManager().getDuration()) {
				// バッファサイズがたまっている場合は、終端がきたことになるので、分割する。
				setDuration(passedTime);
				// 記録済み時間について記録しておく。
				getManager().addPassedTime(getDuration());
				return true;
			}
		}
		// シーケンスヘッダも書き込んでおく。(書き込んでおかないと、中途で変更があったときに困る。)
		ByteBuffer saveBuffer = getBuffer(size+ 4 + 11);
		saveBuffer.put(header);
		saveBuffer.put(body);
		saveBuffer.put(tail);
		return null;
	}
	/**
	 * 映像データを解析する。
	 * @param buffer
	 * @return
	 */
	protected Boolean analizeVideoData(ByteBuffer buffer) {
		if(buffer.remaining() < 11) {
			// header部分取得に満たない場合
			return false;
		}
		// ヘッダ情報を取得
		byte[] header = new byte[11];
		buffer.get(header);
		// データサイズを確認する。
		int size = getSizeFromHeader(header);
		if(buffer.remaining() < size + 4) {
			// 十分な量のデータがない。
			return false;
		}
		long time = getTimeFromHeader(header);
		getManager().setCurrentPos(time);
		// データを取り出す
		byte[] body = new byte[size];
		// データの先頭1文字目を確認することでコーデック情報とかがわかります。
		// 0xF0でキーフレームかどうか判定できる。
		// 0x0Fで、フレームタイプがわかる。
		// H.264の場合は、先頭の4バイトは、拡張データになります。
		// TODO 本当にそうなっているか確認しなければいけない。0:AVCのヘッダデータ
		buffer.get(body);
		// 4byte終端データを確認する。
		byte[] tail = new byte[4];
		buffer.get(tail);
		boolean isSequenceHeader = false;
		// コーデック確認
		headerPacket.setVideoCodec(CodecType.getVideoCodecType(body[0]));
		if(headerPacket.getVideoCodec() == CodecType.AVC) {
			// AVCなら次のパケットを確認して、headerであるか確認する。
			if((body[0] & 0x10) == 0x10 && body[1] == 0x00) {
				// headerだった
				ByteBuffer sequenceHeader = ByteBuffer.allocate(size + 4 + 11);
				sequenceHeader.put(header);
				sequenceHeader.putInt(4, 0); // timestampの位置を強制0にしておく。
				sequenceHeader.put(body);
				sequenceHeader.put(tail);
				sequenceHeader.flip();
				headerPacket.analize(sequenceHeader);
				isSequenceHeader = true;
			}
		}
		// sequenceデータではなく
		// キーフレームだった場合はパケットの境目と判定しなければいけない。
		if(/*(body[0] & 0x10) == 0x10 && */!isSequenceHeader) {
			float passedTime = (getManager().getCurrentPos() - startPos) / 1000f;
			if(passedTime >= getManager().getDuration()) {
				// バッファサイズがたまっている場合は、終端がきたことになるので、分割する。
				setDuration(passedTime);
				// 記録済み時間について記録しておく。
				getManager().addPassedTime(getDuration());
				return true;
			}
		}
		// ここでバッファにカキコする。
		ByteBuffer saveBuffer = getBuffer(size+ 4 + 11);
		saveBuffer.put(header);
		saveBuffer.put(body);
		saveBuffer.put(tail);
		return null;
	}
	/**
	 * metaタグ解析動作
	 * @param buffer 解析するバッファ(flvデータの先頭であることを期待します。)
	 * @return true:今回のパケットの解析が完了した。false:中途でデータがたりなくなった。null:解析は完了し、次のデータを解析する必要がある場合
	 */
	protected Boolean analizeMetaData(ByteBuffer buffer) {
		if(buffer.remaining() < 11) {
			// header部分取得に満たない場合
			return false;
		}
		// ヘッダ情報を取得
		byte[] header = new byte[11];
		buffer.get(header);
		// データサイズを確認する。
		int size = getSizeFromHeader(header);
		if(buffer.remaining() < size + 4) {
			// 十分な量のデータがない。
			return false;
		}
		long time = getTimeFromHeader(header);
		getManager().setCurrentPos(time);
		// データを取り出す
		byte[] body = new byte[size];
		// metaデータは特に興味ないので、すてておく。
		buffer.get(body);
		// 4byte終端データを確認する。
		byte[] tail = new byte[4];
		buffer.get(tail);
		return null;
	}
	/**
	 * Flvのヘッダであるか確認します。
	 * @return
	 */
	protected Boolean analizeFlvHeader(ByteBuffer buffer) {
		// バッファにデータがきちんと存在しているか確認
		if(buffer.remaining() < 13) {
			// 足りない。
			return false;
		}
		byte[] data = new byte[13];
		buffer.get(data);
		for(int i = 0;i < 13;i ++) {
			if(i != 4) {
				if(data[i] != FlvPacketManager.flvHeader[i]) {
					throw new RuntimeException("flvHeaderデータが不正です。");
				}
			}
			else {
				if(data[i] != 1 && data[i] != 4 && data[i] != 5) {
					throw new RuntimeException("flvHeaderデータのメディア指定が不正です。");
				}
			}
		}
		ByteBuffer headerBuffer = ByteBuffer.allocate(FlvPacketManager.flvHeader.length);
		headerBuffer.put(FlvPacketManager.flvHeader);
		headerBuffer.flip();
		headerPacket.analize(headerBuffer);
		// MediaTagには書き込まない。
		return null;
	}
	public void writeData(String targetFile, int number, boolean append) {
		ByteBuffer buffer = getBuffer(0);
		try {
			WritableByteChannel channel = Channels.newChannel(new FileOutputStream(targetFile, append));
			// 先頭にサイズ crc値 index値をいれておく必要あり。
			ByteBuffer header = ByteBuffer.allocate(12);
			buffer.flip();
			header.putInt(buffer.remaining());
			header.putInt(getManager().getCRC());
			header.putInt(number);
			header.flip();
			channel.write(header);
			// データ実体を書き込む
			channel.write(buffer);
		}
		catch (Exception e) {
		}
	}
}
