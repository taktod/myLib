package com.ttProject.media.mpegts.test;

import java.io.FileOutputStream;

import org.junit.Test;

import com.ttProject.media.mpegts.IPacketAnalyzer;
import com.ttProject.media.mpegts.Packet;
import com.ttProject.media.mpegts.PacketAnalyzer;
import com.ttProject.media.mpegts.field.AdaptationField;
import com.ttProject.media.mpegts.packet.Pes;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * avconvでつくったhls出力のmpegtsを結合して一本のtsデータでhlsするプログラム作成用テスト
 * @author taktod
 *
 */
public class ByteRangeHlsCreateTest {
//	private Logger logger = Logger.getLogger(ByteRangeHlsCreateTest.class);
	@Test
	public void test() {
		FileOutputStream master = null;
		try {
			// 母艦
			master = new FileOutputStream("/Users/todatakahiko/tmp/rtype/rtype.ts");
			/*
			 * こんなのをつくりたいところ。
			 * #EXTM3U
			 * #EXT-X-TARGETDURATION:11
			 * #EXT-X-MEDIA-SEQUENCE:0
			 * #EXT-X-VERSION:4
			 * #EXTINF:10.0,
			 * #EXT-X-BYTERANGE:75232@0
			 * media.ts
			 * #EXTINF:10.0,
			 * #EXT-X-BYTERANGE:82112@752321
			 * media.ts
			 * #EXT-X-ENDLIST
			 */
			System.out.println("#EXTM3U");
			System.out.println("#EXT-X-TARGETDURATION:10");
			System.out.println("#EXT-X-MEDIA-SEQUENCE:0");
			System.out.println("#EXT-X-VERSION:4");
			// xinfの長さを計算する必要あり。
			// byteRangeのアクセス長も計算する必要あり。ほしいのは、前のデータの開始位置と読み込むべきbyte数
			// rtype.ts
			int lastPos = 0; // 最後の終端の位置
			float duration = 0; // 出力duration(次のデータのpcrから計算するべし)
			float nextDuration = 0;
			int size = 0;
			for(int i = 0;i < 327;i ++) {
				IFileReadChannel source = FileReadChannel.openFileReadChannel(
						"/Users/todatakahiko/tmp/rtype/rtype" + i + ".ts"
				);
				// サイズが知りたい
//				logger.info(source.size());
				// 長さが知りたい
				IPacketAnalyzer analyzer = new PacketAnalyzer();
				Packet packet = null;
				while((packet = analyzer.analyze(source)) != null) {
					if(packet instanceof Pes) {
						Pes pes = (Pes)packet;
						if(pes.isAdaptationFieldExist()) {
							AdaptationField aField = pes.getAdaptationField();
							if(aField.hasPcr()) {
								// 前のデータについて出力する。
//								logger.info("time:" + aField.getPcrBase() / 90000D);
								nextDuration = aField.getPcrBase() / 90000F;
								if(i != 0) { // 0番目は出力しない。
									System.out.println("#EXTINF:" + (nextDuration - duration));
									System.out.println("#EXT-X-BYTERANGE:" + size + "@" + lastPos);
									System.out.println("rtype.ts");
								}
								duration = nextDuration;
								size = source.size();
								break;
							}
						}
					}
				}
				// はじめに戻す
				source.position(0);
				lastPos = (int)master.getChannel().position();
				// 全コピー
				BufferUtil.quickCopy(source, master.getChannel(), source.size());
				source.close();
			}
			// 最後のデータについては計算できないので、注意が必要
			System.out.println("#EXT-X-ENDLIST");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(master != null) {
				try {
					master.close();
				}
				catch(Exception e) {}
				master = null;
			}
		}
	}
}
