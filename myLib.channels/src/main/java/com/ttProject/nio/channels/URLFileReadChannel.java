package com.ttProject.nio.channels;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.Logger;

/**
 * http経由でのデータDL用のfileChannel
 * @author taktod
 */
public class URLFileReadChannel implements IFileReadChannel {
	/** ロガー */
	private static final Logger logger = Logger.getLogger(URLFileReadChannel.class);
	/** ターゲットURI */
	private final URL url;
	/** 接続保持 */
	private HttpURLConnection conn;
	/** サイズ取得 */
	private final int size;
	/** rangeアクセスの開始位置保持 */
	private int startPos;
	/** 読み込み済みデータ量 */
	private int readSize;
	/** アクセス中かどうか */
	private boolean open;
	/** 読み込みチャンネル */
	private ReadableByteChannel channel;
	/**
	 * コンストラクタ
	 * @param urlString
	 * @throws IOException
	 */
	public URLFileReadChannel(String urlString) throws IOException {
		this(urlString, 0);
	}
	/**
	 * コンストラクタ with 位置
	 * アクセス位置がrange範囲外の場合は例外になります
	 * @param urlString
	 * @param position
	 * @throws IOException
	 */
	public URLFileReadChannel(String urlString, int position) throws IOException {
		url = new URL(urlString);
		openConnection(position);
		size = conn.getContentLength();
	}
	/**
	 * コネクションを開く処理
	 * @param position
	 * @throws IOException
	 */
	private void openConnection(int position) throws IOException {
		if(size == 0 || position < size) {
			// オブジェクトをつくった瞬間にデータを作成します。
			URLConnection urlConn = url.openConnection();
			if(!(urlConn instanceof HttpURLConnection)) {
				logger.error("コネクションを開いたところhttpではありませんでした。");
				throw new IOException("connection is not http");
			}
			conn = (HttpURLConnection)urlConn;
			conn.setRequestMethod("GET");
			conn.setAllowUserInteraction(false); // ユーザーによるパスワードの入力とかを許可するかどうか?
			conn.setInstanceFollowRedirects(true); // サーバーのリダイレクト指示に従うか(従う)
			// iPhone4SのUser-Agentを名乗っておく。
			if(position != 0) {
				conn.addRequestProperty("Range", "bytes=" + position + "-");
			}
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 6_1 like Mac OS X; ja-jp) AppleWebKit/536.26 (KHTML, like Gecko) CriOS/23.0.1271.100 Mobile/10B142 Safari/8536.25");
			// proxyアクセスであることを名乗っておく。
			conn.setRequestProperty("Via", "1.1(jetty)");
			conn.connect(); // つなぐ
			open = true; // 開いたことにする。
			channel = Channels.newChannel(conn.getInputStream());
		}
		else {
			open = false;
			if(position > size) {
				logger.error("httpのファイルのサイズを超過した部分の位置から開こうとしました。");
				throw new IOException("out of range for http");
			}
		}
		// サイズだけはとりあえず保持しておく。
		startPos = position; // 初期位置をいれておく。
		readSize = 0; // 読み込み済みデータ量をいれておく。
	}
	/**
	 * 接続を閉じます
	 */
	private void closeConnection() {
		conn.disconnect();
		open = false;
	}
	/**
	 * {@inheritDoc}
	 */
	public boolean isOpen() {
		return open;
	}
	/**
	 * {@inheritDoc}
	 */
	public int read(ByteBuffer dst) throws IOException {
		int startPos = dst.position();
		channel.read(dst);
		int readSize = dst.position() - startPos;
		this.readSize += readSize;
		return readSize; // 読み込めた量を応答しておく
	}
	/**
	 * {@inheritDoc}
	 */
	public void close() throws IOException {
		closeConnection();
	}
	/**
	 * {@inheritDoc}
	 */
	public int position() {
		return startPos + readSize;
	}
	/**
	 * {@inheritDoc}
	 */
	public URLFileReadChannel position(int newPosition) throws IOException {
		if(newPosition == size) {
			readSize = newPosition - startPos;
		}
		if(!open) {
			return this;
		}
		// 現在のinputStreamからの読み込み位置をしっておく必要あり。
		long skipSize = newPosition - startPos - readSize;
		// 先に進む場合は、10k以内ならそのまままった方がよさそう。(dl終了遅延より・・・という意味)
		if(skipSize == 0) {
			return this;
		}
		if(skipSize > 0 && conn.getInputStream().available() > skipSize) {
			readSize += skipSize;
			conn.getInputStream().skip(skipSize);
		}
		else {
			logger.info("positionの変更を実行するために、コネクションをつなぎ直す必要がでました。");
			// 巻き戻す場合は、接続し直す必要がある。(たとえ近くても無理)
			// それ以上離れている場合は、接続し直した方がよさそう。
			closeConnection();
			openConnection(newPosition);
		}
		return this;
	}
	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return size;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUri() {
		return url.toString();
	}
}
