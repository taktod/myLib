package com.ttProject.media.h264.test;

import java.util.List;

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
	@Test
	public void test() throws Exception {
		// h264データの読み込みテストを実施します。
		// ただしh264を読み込む適当なフォーマットがないので、flvからデータを読み込むことにします。
		IFileReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("mario.nosound.flv")
		);
		FlvHeader flvHeader = new FlvHeader();
		flvHeader.analyze(source);
		System.out.println(flvHeader);
		ITagAnalyzer analyzer = new TagAnalyzer();
		// sourceを解析していく
		Tag tag = null;
		try {
			while((tag = analyzer.analyze(source)) != null) {
				if(tag instanceof VideoTag) {
					VideoTag vTag = (VideoTag) tag;
					if(vTag.getCodec() == CodecType.AVC) {
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
								System.out.println(HexUtil.toHex(frame.getData(), 0, 3, true));
							}
						}
						else {
							// 内容を解析して、mpegtsとして使えるIDRSliceとsliceがとれていることを願う
							DataNalAnalyzer dataNalAnalyzer = new DataNalAnalyzer();
							IReadChannel dataChannel = new ByteReadChannel(vTag.getRawData());
							dataChannel.position(3);
							Frame frame = dataNalAnalyzer.analyze(dataChannel);
							System.out.println(HexUtil.toHex(frame.getData(), 0, 5, true));
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		source.close();
	}
}
