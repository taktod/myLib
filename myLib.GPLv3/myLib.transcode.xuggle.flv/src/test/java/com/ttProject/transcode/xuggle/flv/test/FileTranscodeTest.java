package com.ttProject.transcode.xuggle.flv.test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.media.Unit;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.transcode.ITranscodeListener;
import com.ttProject.transcode.ITranscodeManager;
import com.ttProject.transcode.xuggle.Preset;
import com.ttProject.transcode.xuggle.XuggleTranscodeManager;
import com.ttProject.transcode.xuggle.packet.FlvAudioPacketizer;
import com.ttProject.transcode.xuggle.packet.FlvDepacketizer;
import com.ttProject.transcode.xuggle.packet.FlvVideoPacketizer;

/**
 * ファイルをxuggleで変換する動作テスト
 * @author taktod
 */
public class FileTranscodeTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FileTranscodeTest.class);
//	@Test
	public void checkCpu() {
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
	/**
	 * 動作テスト
	 */
	@Test
	public void test() {
		IFileReadChannel source = null;
		ITranscodeManager audioTranscodeManager = null;
		ITranscodeManager videoTranscodeManager = null;
		try {
			// mario.flvをダウンロードしつつコンバートさせる
			source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
			FlvHeader flvHeader = new FlvHeader();
			flvHeader.analyze(source);
			ITagAnalyzer analyzer = new TagAnalyzer();
			Tag tag = null;
			
			// xuggleに変換させる。
			audioTranscodeManager = new XuggleTranscodeManager();
			videoTranscodeManager = new XuggleTranscodeManager();
			ITranscodeListener listener = new ITranscodeListener() {
				@Override
				public void receiveData(List<Unit> unit) {
					// 変換後データをうけとります。
					logger.info(unit);
				}
				@Override
				public void exceptionCaught(Exception e) {
					logger.warn("変換中に例外が発生しました。", e);
				}
			};
			audioTranscodeManager.addTranscodeListener(listener);
			videoTranscodeManager.addTranscodeListener(listener);
			ExecutorService executor1 = Executors.newSingleThreadExecutor();
//			ExecutorService executor2 = Executors.newSingleThreadExecutor();
//			ExecutorService executor3 = Executors.newSingleThreadExecutor();
			ExecutorService executor4 = Executors.newSingleThreadExecutor();
			ExecutorService executor5 = Executors.newSingleThreadExecutor();
//			ExecutorService executor6 = Executors.newSingleThreadExecutor();
			// 音声用
			// flvで出力させるので、flvTagにするためのdepacketizerとencoder(mp3)を設定
			((XuggleTranscodeManager) audioTranscodeManager).addEncodeObject(Preset.mp3(), new FlvDepacketizer(), executor1);
			((XuggleTranscodeManager) audioTranscodeManager).addEncodeObject(Preset.aac(), new FlvDepacketizer(), executor1);
			// flvを入力するので、flvTagからPacketをつくるPacketizerを登録とりあえず音声を扱う
			((XuggleTranscodeManager) audioTranscodeManager).setPacketizer(new FlvAudioPacketizer());
	//		((XuggleTranscodeManager) audioTranscodeManager).setExecutorService(executor3);

			// 映像用
			// flvで出力させるので、flvTagにするためのdepacketizerとencoder(h264)を設定
			((XuggleTranscodeManager) videoTranscodeManager).addEncodeObject(Preset.h264(), new FlvDepacketizer(), executor4);
			((XuggleTranscodeManager) videoTranscodeManager).addEncodeObject(Preset.flv1(), new FlvDepacketizer(), executor5);
			// flvを入力するので、flvTagからPacketをつくるPacketizerを登録とりあえず音声を扱う
			((XuggleTranscodeManager) videoTranscodeManager).setPacketizer(new FlvVideoPacketizer());
	//		((XuggleTranscodeManager) videoTranscodeManager).setExecutorService(executor6);
			while((tag = analyzer.analyze(source)) != null) {
				// 変換させます
				// 時間はずらしてもずれた分だけ勝手にデータが挿入されるとかなさそう。
//				tag.setTimestamp(tag.getTimestamp()); // 時間をねつ造するとうまく動作しないらしい。
				// ということは・・・時間軸がずれる場合はtimestampをきちんと張り替えた方がいいみたい。
				audioTranscodeManager.transcode(tag);
				videoTranscodeManager.transcode(tag);
			}
			// 処理がおわっているか判断して、終わってなかったら１秒待つ
//			while(((XuggleTranscodeManager)videoTranscodeManager).isRemaining()
//				|| ((XuggleTranscodeManager)audioTranscodeManager).isRemaining()) {
//					Thread.sleep(1000);
//			}

//			executor3.shutdown();
//			executor5.shutdown();
//			executor3.awaitTermination(1000, TimeUnit.SECONDS);
//			executor5.awaitTermination(1000, TimeUnit.SECONDS);
			
			executor1.shutdown();
//			executor2.shutdown();
			executor4.shutdown();
			executor5.shutdown();
			executor1.awaitTermination(1000, TimeUnit.SECONDS);
//			executor2.awaitTermination(1000, TimeUnit.SECONDS);
			executor4.awaitTermination(1000, TimeUnit.SECONDS);
			executor5.awaitTermination(1000, TimeUnit.SECONDS);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(audioTranscodeManager != null) {
				audioTranscodeManager.close();
				audioTranscodeManager = null;
			}
			if(videoTranscodeManager != null) {
				videoTranscodeManager.close();
				videoTranscodeManager = null;
			}
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
