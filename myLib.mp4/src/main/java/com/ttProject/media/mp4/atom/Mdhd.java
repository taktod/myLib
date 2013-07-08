package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

public class Mdhd extends Atom {
	/** 作成日時 */
	private long creationTime;
	/** 更新日時 */
	private long modifitaionTime;
	/** sttsのtimescale情報 */
	private int timescale;
	/** データの長さ(timestampによるtic数で記述) */
	private long duration;
	/**
	 * コンストラクタ
	 * @param size
	 * @param position
	 */
	public Mdhd(int size, int position) {
		super(Mdhd.class.getSimpleName().toLowerCase(), size, position);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, getSize() - 8);
		analyzeFirstInt(buffer.getInt());
		if(getVersion() == 0) {
			creationTime = buffer.getInt();
			modifitaionTime = buffer.getInt();
		}
		else {
			creationTime = buffer.getLong();
			modifitaionTime = buffer.getLong();
		}
		timescale = buffer.getInt();
		if(getVersion() == 0) {
			duration = buffer.getInt();
		}
		else {
			duration = buffer.getLong();
		}
		// あとはpad 1 とLanguage 5x3 Reserved 16 = 32bit でおわるはず。
		// 4バイトのこっているはず。
	}
	/**
	 * @return
	 */
	public int getTimescale() {
		return timescale;
	}
	/**
	 * @return
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * @return
	 */
	public long getCreationTime() {
		return creationTime;
	}
	/**
	 * @return
	 */
	public long getModificationTime() {
		return modifitaionTime;
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
