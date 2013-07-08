package com.ttProject.flv.test;

import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;

public class FlvAnalyzeTest {
	@Test
	public void readTest() throws Exception {
/*		System.out.println("flvのデータを読み込むテスト");
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
			Thread.currentThread().getContextClassLoader().getResource("target.flv")
//			"/home/taktod/デスクトップ/xtest/mario.flv"
		);
		// ヘッダ情報を読み込んでおく。
		FlvHeader flvHeader = new FlvHeader();
		flvHeader.analyze(source);
		System.out.println(flvHeader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		try {
			Tag tag = null;
			while((tag = analyzer.analyze(source)) != null) {
				System.out.println(tag);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		source.close();*/
	}
}
