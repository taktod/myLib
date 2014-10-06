/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.h264.SliceFrame;
import com.ttProject.unit.UnitComparator;
import com.ttProject.unit.extra.EbmlValue;

/**
 * Cluster
 * @author taktod
 */
public class Cluster extends MkvMasterTag {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Cluster.class);
	/** duration of this cluster */
	private long duration;
	/** trackIds on process. */
	private Set<Integer> trackIdSet = new HashSet<Integer>();
	/** list of blocks. */
	private List<SimpleBlock> blockList = new ArrayList<SimpleBlock>();
	/** for sort. */
	private static UnitComparator comparator = new UnitComparator();
	/**
	 * constructor
	 * @param size
	 */
	public Cluster(EbmlValue size) {
		super(Type.Cluster, size);
	}
	/**
	 * constructor
	 */
	public Cluster() {
		this(new EbmlValue());
	}
	/**
	 * constructor
	 * @param position
	 */
	public Cluster(long position) {
		this();
		setPosition((int)position);
	}
	/**
	 * set the position.
	 * @param position
	 */
	public void setPosition(long position) {
		super.setPosition((int)position);
	}
	/**
	 * setup timeinformation.
	 * @param pts
	 * @param timebase
	 * @param duration
	 * @throws Exception
	 */
	public void setupTimeinfo(long pts, long timebase, long duration) throws Exception {
		setPts(pts);
		setTimebase(timebase);
		this.duration = duration;
		Timecode timecode = new Timecode();
		timecode.setValue(pts);
		addChild(timecode);
	}
	/**
	 * check trackId(sign as progress trackId)
	 * @param trackId
	 */
	public void checkTrackId(int trackId) {
		trackIdSet.add(trackId);
	}
	/**
	 * add frame.
	 * @param trackId
	 * @param frame
	 * @return IFrame If not added, return frame. if added, return null.
	 */
	public IFrame addFrame(int trackId, IFrame frame) throws Exception {
		// TODO can be complete before all track passed.
		// check the pts, in cluster or not.
		int pts = (int)(getTimebase() * frame.getPts() / frame.getTimebase() - getPts());
		if(pts <= 0) {
			return null;
		}
		if(pts >= 0 && pts < duration) {
			// inside.
			setupSimpleBlock(trackId, frame, pts);
			return null;
		}
		trackIdSet.remove((Integer)trackId);
		return frame;
	}
	/**
	 * hold data as simpleBlock.
	 * @param trackId
	 * @param frame
	 * @throws Exception
	 */
	private void setupSimpleBlock(int trackId, IFrame frame, int clusterPts) throws Exception {
		switch(frame.getCodecType()) {
		case H264:
			// h264 deal with only sliceFrame.(need to deal with sei?)
			if(!(frame instanceof SliceFrame)) {
				return;
			}
			break;
		default:
			break;
		}
		SimpleBlock simpleBlock = new SimpleBlock();
		simpleBlock.addFrame(trackId, frame, clusterPts);
		blockList.add(simpleBlock);
	}
	/**
	 * check the exist of progress trackIds.
	 * @return
	 */
	public boolean isCompleteCluster() {
		return trackIdSet.isEmpty();
	}
	/**
	 * setup complete the cluster.
	 */
	public void setupComplete() {
		Collections.sort(blockList, comparator);
		for(MkvBlockTag blockTag : blockList) {
			addChild(blockTag);
		}
		blockList.clear();
	}
}
