package com.ttProject.flazr.test;

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
			System.out.print(aTag);
			System.out.print(" : ");
			System.out.println(aTag.getCodec());
			if(aTag.getCodec() == CodecType.MP3) {
				try {
					System.out.println(HexUtil.toHex(aTag.getBuffer(), 0, 30, true));
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
			System.out.println(tag);
		}
	}

}
