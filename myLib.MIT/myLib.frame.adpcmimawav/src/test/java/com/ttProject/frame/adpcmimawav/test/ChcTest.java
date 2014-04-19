package com.ttProject.frame.adpcmimawav.test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.util.BufferUtil;

/**
 * ハフマン符号化について調べてみる。
 * adpcmのnibbleって0〜fまでしかありえないので、なんとかなるんちゃうん？
 * @author taktod
 */
public class ChcTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ChcTest.class);
	/**
	 * とりあえずどの文字が一番よくでてくるかというデータがほしいね
	 * @throws Exception
	 */
//	@Test
	public void test() throws Exception {
		logger.info("チェック開始");
		IReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("bm_mono.wav")
//				Thread.currentThread().getContextClassLoader().getResource("test_mono.wav")
//				Thread.currentThread().getContextClassLoader().getResource("rtype_mono.wav")
		);
		channel.position(0x5C);
		int[] order = new int[16];
		// ここから、読み込んでいく。
		while(channel.position() < channel.size()) {
			// とりあえずどの符号が一番おおくでてくるか調べていきたい。
			ByteBuffer buffer = BufferUtil.safeRead(channel, 0x400);
			buffer.position(4);
			int[] result = new int[16];
			while(buffer.remaining() > 0) {
				byte data = buffer.get();
				result[(data & 0x0F)] ++;
				result[((data >> 4) & 0x0F)] ++;
			}
			int[] sortMap = Arrays.copyOf(result, result.length);
			Arrays.sort(sortMap);
			// 一番出現率の高いデータをみつけて、順番に並べる
			StringBuilder data = new StringBuilder();
			for(int j = 0;j < 16;j ++) {
				for(int i = 0;i < 16;i ++) {
					if(sortMap[j] == result[i]) {
						if(j == 15) {
							order[i] ++;
						}
						data.append(Integer.toHexString(i)).append(":").append(result[i]).append(" ");
						result[i] = 99999;
						break;
					}
				}
			}
			logger.info(data.toString());
		}
		StringBuilder dat = new StringBuilder();
		for(int i = 0;i < 16;i ++) {
			dat.append(i).append(":").append(order[i]).append(" ");
		}
		logger.info("最終1位回数メモ");
		logger.info(dat.toString());
	}
	/*
13:09:21,369 [main] INFO [ChcTest] - 最終1位回数メモ
13:09:21,369 [main] INFO [ChcTest] - 0:16352 1:4217 2:4747 3:2926 4:0 5:0 6:0 7:0 8:11209 9:4878 10:5688 11:2906 12:0 13:0 14:0 15:0 

13:25:41,307 [main] INFO [ChcTest] - 最終15位回数メモ
13:25:41,307 [main] INFO [ChcTest] - 0:0 1:2 2:2079 3:253 4:7 5:23 6:10873 7:16504 8:0 9:0 10:0 11:0 12:0 13:7 14:9391 15:13784 

18:39:57,141 [main] INFO [ChcTest] - 最終1位回数メモ
18:39:57,142 [main] INFO [ChcTest] - 0:47170 1:21390 2:7042 3:5067 4:0 5:0 6:0 7:0 8:42730 9:23854 10:7446 11:4980 12:1 13:0 14:0 15:0 

18:38:38,436 [main] INFO [ChcTest] - 最終15位回数メモ
18:38:38,436 [main] INFO [ChcTest] - 0:0 1:67 2:55 3:35 4:36 5:84 6:33375 7:50789 8:0 9:0 10:0 11:0 12:0 13:2 14:29492 15:45745 
	 */
	// 通常のadpcmの場合はlittleEndian動作をbigEndian動作に変更する。
	// ハフマン圧縮を掛ける場合は次のようにする。
	/*
	 * 1:     0 8
	 * 0111:  8 0
	 * 0110:  2 2
	 * 0101:  A A
	 * 01001: 1 1
	 * 01000: 3 3
	 * 00111: 4 4
	 * 00110: 5 5
	 * 00101: 9 9
	 * 00100: B B
	 * 00011: C C
	 * 00010: D D
	 * 000011:6 6
	 * 000010:7 7
	 * 000001:E E
	 * 000000:F F
	 */
	private Bit getHVal3(int data) {
		return new Bit4(data);
	}
	private Bit getHVal2(int data) {
		switch(data) {
		case 0x08:
			return new Bit1(1);
		case 0x00:
			return new Bit4(7);
		case 0x02:
			return new Bit4(6);
		case 0x0A:
			return new Bit4(5);

		case 0x01:
			return new Bit5(9);
		case 0x03:
			return new Bit5(8);
		case 0x04:
			return new Bit5(7);
		case 0x05:
			return new Bit5(6);
		case 0x09:
			return new Bit5(5);
		case 0x0B:
			return new Bit5(4);
		case 0x0C:
			return new Bit5(3);
		case 0x0D:
			return new Bit5(2);

		case 0x06:
			return new Bit6(3);
		case 0x07:
			return new Bit6(2);
		case 0x0E:
			return new Bit6(1);
		case 0x0F:
		default:
			return new Bit6(0);
		}
	}
	private Bit getHVal1(int data) {
		switch(data) {
		case 0x00:
			return new Bit1(1);
		case 0x08:
			return new Bit4(7);
		case 0x02:
			return new Bit4(6);
		case 0x0A:
			return new Bit4(5);

		case 0x01:
			return new Bit5(9);
		case 0x03:
			return new Bit5(8);
		case 0x04:
			return new Bit5(7);
		case 0x05:
			return new Bit5(6);
		case 0x09:
			return new Bit5(5);
		case 0x0B:
			return new Bit5(4);
		case 0x0C:
			return new Bit5(3);
		case 0x0D:
			return new Bit5(2);

		case 0x06:
			return new Bit6(3);
		case 0x07:
			return new Bit6(2);
		case 0x0E:
			return new Bit6(1);
		case 0x0F:
		default:
			return new Bit6(0);
		}
	}
