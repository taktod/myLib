package com.ttProject.media.extra.mp4;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.media.mp4.Type;
import com.ttProject.media.mp4.atom.Ftyp;
import com.ttProject.media.mp4.atom.Mdhd;
import com.ttProject.media.mp4.atom.Mdia;
import com.ttProject.media.mp4.atom.Minf;
import com.ttProject.media.mp4.atom.Moov;
import com.ttProject.media.mp4.atom.Smhd;
import com.ttProject.media.mp4.atom.Stbl;
import com.ttProject.media.mp4.atom.Stco;
import com.ttProject.media.mp4.atom.Stsc;
import com.ttProject.media.mp4.atom.Stsd;
import com.ttProject.media.mp4.atom.Stss;
import com.ttProject.media.mp4.atom.Stsz;
import com.ttProject.media.mp4.atom.Stts;
import com.ttProject.media.mp4.atom.Tkhd;
import com.ttProject.media.mp4.atom.Trak;
import com.ttProject.media.mp4.atom.Vmhd;
import com.ttProject.media.mp4.atom.stsd.Record;
import com.ttProject.media.mp4.atom.stsd.RecordAnalyzer;
import com.ttProject.media.mp4.atom.stsd.data.Avcc;
import com.ttProject.media.mp4.atom.stsd.record.Aac;
import com.ttProject.media.mp4.atom.stsd.record.H264;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * indexファイルを作成していきます。
 * @author taktod
 */
