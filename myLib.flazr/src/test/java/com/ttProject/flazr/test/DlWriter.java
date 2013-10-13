package com.ttProject.flazr.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpWriter;
import com.ttProject.flazr.MessageManager;
import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AggregateTag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.util.HexUtil;

/**
 * データをdownloadするwriter
 * @author taktod
 */
public class DlWriter implements RtmpWriter {
	private Logger logger = LoggerFactory.getLogger(DlWriter.class);
	@Override
	public void close() {

	}

	@Override
	public void write(RtmpMessage message) {
		MessageManager manager = new MessageManager();
		Tag tag = manager.getTag(message);
		// aggregateTagの場合はtagに戻す必要あり。
		if(tag instanceof AggregateTag) {
			AggregateTag aTag = (AggregateTag)tag;
			for(Tag t : aTag.getList()) {
				check(t);
			}
		}
		else {
			check(tag);
		}
	}
	public void check(Tag tag) {
		if(tag instanceof AudioTag) {
			AudioTag aTag = (AudioTag) tag;
			logger.info("{}", aTag);
			logger.info(" : ");
			logger.info("{}", aTag.getCodec());
			if(aTag.getCodec() == CodecType.MP3) {
				try {
					logger.info(HexUtil.toHex(aTag.getBuffer(), 0, 30, true));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Exception e = new Exception("a");
				e.printStackTrace();
				System.exit(0);
			}
		}
		else {
			logger.info(tag.toString());
		}
	}

}
