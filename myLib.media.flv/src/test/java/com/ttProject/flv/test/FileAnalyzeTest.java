package com.ttProject.flv.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.FlvManager;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * flvの解析テスト
 * @author taktod
 */
public class FileAnalyzeTest {
	/**
	 * ファイル全体を解析するテスト
	 */
	@Test
	public void fixedFileTest() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvheader = new FlvHeader();
		flvheader.analyze(source);
		System.out.println(flvheader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		// sourceをそのまま解析する。
		Tag tag = null;
		while((tag = analyzer.analyze(source)) != null) {
			System.out.println(tag);
		}
		source.close();
	}
	/**
	 * サイズがわかっていないデータを順に受け取るときにflvを解析する動作テスト
	 */
	@Test
	public void appendingBufferTest() throws Exception {
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvheader = new FlvHeader();
		flvheader.analyze(source);
		System.out.println(flvheader);
		ByteBuffer buffer = BufferUtil.safeRead(source, 2560);
		FlvManager manager = new FlvManager();
		for(Tag tag : manager.getUnits(buffer)) {
			System.out.println(tag);
		}
		buffer = BufferUtil.safeRead(source, 2560);
		for(Tag tag : manager.getUnits(buffer)) {
			System.out.println(tag);
		}
		source.close();
	}
}