//	@Test
	public void test2() throws Exception {
		logger.info("チェック開始");
		IReadChannel channel = FileReadChannel.openFileReadChannel(
//				Thread.currentThread().getContextClassLoader().getResource("test_mono.wav")
				Thread.currentThread().getContextClassLoader().getResource("rtype_mono.wav")
		);
		channel.position(0x5C);
		while(channel.position() < channel.size()) {
			BitConnector connector = null;
			Bit[] bits1 = new Bit[2040]; // 2040nibble存在するので、やってみる。
			Bit[] bits2 = new Bit[2040]; // 2040nibble存在するので、やってみる。
			Bit[] bits3 = new Bit[2040]; // 2040nibble存在するので、やってみる。
			ByteBuffer buffer = BufferUtil.safeRead(channel, 0x400);
			buffer.position(4);
			int i = 0;
			while(buffer.remaining() > 0) {
				byte data = buffer.get();
				bits1[i] = getHVal1(data & 0x0F);
				bits2[i] = getHVal2(data & 0x0F);
				bits3[i] = getHVal3(data & 0x0F);
				i ++;
				bits1[i] = getHVal1((data >> 4) & 0x0F);
				bits2[i] = getHVal2((data >> 4) & 0x0F);
				bits3[i] = getHVal3((data >> 4) & 0x0F);
				i ++;
			}
			connector = new BitConnector();
			logger.info("1:" + connector.connect(bits1).remaining());
			logger.info("2:" + connector.connect(bits2).remaining());
			logger.info("3:" + connector.connect(bits3).remaining());
		}
	}
	/**
	 * 思いついたadpcm_ttという圧縮形式
	 * 44100 kHz monoralのみ
	 * 通常のadpcm返還を実施後、huffman符号化による圧縮を実施する。
	 * 基本テーブルはtest2と同じだが、先に最もよくつかうデータと最も使わないデータを割り出しておく。
	 * 
	 * 1:     8
	 * 0111:  0
	 * 0110:  2
	 * 0101:  A
	 * 01001: 1
	 * 01000: 3
	 * 00111: 4
	 * 00110: 5
	 * 00101: 9
	 * 00100: B
	 * 00011: C
	 * 00010: D
	 * 000011:6
	 * 000010:7
	 * 000001:E
	 * 000000:F
	 * 
	 * あまりよくなかったので、次のmapをためしてみる。
	 * 111:  0
	 * 110:  8
	 * 1011: 1
	 * 1010: 2
	 * 1001: 3
	 * 1000: 4
	 * 0111: 5
	 * 0110: 9
	 * 0101: A
	 * 0100: B
	 * 0011: C
	 * 0010: D
	 * 00011:6
	 * 00010:7
	 * 00001:E
	 * 00000:F
	 * 全体的に圧縮かかるけど・・・
	 * 
	 * 111:   0
	 * 110:   1
	 * 101:   8
	 * 100:   9
	 * 0111:  2
	 * 0110:  3
	 * 0101:  4
	 * 0100:  A
	 * 0011:  B
	 * 0010:  C
	 * 00011: 5
	 * 00010: D
	 * 000011:6
	 * 000010:7
	 * 000001:E
	 * 000000:F
	 * あまりかわらず、でもまぁそこそこかな
	 * total:151 650 698 (7%) type1
	 * 
	 * 11:    8
	 * 1011:  0
	 * 1010:  1
	 * 1001:  2
	 * 1000:  3
	 * 0111:  4
	 * 0110:  5
	 * 0101:  9
	 * 0100:  A
	 * 0011:  B
	 * 0010:  C
	 * 0001:  D
	 * 000011:6
	 * 000010:7
	 * 000001:E
	 * 000000:F
	 * total:157 327 067 (4%) type2
	 * 
	 * 163 512 320(オリジナル)
	 * 
	 * もっともよく使うデータは8と入れ替え
	 * ただし8の場合は入れ替えは実施せず
	 * もっとも使わないデータは6と入れ替えとします。
	 * ただし、6,7,E,Fだった場合は入れ替え実施せず。
	 * 
	 * 出力データフォーマットは次のようにする。
	 * 基本adpcm ima wavと同じにする。
	 * 始めの2byteは初期振幅(predictor)
	 * 次の1byteは初期index
	 * 次の1byteはよく使うデータ + 最も使わないデータ(ただし、両値が一致する場合は通常のadpcmとします。)
	 * あとはnibbleの羅列(ただしadpcm_ima_wavとは違いbigendianにします。)
	 * test_mono
	 * 20480(オリジナル)
	 * 19087(type1) 7%
	 * 18160(type2) 11%
	 * 
	 * bm_mono
	 * 163 512 320(オリジナル)
	 * total:151 650 698 (7%) type1
	 * total:157 327 067 (4%) type2
	 * 
	 * rtype_mono
	 * 54 193 152 (original)
	 * 49 798 993 type1 8%
	 * 51 323 639 type2 5%
	 */
	@Test
	public void test3() throws Exception {
		logger.info("チェック開始");
		IReadChannel channel = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test_mono.wav")
//				Thread.currentThread().getContextClassLoader().getResource("bm_mono.wav")
//				Thread.currentThread().getContextClassLoader().getResource("rtype_mono.wav")
		);
		channel.position(0x5C);
		long length = 0;
		// ここからループする部分
		while(channel.position() < channel.size()) {
			Map<Integer, Bit> huffMap = new HashMap<Integer, Bit>(ttMap);
			ByteBuffer buffer = BufferUtil.safeRead(channel, 0x400);
			buffer.position(4);
			int[] result = new int[16];
			while(buffer.remaining() > 0) {
				byte data = buffer.get();
				result[(data & 0x0F)] ++;
				result[((data >> 4) & 0x0F)] ++;
			}
			int[] sortMap = Arrays.copyOf(result, result.length);
			Arrays.sort(sortMap); // データを調べる
			for(int j = 0;j < 16;j ++) {
				if(j == 0 || j == 15) {
					for(int i = 0;i < 16;i ++) {
						if(sortMap[j] == result[i]) {
							if(j == 0) {
								if(i == 6 || i == 7 || i == 0x0E || i == 0x0F) {
									continue;
								}
								// 最小値
								logger.info("min:" + i);
								if(i != 6) {
									Bit val_6 = huffMap.get(0x6);
									Bit val_min = huffMap.get(i);
									huffMap.put(i, val_6);
									huffMap.put(6, val_min);
								}
								break;
							}
							else if(j == 15) {
								// 最大値
								logger.info("max:" + i);
								if(i != 8) {
									Bit val_8 = huffMap.get(0x8);
									Bit val_max = huffMap.get(i);
									huffMap.put(i, val_8);
									huffMap.put(8, val_max);
								}
							}
						}
					}
				}
			}
			logger.info("huffMap:" + huffMap.toString());
			// このデータで圧縮してみる。
			buffer.position(4);
			Bit[] bits = new Bit[2040];
			int i = 0;
			while(buffer.remaining() > 0) {
				int data = buffer.get();
				bits[i ++] = huffMap.get(data & 0x0F);
				bits[i ++] = huffMap.get((data >> 4) & 0x0F);
			}
			BitConnector connector = new BitConnector();
			ByteBuffer buf = connector.connect(bits);
//			buffer.position(0);
			if(buf.remaining() > 1020) {
				length += 1020;
			}
			else {
				length += buf.remaining();
			}
			logger.info("length:" + buf.remaining());
		}
		logger.info("total:" + length);
	}
	private static final Map<Integer, Bit> ttMap = new HashMap<Integer, Bit>();
	{

		// type1 コンスタントに8%くらい減る
/*		ttMap.put(0x8, new Bit2(0x3));
		ttMap.put(0x0, new Bit4(0xB));
		ttMap.put(0x1, new Bit4(0xA));
		ttMap.put(0x2, new Bit4(0x9));
		ttMap.put(0x3, new Bit4(0x8));
		ttMap.put(0x4, new Bit4(0x7));
		ttMap.put(0x5, new Bit4(0x6));
		ttMap.put(0x9, new Bit4(0x5));
		ttMap.put(0xA, new Bit4(0x4));
		ttMap.put(0xB, new Bit4(0x3));
		ttMap.put(0xc, new Bit4(0x2));
		ttMap.put(0xD, new Bit4(0x1));
		ttMap.put(0x6, new Bit6(0x3));
		ttMap.put(0x7, new Bit6(0x2));
		ttMap.put(0xE, new Bit6(0x1));
		ttMap.put(0xF, new Bit6(0x0)); // */
		// type2 効くときは10%くらいいく
		/*		ttMap.put(0x0, new Bit3(0x7));
		ttMap.put(0x1, new Bit3(0x6));
		ttMap.put(0x8, new Bit3(0x5));
		ttMap.put(0x9, new Bit3(0x4));
		ttMap.put(0x2, new Bit4(0x7));
		ttMap.put(0x3, new Bit4(0x6));
		ttMap.put(0x4, new Bit4(0x5));
		ttMap.put(0xA, new Bit4(0x4));
		ttMap.put(0xB, new Bit4(0x3));
		ttMap.put(0xC, new Bit4(0x2));
		ttMap.put(0x5, new Bit5(0x3));
		ttMap.put(0xD, new Bit5(0x2));
		ttMap.put(0x6, new Bit6(0x3));
		ttMap.put(0x7, new Bit6(0x2));
		ttMap.put(0xE, new Bit6(0x1));
		ttMap.put(0xF, new Bit6(0x0)); // */
		// type3(効くときには効くけど、全体としては、非常に圧縮率がわるくなる)
/*		ttMap.put(0x8, new Bit1(0x1));
		ttMap.put(0x0, new Bit4(0x7));
		ttMap.put(0x2, new Bit4(0x6));
		ttMap.put(0xA, new Bit4(0x5));
		ttMap.put(0x1, new Bit5(0x9));
		ttMap.put(0x3, new Bit5(0x8));
		ttMap.put(0x4, new Bit5(0x7));
		ttMap.put(0x5, new Bit5(0x6));
		ttMap.put(0x9, new Bit5(0x5));
		ttMap.put(0xB, new Bit5(0x4));
		ttMap.put(0xC, new Bit5(0x3));
		ttMap.put(0xD, new Bit5(0x2));
		ttMap.put(0x6, new Bit6(0x3));
		ttMap.put(0x7, new Bit6(0x2));
		ttMap.put(0xE, new Bit6(0x1));
		ttMap.put(0xF, new Bit6(0x0)); // */
	}
}
