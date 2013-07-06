package com.ttProject.media.mp4.atom;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.util.BufferUtil;
import com.ttProject.media.IAnalyzer;
import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.atom.stsd.IRecordAnalyzer;
import com.ttProject.media.mp4.atom.stsd.Record;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 各トラックのメディア詳細データを保持しているみたいです。
 * コーデック情報やflv化するときのmediaSequenceHeader等もここにはいっています。
 * @author taktod
 */
public class Stsd extends Atom {
	private int trackCount;
	private List<Record> records = new ArrayList<Record>();
	public Stsd(int position, int size) {
		super(Stsd.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		throw new Exception("stsdの解析では、IAtomAnalyzerは使いません。");
	}
	@Override
	public void analyze(IFileReadChannel ch, IAnalyzer<?> analyzer) throws Exception {
		ch.position(getPosition() + 8);
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		analyzeFirstInt(buffer.getInt());
		trackCount = buffer.getInt();
		// trackCountは1であることを望んでおきます。(マルチトラックはやらない。)
		if(trackCount != 1) {
			throw new Exception("stsdのtrack数が1ではありませんでした。作者の知らない形なので必要であれば解析を依頼してください。");
		}
		if(analyzer instanceof IRecordAnalyzer) {
			IRecordAnalyzer recordAnalyzer = (IRecordAnalyzer)analyzer;
			for(int i = 0;i < trackCount;i ++) {
				records.add(recordAnalyzer.analyze(ch));
			}
		}
	}
	public List<Record> getRecords() {
		return records;
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
