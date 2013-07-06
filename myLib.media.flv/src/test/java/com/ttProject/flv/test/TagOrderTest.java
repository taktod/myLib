package com.ttProject.flv.test;

import org.junit.Test;

import com.ttProject.media.flv.FlvTagOrderManager;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.MetaTag;
import com.ttProject.media.flv.tag.VideoTag;

/**
 * flvのtagを適当にいれていったときに順番に抜き出せるか動作テスト
 * @author taktod
 */
public class TagOrderTest {
/*	@Test
	public void orderTest() throws Exception {
		FlvTagOrderManager manager = new FlvTagOrderManager();
		Tag tag = new VideoTag();
		tag.setTimestamp(10);
		manager.addTag(tag);
		tag = new AudioTag();
		tag.setTimestamp(10);
		manager.addTag(tag);
		tag = new MetaTag();
		tag.setTimestamp(10);
		manager.addTag(tag);
		tag = new VideoTag();
		tag.setTimestamp(30);
		manager.addTag(tag);
		tag = new AudioTag();
		tag.setTimestamp(20);
		manager.addTag(tag);
		tag = new VideoTag();
		tag.setTimestamp(40);
		manager.addTag(tag);
		tag = new AudioTag();
		tag.setTimestamp(40);
		manager.addTag(tag);
		tag = new AudioTag();
		tag.setTimestamp(80);
		manager.addTag(tag);
		manager.setNomoreAudio();
		System.out.println(manager.getCompleteTags());
		System.out.println(manager);

		tag = new VideoTag();
		tag.setTimestamp(50);
		manager.addTag(tag);
		System.out.println(manager.getCompleteTags());
		System.out.println(manager);

		tag = new VideoTag();
		tag.setTimestamp(100);
		manager.addTag(tag);
		System.out.println(manager.getCompleteTags());
		System.out.println(manager);
	}*/
}
