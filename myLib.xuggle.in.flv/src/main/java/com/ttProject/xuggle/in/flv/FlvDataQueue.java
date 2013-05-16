package com.ttProject.xuggle.in.flv;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * xuggleのIURLProtocolHandlerはffmpegが処理のトリガーになってしまっているので、次のような動作にする。
 * １：データをqueueにためておく。
 * ２：ffmpegから要求があれば、queueから必要数のデータを取り出して応答する。
 * なお、ffmpegからの要求の応答では、データサイズ0を返してしまうと動作がとまってしまうことがあるので
 * threadをblockしないpollではなく、blockするtakeを利用しています。
 * この部分もうちょっとなんとかしないといけないかも。
 * 
 * @author toktod
 */
public class FlvDataQueue {
	/** ロガー */
	private final Logger logger = LoggerFactory.getLogger(FlvDataQueue.class);
	/** データを保持しておく。queue */
	LinkedBlockingQueue<ByteBuffer> dataQueue = new LinkedBlockingQueue<ByteBuffer>();
	/**
	 * headerデータを設定します。
	 * @param header
	 */
	public void putHeaderData(ByteBuffer header) {
		dataQueue.add(header.duplicate());
	}
	/**
	 * tagデータを更新します。
	 * @param tag
	 */
	public void putTagData(ByteBuffer tag) {
		dataQueue.add(tag.duplicate());
	}
	/**
	 * 動作が停止するときの動作
	 */
	public void close() {
		if(dataQueue != null) {
			dataQueue.clear(); // dataQueueの待ちがある場合にこまる。
			dataQueue = null;
		}
	}
	/**
	 * queueから要素を読み込んで処理を実行する。
	 * @return
	 * @throws InterruptedException
	 */
	public ByteBuffer read() {
		ByteBuffer result = null;
		try {
			// takeを利用して、内容がない場合は、データが届くまで待つようにします。
			result = dataQueue.take();
			return result;
		}
		catch (InterruptedException e) {
			// threadの動作が阻害されただけなら特になにもせずぬける。(動作がとまっただけなので・・・)
			return null;
		}
		catch (Exception e) {
			logger.error("dataQueueの取得で例外が発生しました。", e);
			// 例外がでた場合は、nullを応答しておく。
			return null;
		}
	}
}
