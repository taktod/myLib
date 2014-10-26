package com.ttProject.container.mpegts.test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.CodecType;

public class MapWorkTest {
	private Logger logger = Logger.getLogger(MapWorkTest.class);
	@Test
	public void test() {
		logger.info("map test.");
		// should use linkedHashMap
		Map<Integer, CodecType> pidMap = new LinkedHashMap<Integer, CodecType>();
		pidMap.put(5,  CodecType.AAC);
		pidMap.put(10, CodecType.H264);
		pidMap.put(15, CodecType.H264);
		pidMap.put(20, CodecType.H264);
		pidMap.put(25, CodecType.MP3);
		CodecType targetType = CodecType.H264;
		Integer findPid = null;
		for(Entry<Integer, CodecType> entry : pidMap.entrySet()) {
			if(entry.getValue() == targetType) {
				findPid = entry.getKey();
				break;
			}
		}
		logger.info("find pid:" + findPid);
		pidMap.remove(findPid);
		logger.info(pidMap);
	}
}
