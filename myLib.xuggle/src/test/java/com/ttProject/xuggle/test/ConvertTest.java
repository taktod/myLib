package com.ttProject.xuggle.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.util.DateUtil;
import com.ttProject.util.HexUtil;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IStreamCoder.Direction;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

/**
 * データのコンバート動作のテスト
 * @author taktod
 */
public class ConvertTest {
	/**
	 * h263のコンバート動作テスト とりあえず画像→h263をつくりたい。
	 * @throws Exception
	 */
//	@Test
	public void flv1() throws Exception {
		IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
		IRational frameRate = IRational.make(15, 1); // 15fps
		coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
		
		coder.setBitRate(250000); // 250kbps
		coder.setBitRateTolerance(9000);
		coder.setPixelType(IPixelFormat.Type.YUV420P);
		coder.setWidth(320);
		coder.setHeight(240);
		coder.setGlobalQuality(10);
		coder.setFrameRate(frameRate);
		coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
		
		int retVal = coder.open(null, null);
		if(retVal < 0) {
			throw new RuntimeException("変換コーダーが開けませんでした。");
		}
		// 映像データを適当にコンバートするのに必要な処置
		IConverter converter = null;
		converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);
		int index = 0;
		long firstTimestamp = -1;
		IPacket packet = IPacket.make();
		while(index < 100) {
			index ++;
			long now = System.currentTimeMillis();
			if(firstTimestamp == -1) {
				firstTimestamp = now;
			}
			BufferedImage image = image();
			IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
			retVal = coder.encodeVideo(packet, picture, 0);
			if(retVal < 0) {
				throw new Exception("変換失敗");
			}
			if(packet.isComplete()) {
				System.out.println(packet);
				System.out.println(HexUtil.toHex(packet.getByteBuffer(), true)); // flvに入るべきh263のデータ部分のみ入っています。
			}
			Thread.sleep((long)(1000 / frameRate.getDouble()));
		}
	}
	/**
	 * せっかくなので、flvとして書き込みしてみた。
	 */
	@Test
	public void flv1MakeTest() {
		// flv1をつくりたいところだが・・・
		FileChannel output = null;
		try {
			// 書き込み対象ファイル作成
			output = new FileOutputStream("output.flv").getChannel();
			// headerの部分だけ書き込む必要あり。
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.setVideoFlg(true);
			flvHeader.setAudioFlg(false);
			output.write(flvHeader.getBuffer());
			// xuggleの変換の準備エンコード処理
			IStreamCoder coder = IStreamCoder.make(Direction.ENCODING, ICodec.ID.CODEC_ID_FLV1);
			IRational frameRate = IRational.make(15, 1); // 15fps
			coder.setNumPicturesInGroupOfPictures(30); // gopを30にしておく。keyframeが30枚ごとになる。
			
			coder.setBitRate(250000); // 250kbps
			coder.setBitRateTolerance(9000);
			coder.setPixelType(IPixelFormat.Type.YUV420P);
			coder.setWidth(320);
			coder.setHeight(240);
			coder.setGlobalQuality(10);
			coder.setFrameRate(frameRate);
			coder.setTimeBase(IRational.make(1, 1000)); // 1/1000設定(flvはこうなるべき)
			
			int retVal = coder.open(null, null);
			if(retVal < 0) {
				throw new RuntimeException("変換コーダーが開けませんでした。");
			}
			// 画像データを変換可能なpixelデータに変換する。
			IConverter converter = null;
			converter = ConverterFactory.createConverter(new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR), IPixelFormat.Type.YUV420P);

			int index = 0; // 処理ステップ数
			long firstTimestamp = -1; // 時間のコントロール
			IPacket packet = IPacket.make(); // 処理パケット
			while(index < 100) {
				index ++;
				long now = System.currentTimeMillis();
				if(firstTimestamp == -1) {
					firstTimestamp = now;
				}
				// 表示させる画像を作成
				BufferedImage image = image();
				// BufferedImage→picture
				IVideoPicture picture = converter.toPicture(image, 1000 * (now - firstTimestamp));
				// picture→videoデータ(エンコード)
				retVal = coder.encodeVideo(packet, picture, 0);
				if(retVal < 0) {
					throw new Exception("変換失敗");
				}
				// パケットが完成した場合
				if(packet.isComplete()) {
					// flvデータをつくってみる。
					VideoTag videoTag = new VideoTag();
					videoTag.setCodec(CodecType.H263);
					// キーフレーム判定
					videoTag.setFrameType(packet.isKey());
					// 実データ取得
					ByteBuffer buffer = packet.getByteBuffer();
					videoTag.setSize(12 + 4 + buffer.remaining());
					videoTag.setTimestamp((int)(now - firstTimestamp));
					videoTag.setRawData(buffer);
					output.write(videoTag.getBuffer());
				}
				// 次のフレームの処理する前にしばらく時間をあける。
				Thread.sleep((long)(1000 / frameRate.getDouble()));
			}
		}
		catch (Exception e) {
		}
		finally {
			if(output != null) {
				try {
					output.close();
				}
				catch (Exception e) {
				}
			}
		}
	}
	/**
	 * 表示画像をつくってみる。
	 * @return
	 */
	private BufferedImage image() {
		BufferedImage base = new BufferedImage(320, 240, BufferedImage.TYPE_3BYTE_BGR);
		String message = DateUtil.makeDateTime();
		Graphics g = base.getGraphics();
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
		g.drawString(message, 100, 100);
		g.dispose();
		return base;
	}
}
