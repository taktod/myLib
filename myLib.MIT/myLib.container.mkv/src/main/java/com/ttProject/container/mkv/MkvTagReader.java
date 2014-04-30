package com.ttProject.container.mkv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.NullContainer;
import com.ttProject.container.Reader;
import com.ttProject.container.mkv.type.Cluster;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.container.mkv.type.Timecode;
import com.ttProject.container.mkv.type.TimecodeScale;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mkvデータを解析します。(内容データもばっちり解析する予定)
 * @author taktod
 */
public class MkvTagReader extends Reader {
	/** ロガー */
//	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvTagReader.class);
	private Map<Integer, TrackEntry> trackEntryMap = new ConcurrentHashMap<Integer, TrackEntry>();
	private long defaultTimebase = 1000;
	private long clusterTime = 0;
	/**
	 * コンストラクタ
	 */
	public MkvTagReader() {
		super(new MkvTagSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IContainer container = (IContainer)getSelector().select(channel);
		MkvTag tag = null;
		if(container != null) {
			if(container instanceof NullContainer) {
				return container;
			}
			tag = (MkvTag)container;
			tag.setMkvTagReader(this);
			if(!(tag instanceof Cluster) && !(tag instanceof Segment)) {
				// clusterとsegmentの読み込みをスキップすることで、simpleBlockのデータを応答するようにしておく
				tag.load(channel);
			}
			logger.info(container);
		}
		if(tag instanceof TimecodeScale) {
			defaultTimebase = ((TimecodeScale) tag).getTimebaseValue();
		}
		if(tag instanceof TrackEntry) {
			TrackEntry trackEntry = (TrackEntry)tag;
			int id = trackEntry.setupEntry(defaultTimebase);
			trackEntryMap.put(id, trackEntry);
		}
		if(tag instanceof Timecode) {
			clusterTime = ((Timecode)tag).getValue();
		}
		return tag;
	}
	public TrackEntry getTrackEntry(int trackId) {
		return trackEntryMap.get(trackId);
	}
	public long getClusterTime() {
		return clusterTime;
	}
}
