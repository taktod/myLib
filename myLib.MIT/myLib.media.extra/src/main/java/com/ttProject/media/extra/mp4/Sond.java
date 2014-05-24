/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.extra.mp4;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.atom.Stco;
import com.ttProject.media.mp4.atom.Stsc;
import com.ttProject.media.mp4.atom.Stsz;
import com.ttProject.media.mp4.atom.Stts;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 音声データ用のatom
 * @author taktod
 *
 */
public class Sond extends Atom implements IIndexAtom {
	/** データサイズ */
	private int size;
	/** sample数 */
	private int totalSampleCount;
	/** データサイズ */
	private int totalSize;
	/** timescale値(1秒あたり何ticあるか) */
	private int timescale;
	/** サンプルレート */
	private int sampleRate;
	/** チャンネルカウント 1:モノラル 2:ステレオ */
	private byte channelCount;
	/** mediaSequenceHeader */
	private Msh msh;
	/** stco */
	private Stco stco;
	/** stsc */
	private Stsc stsc;
	/** stsz */
	private Stsz stsz;
	/** stts */
	private Stts stts;
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 */
	public Sond(int position, int size) {
		super(Sond.class.getSimpleName().toLowerCase(), position, size);
		this.size = size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getSize() {
		return size;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public int getTotalSampleCount() {
		return totalSampleCount;
	}
	public int getTotalFlvSize() {
		if(msh == null) {
			return totalSampleCount * (11 + 4 + 1) + totalSize;
		}
		else {
			return totalSampleCount * (11 + 4 + 2) + totalSize + msh.getSize() - 8 + 11 + 4 + 2;
		}
	}
	public void setTimescale(int timescale) {
		this.timescale = timescale;
	}
	public int getTimescale() {
		return timescale;
	}
	public int getSampleRate() {
		return sampleRate;
	}
	public byte getChannelCount() {
		return channelCount;
	}
	public Msh getMsh() {
		return msh;
	}
	public Stco getStco() {
		return stco;
	}
	public Stsc getStsc() {
		return stsc;
	}
	public Stsz getStsz() {
		return stsz;
	}
	public Stts getStts() {
		return stts;
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 21);
		buffer.position(4);
		totalSampleCount = buffer.getInt();
		totalSize = buffer.getInt();
		timescale = buffer.getInt();
		sampleRate = buffer.getInt();
		channelCount = buffer.get();
		// ここから先がタグデータ
		while(ch.position() < getPosition() + getSize()) {
			int position = ch.position();
			buffer = BufferUtil.safeRead(ch, 8);
			int size = buffer.getInt();
			String tag = BufferUtil.getDwordText(buffer);
			if("msh ".equals(tag)) {
				msh = new Msh(position, size);
			}
			else if("stco".equals(tag)) {
				stco = new Stco(position, size);
			}
			else if("stsc".equals(tag)) {
				stsc = new Stsc(position, size);
			}
			else if("stsz".equals(tag)) {
				stsz = new Stsz(position, size);
			}
			else if("stts".equals(tag)) {
				stts = new Stts(position, size);
			}
			else {
				throw new Exception("解析不能なタグを発見:" + tag);
			}
			ch.position(position + size);
		}
	}
	@Override
	public void writeIndex(WritableByteChannel idx) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(29);
		buffer.putInt(size); // サイズ
		buffer.put("sond".getBytes()); // タグ
		buffer.putInt(0); // version + flags
		buffer.putInt(0); // totalSampleCount
		buffer.putInt(0); // totalSize
		buffer.putInt(timescale); // timescale
		buffer.putInt(sampleRate); // sampleRate
		buffer.put(channelCount);
		buffer.flip();
		idx.write(buffer);
	}
	/**
	 * flv用のmediaSequenceHeaderを作成します。
	 * @param tmp
	 * @return
	 * @throws Exception
	 */
	public AudioTag createFlvMshTag(IReadChannel tmp) throws Exception {
		if(msh == null) { // mediaSequenceHeaderが解析されていない場合は応答しない。
			return null;
		}
		AudioTag mshTag = new AudioTag();
		// とりあえずmshがある場合はaacなので、そのようにしておく
		mshTag.setCodec(CodecType.AAC);
		mshTag.setChannels(channelCount);
		mshTag.setSampleRate(sampleRate);
		mshTag.setMSHFlg(true);
		tmp.position(msh.getPosition() + 8);
		mshTag.setData(tmp, msh.getSize() - 8);
		return mshTag;
	}
}
