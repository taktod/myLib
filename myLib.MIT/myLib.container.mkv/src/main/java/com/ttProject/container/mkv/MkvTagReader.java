package com.ttProject.container.mkv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.Reader;
import com.ttProject.container.mkv.type.TimecodeScale;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.container.mkv.type.Tracks;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mkvデータを解析します。(内容データもばっちり解析する予定)
 * @author taktod
 */
public class MkvTagReader extends Reader {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvTagReader.class);
	// TODO ここで必要なインスタンスを保持しておいて、参照できるようにする必要がある
	private long defaultTimebase = 1000;
	private Tracks tracks; // TrackIDcodec情報とcodecPrivate、FlagLacing videoWidth videoHeight Channels SampleFrequency BitDepthあたりが必要
	private Map<Integer, TrackEntry> trackEntryMap = new ConcurrentHashMap<Integer, TrackEntry>();
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
		MkvTag tag = (MkvTag)getSelector().select(channel);
		if(tag != null) {
			tag.setMkvTagReader(this);
			tag.load(channel);
		}
		if(tag instanceof TimecodeScale) {
			defaultTimebase = ((TimecodeScale) tag).getTimebaseValue();
		}
		if(tag instanceof TrackEntry) {
			TrackEntry trackEntry = (TrackEntry)tag;
			int id = trackEntry.setupEntry(defaultTimebase);
			trackEntryMap.put(id, trackEntry);
		}
		if(tag instanceof Tracks) {
			tracks = (Tracks)tag;
		}
		return tag;
	}
	public void showData() {
		if(tracks == null) {
			logger.info("tracksがありません");
		}
		else {
			showData("", tracks);
		}
	}
	public void showData(String space, MkvTag tag) {
		if(tag instanceof MkvMasterTag) {
			logger.info(tag.toString(space));
			for(MkvTag t : ((MkvMasterTag) tag).getChildList()) {
				if(t instanceof MkvMasterTag) {
					showData(space + "  ", t);
				}
				else {
					logger.info(t.toString(space + "  "));
				}
			}
		}
	}
}
