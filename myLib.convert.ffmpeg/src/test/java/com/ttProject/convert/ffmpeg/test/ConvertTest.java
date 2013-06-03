package com.ttProject.convert.ffmpeg.test;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

import com.ttProject.convert.IConvertListener;
import com.ttProject.convert.ffmpeg.FfmpegConvertManager;
import com.ttProject.convert.ffmpeg.ProcessHandler;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 動作テスト用
 * @author taktod
 */
public class ConvertTest {
//	@Test
	public void test() throws Exception {
		// データ元
		IFileReadChannel fc1 = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.mp4");
		// 保存先
		final FileChannel fc2 = new FileOutputStream("output.flv").getChannel();
		// 変換マネージャー
		FfmpegConvertManager manager = new FfmpegConvertManager();
		// 変換動作プロセス取得
		ProcessHandler handler = manager.getProcessHandler("test");
		// 変換出力データの処理
		handler.addListener(new IConvertListener() {
			@Override
			public void receiveData(ByteBuffer buffer) {
				try {
					// 応答をうけとったらファイルに書き出す。
					System.out.println("応答をうけとりました。");
					fc2.write(buffer);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// 処理させる動作コマンド
		handler.setCommand("~/bin/bin/avconv -i - -acodec copy -vcodec copy -f flv -");
		// 処理開始
		manager.start();
		ByteBuffer buf;
		while(true) {
			// ソースファイルからデータ読み込み
			buf = ByteBuffer.allocate(65536);
			fc1.read(buf);
			buf.flip();
			if(buf.remaining() != 0) {
				// データがある場合は変換プログラムにデータを送る
				manager.applyData(buf);
			}
			// 読み込み元ファイルを読み切ったらおわり。
			if(fc1.position() == fc1.size()) {
				break;
			}
		}
		// ちょっと待っとく。
		Thread.sleep(2000);
		// 後処理
		fc1.close();
		fc2.close();
	}
}