public class IndexFileCreator implements IAtomAnalyzer {
//	private FileChannel idx; // 書き込み対象ファイル
	private FileOutputStream idx;
	private final File targetFile;
	private int trakStartPos; // トラックの開始位置
	private CurrentType type = null; // 現在の処理trakタイプ
	private Vdeo vdeo = null;
	private Sond sond = null;
	private Meta meta = null;
	private Mdhd mdhd = null;
	private Tkhd tkhd = null;
	private enum CurrentType { // タイプリスト
		AUDIO,
		VIDEO,
		HINT,
		MEDIA
	}
	/**
	 * コンストラクタ
	 * @param targetFile
	 * @throws Exception
	 */
	public IndexFileCreator(File targetFile) throws Exception {
		this.targetFile = targetFile;
		idx = new FileOutputStream(targetFile);
	}
	@Override
	public Atom analyze(IReadChannel ch) throws Exception {
		if(ch.size() == ch.position()) {
			return null;
		}
		int position = ch.position();
		ByteBuffer buffer = BufferUtil.safeRead(ch, 8);
		int size = buffer.getInt();
		String tag = BufferUtil.getDwordText(buffer);
		Type type = Type.getType(tag);
		switch(type) {
		case Ftyp:
			Ftyp ftyp = new Ftyp(position, size);
			ch.position(position + size);
			return ftyp;
		case Moov:
			Moov moov = new Moov(position, size);
			moov.analyze(ch, this);
			ch.position(position + size);
			return moov;
/*		case Mvhd:
			Mvhd mvhd = new Mvhd(position, size);
			// とりあえず解析せずほっとく
			ch.position(position + size);
			return mvhd;
/*		case Iods: // 消す候補
			// iodsは必要ないと思う。消す。
			Iods iods = new Iods(position, size);
			ch.position(position + size);
			return iods;
		case Udta: // 消す候補
			// udtaはいらない。
			Udta udta = new Udta(position, size);
			ch.position(position + size);
			return udta;*/
		case Trak:
			updatePrevTag();
			this.type = null;
			// trakの開始位置を調べる。
			this.trakStartPos = (int)idx.getChannel().position();
			Trak trak = new Trak(position, size);
			trak.analyze(ch, this);
			ch.position(position + size);
			return trak;
		case Tkhd: // 今回はこれがいる。
			tkhd = new Tkhd(position, size);
			tkhd.analyze(ch);
			ch.position(position + size);
			return tkhd;
		case Mdia:
			Mdia mdia = new Mdia(position, size);
			mdia.analyze(ch, this);
			ch.position(position + size);
			return mdia;
		case Mdhd:
			mdhd = new Mdhd(position, size);
			mdhd.analyze(ch, null);
			ch.position(position + size);
			if(tkhd.getHeight() != 0 && tkhd.getWidth() != 0) {
				meta = new Meta(this.trakStartPos, 28);
				meta.setHeight(tkhd.getHeight());
				meta.setWidth(tkhd.getWidth());
				meta.setDuration(mdhd.getDuration() * 1000 / mdhd.getTimescale());
			}
			return mdhd;
/*		case Hdlr:
			Hdlr hdlr = new Hdlr(position, size);
			ch.position(position + size);
			return hdlr;*/
		case Minf:
			Minf minf = new Minf(position, size);
			minf.analyze(ch, this);
			ch.position(position + size);
			return minf;
		case Vmhd:
			this.type = CurrentType.VIDEO;
			this.vdeo = new Vdeo(this.trakStartPos, 0);
			this.vdeo.setTimescale(mdhd.getTimescale());
			this.vdeo.writeIndex(idx.getChannel());
			Vmhd vmhd = new Vmhd(position, size);
			ch.position(position + size);
			return vmhd;
		case Smhd:
			this.type = CurrentType.AUDIO;
			this.sond = new Sond(this.trakStartPos, 0);
			this.sond.setTimescale(mdhd.getTimescale());
			this.sond.writeIndex(idx.getChannel());
			Smhd smhd = new Smhd(position, size);
			ch.position(position + size);
			return smhd;
/*		case Dinf:
			Dinf dinf = new Dinf(position, size);
			ch.position(position + size);
			return dinf;*/
		case Stbl:
			Stbl stbl = new Stbl(position, size);
			stbl.analyze(ch, this);
			ch.position(position + size);
			return stbl;
		case Stsd:
			Stsd stsd = new Stsd(position, size);
			try {
				stsd.analyze(ch, new RecordAnalyzer());
				// 適合している場合はmshを取り出す。
				for(Record record : stsd.getRecords()) {
					if(record instanceof H264) {
						H264 h264 = (H264)record;
						Avcc avcc = h264.getAvcc();
						// そのままコピーしておく。
						buffer = ByteBuffer.allocate(8);
						buffer.putInt(avcc.getSize());
						buffer.put("msh ".getBytes());
						buffer.flip();
						idx.getChannel().write(buffer);
						ch.position(avcc.getPosition() + 8);
						BufferUtil.quickCopy(ch, idx.getChannel(), avcc.getSize() - 8);
					}
					else if(record instanceof Aac) {
						// どうやらavconvでmp3に変換したらrecordタグはmp4aになるみたい。
						// その場合mshはnullになってしまう。
						Aac aac = (Aac)record;
						// TODO この上書きの部分が少々気に入らない
						// このタイミングでsondの中にデータをいれておく。
						System.out.println("sampleRate:" + aac.getSampleRate());
						System.out.println("channels:" + aac.getChannelCount());
						long prevPos = idx.getChannel().position();
						idx.getChannel().position(sond.getPosition() + 24);
						buffer = ByteBuffer.allocate(5);
						buffer.putInt(aac.getSampleRate());
						buffer.put((byte)aac.getChannelCount());
						buffer.flip();
						idx.getChannel().write(buffer);
						idx.getChannel().position(prevPos);
						byte[] data = aac.getEsds().getSequenceHeader();
						if(data != null) {
							buffer = ByteBuffer.allocate(8 + data.length);
							buffer.putInt(8 + data.length);
							buffer.put("msh ".getBytes());
							buffer.put(data);
							buffer.flip();
							idx.getChannel().write(buffer);
						}
					}
				}
			}
			catch(Exception e) {
				this.type = null;
				e.printStackTrace();
				// 適合していない場合は開始位置までfileを削っておく
				idx.getChannel().truncate(trakStartPos);
				idx.getChannel().position(trakStartPos);
				return null;
			}
			ch.position(position + size);
			return stsd;
		case Stts:
			Stts stts = new Stts(position, size);
			buffer.position(0);
			idx.getChannel().write(buffer);
			BufferUtil.quickCopy(ch, idx.getChannel(), size - 8);
			ch.position(position + size);
			return stts;
		case Stss: // keyFrameのデータになる必須
			// 映像の場合はkeyFrame指示になるっぽい
			Stss stss = new Stss(position, size);
			buffer.position(0);
			idx.getChannel().write(buffer);
			BufferUtil.quickCopy(ch, idx.getChannel(), size - 8);
			ch.position(position + size);
			return stss;
		case Stsc:
			// 各チャンクのサンプル量
			Stsc stsc = new Stsc(position, size);
			buffer.position(0);
			idx.getChannel().write(buffer);
			BufferUtil.quickCopy(ch, idx.getChannel(), size - 8);
			ch.position(position + size);
			return stsc;
		case Stsz:
			// サンプルのサイズ量
			Stsz stsz = new Stsz(position, size);
			buffer.position(0);
			idx.getChannel().write(buffer);
			// この処理の部分を書き換えてサイズの計算を実施しておくべき。
			// コピーしつつサイズの合計を調べておく。
			BufferUtil.quickCopy(ch, idx.getChannel(), size - 8);
			ch.position(position + size);
			return stsz;
		case Stco:
			// 各チャンクの開始位置
			Stco stco = new Stco(position, size);
			buffer.position(0);
			idx.getChannel().write(buffer);
			BufferUtil.quickCopy(ch, idx.getChannel(), size - 8);
			ch.position(position + size);
			return stco;
/*		case Mdat:
			Mdat mdat = new Mdat(position, size);
			// mdatの位置を考えることでmoovが後ろにあるmp4でも対応できるようになる。(ただし処理がおそくなる)
			ch.position(position + size);
			return mdat;*/
		default:
			break;
		}
		ch.position(position + size);
		return new Atom(tag, position, size) {
			@Override
			public void analyze(IReadChannel ch, IAtomAnalyzer analyzer)
					throws Exception {
				;
			}
		};
	}
	public void updatePrevTag() throws Exception {
		if(this.type == CurrentType.AUDIO
		|| this.type == CurrentType.VIDEO) {
			// いままでよんできたデータが正しいtagだった場合
			int prevPosition = (int)idx.getChannel().position();
			int prevSize = prevPosition - this.trakStartPos;
			ByteBuffer buf = ByteBuffer.allocate(4);
			buf.putInt(prevSize);
			buf.flip();
			idx.getChannel().position(trakStartPos);
			idx.getChannel().write(buf);
			idx.getChannel().position(prevPosition);
		}
		if(meta != null) {
			// metaデータを書き込んでおく。
			meta.writeIndex(idx.getChannel());
//			meta = null;
		}
	}
	/**
	 * 各要素の有用データ量を確認しておく
	 */
	public void checkDataSize() throws Exception {
		IReadChannel tmp = FileReadChannel.openFileReadChannel(targetFile.getAbsolutePath());
		while(tmp.position() < tmp.size()) {
			int position = tmp.position();
			ByteBuffer buffer = BufferUtil.safeRead(tmp, 8);
			int size = buffer.getInt();
			String tag = BufferUtil.getDwordText(buffer);
			if("vdeo".equals(tag)) {
				Vdeo vdeo = new Vdeo(position, size);
				vdeo.analyze(tmp);
				// 読み込み可能データ量を調べる
				vdeo.getStco().start(tmp, false); // dataPos
				vdeo.getStsc().start(tmp, false); // samples in chunk
				vdeo.getStsz().start(tmp, false); // sample size
				vdeo.getStts().start(tmp, false);
				int totalSize = 0;
				int sampleCount = 0;
				while(vdeo.getStco().nextChunkPos() != -1) {
					vdeo.getStsc().nextChunk();
					int chunkSampleCount = vdeo.getStsc().getSampleCount();
					for(int i = 0;i < chunkSampleCount;i ++) {
						int sampleSize = vdeo.getStsz().nextSampleSize();
						if(sampleSize == -1) {
							break;
						}
						sampleCount ++;
						totalSize += sampleSize;
						if(vdeo.getStts().nextDuration() == -1) {
							break;
						}
					}
				}
				idx.getChannel().position(vdeo.getPosition() + 12);
				BufferUtil.writeInt(idx.getChannel(), sampleCount);
				BufferUtil.writeInt(idx.getChannel(), totalSize);
				tmp.position(position + size);
			}
			else if("sond".equals(tag)) {
				Sond sond = new Sond(position, size);
				sond.analyze(tmp);
				sond.getStco().start(tmp, false); // dataPos
				sond.getStsc().start(tmp, false); // samples in chunk
				sond.getStsz().start(tmp, false); // sample size
				sond.getStts().start(tmp, false);
				int totalSize = 0;
				int sampleCount = 0;
				while(sond.getStco().nextChunkPos() != -1) {
					sond.getStsc().nextChunk();
					int chunkSampleCount = sond.getStsc().getSampleCount();
					for(int i = 0;i < chunkSampleCount;i ++) {
						int sampleSize = sond.getStsz().nextSampleSize();
						if(sampleSize == -1) {
							break;
						}
						sampleCount ++;
						totalSize += sampleSize;
						if(sond.getStts().nextDuration() == -1) {
							break;
						}
					}
				}
				idx.getChannel().position(sond.getPosition() + 12);
				BufferUtil.writeInt(idx.getChannel(), sampleCount);
				BufferUtil.writeInt(idx.getChannel(), totalSize);
				tmp.position(position + size);
			}
			else {
				tmp.position(position + size);
			}
		}
		tmp.close();
	}
	public Meta getMeta() {
		return meta;
	}
	public void close() {
		if(idx != null) {
			try {
				idx.close();
			}
			catch(Exception e) {
			}
			idx = null;
		}
	}
}
