package com.ttProject.flazr.test;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.io.flv.FlvAtom;
import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpReader;
import com.flazr.rtmp.message.Metadata;
import com.flazr.rtmp.message.MetadataAmf0;
import com.ttProject.flazr.TagManager;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

public final class FlvTestReader implements RtmpReader {
	private Logger logger = LoggerFactory.getLogger(FlvTestReader.class);
	private final LinkedBlockingQueue<FlvAtom> dataQueue = new LinkedBlockingQueue<FlvAtom>();
	private Metadata metadata;
	private int aggregateDuration;
	private final Thread readThread;
	public FlvTestReader() {
		readThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// flvデータの読み込みを実施してデータを送り込む
				logger.info("読み込み処理開始");
				try {
					IReadChannel channel = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
					FlvHeader header = new FlvHeader();
					header.analyze(channel);
					ITagAnalyzer analyzer = new TagAnalyzer();
					Tag tag = null;
					TagManager manager = new TagManager();
					while((tag = analyzer.analyze(channel)) != null) {
						FlvAtom atom = manager.getAtom(tag);
						dataQueue.add(atom);
						logger.info(tag.toString());
					}
				}
				catch (Exception e) {
				}
			}
		});
		readThread.start();
		metadata = new MetadataAmf0("onMetaData");
	}
	@Override
	public void close() {
		readThread.interrupt();
	}

	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	@Override
	public RtmpMessage[] getStartMessages() {
		return new RtmpMessage[]{metadata};
	}

	@Override
	@Deprecated
	public long getTimePosition() {
		// live動作なのでシーク禁止
		throw new RuntimeException("getTimePositionが呼ばれました。");
	}

	@Override
	public boolean hasNext() {
		return true;
	}

	@Override
	public RtmpMessage next() {
		if(aggregateDuration <= 0) {
			try {
				return dataQueue.take();
			}
			catch (Exception e) {
				throw new RuntimeException("takeに失敗したよ");
			}
		}
		else {
			throw new RuntimeException("aggregateDurationが0よりおおきいです。");
		}
	}

	@Override
	@Deprecated
	public long seek(long time) {
		throw new RuntimeException("seekが呼ばれました。");
	}

	@Override
	public void setAggregateDuration(int targetDuration) {
		aggregateDuration = targetDuration;

	}
}
