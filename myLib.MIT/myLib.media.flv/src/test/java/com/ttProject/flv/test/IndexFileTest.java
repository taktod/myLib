package com.ttProject.flv.test;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.model.FlvOrderModel;
import com.ttProject.media.flv.model.IndexFileCreator;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * flvの高速seekのためのindexFile作成テスト
 * @author taktod
 */
public class IndexFileTest {
	@Test
	public void test() throws Exception {
		IFileReadChannel target = null;
		IndexFileCreator idx = null;
		FlvOrderModel flvOrderModel = null;
		try {
			target = FileReadChannel.openFileReadChannel(
				"http://49.212.39.17/rtypeDelta.flv");
			idx = new IndexFileCreator(new File("test.tmp"), target);
			// 初期セットアップを実行
			idx.initSetup();
			System.out.println(idx);
			// このあと解析を実行します。
			flvOrderModel = new FlvOrderModel(idx, true, true, 0);
			flvOrderModel.initialize(target);
			// 解析を実施します。
			List<Tag> list = null;
			while((list = flvOrderModel.nextTagList(target)) != null) {
				for(Tag tag : list) {
					System.out.println(tag);
				}
				Thread.sleep(100);
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(flvOrderModel != null) {
				flvOrderModel.close();
				flvOrderModel = null;
			}
			if(idx != null) {
				idx.close();
				idx = null;
			}
			if(target != null) {
				target.close();
				target = null;
			}
		}
	}
}
