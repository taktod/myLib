package com.ttProject.media.h264.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.DataNalAnalyzer;
import com.ttProject.media.h264.Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class FileAnalyzeTest {
	private Logger logger = Logger.getLogger(FileAnalyzeTest.class);
	@Test
	public void test() throws Exception {
		// h264データの読み込みテストを実施します。
		// ただしh264を読み込む適当なフォーマットがないので、flvからデータを読み込むことにします。
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.flv")
		);
		FlvHeader flvHeader = new FlvHeader();
		flvHeader.analyze(source);
		logger.info(flvHeader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		// sourceを解析していく
		Tag tag = null;
		DataNalAnalyzer dataNalAnalyzer = new DataNalAnalyzer();
		try {
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof VideoTag) {
					VideoTag vTag = (VideoTag) tag;
					if(vTag.getCodec() == CodecType.AVC) {
						if(vTag.isEndOfSequence()) {
							// シーケンス終了時の場合
							break;
						}
						// h.264だったら読み込んでやっておく。
						if(vTag.isMediaSequenceHeader()) {
							// mshの場合はデータがmshになっているはずなので、解析する必要がある。
							// この内容をConfigDataに流してspsとppsを取得する必要あり。
							ConfigData configData = new ConfigData();
							IReadChannel configChannel = new ByteReadChannel(vTag.getRawData());
							configChannel.position(3);
							List<Frame> frames = configData.getNals(configChannel);
							for(Frame frame : frames) {
								// spsとppsがとれているはず。
								logger.info(HexUtil.toHex(frame.getData(), 0, 3, true));
							}
						}
						else {
							// 内容を解析して、mpegtsとして使えるIDRSliceとsliceがとれていることを願う
							IReadChannel dataChannel = new ByteReadChannel(vTag.getRawData());
							dataChannel.position(3);
							Frame frame = dataNalAnalyzer.analyze(dataChannel);
							logger.info(HexUtil.toHex(frame.getData(), 0, 5, true));
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Assert.fail("例外が発生しました。");
		}
		source.close();
	}
}
