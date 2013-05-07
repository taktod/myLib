package com.ttProject.nio.channels;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * http経由でのデータDL用のfileChannel
 * fileのポインター等をうまく管理します。
 * rangeを利用して中途DLする場合にでもきちんとfilePointerを保持できるようにします。
 * @author taktod
 */
public class URLFileReadChannel implements IFileReadChannel {
	private final URL url;
	private HttpURLConnection conn;
	private final int size;
	private int startPos; // 読み込み時の開始位置
	private int readSize; // 読み込み済みサイズ
	private boolean open;
	public URLFileReadChannel(String urlString) throws Exception {
		url = new URL(urlString);
		openConnection(0);
		size = conn.getContentLength();
	}
	private void openConnection(int position) throws Exception {
		// オブジェクトをつくった瞬間にデータを作成します。
		URLConnection urlConn = url.openConnection();
		if(!(urlConn instanceof HttpURLConnection)) {
			throw new Exception("connection is not http");
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
		// サイズだけはとりあえず保持しておく。
		startPos = position; // 初期位置をいれておく。
		readSize = 0; // 読み込み済みデータ量をいれておく。
		open = true; // 開いたことにする。
	}
	private void closeConnection() {
		conn.disconnect();
		open = false;
	}
	public boolean isOpen() {
		return open;
	}
	public int read(ByteBuffer dst) throws IOException {
		ReadableByteChannel channel = Channels.newChannel(conn.getInputStream());
		channel.read(dst);
		int readSize = dst.capacity() - dst.remaining();
		this.readSize += readSize;
		return readSize; // 読み込めた量を応答しておく
	}
	public void close() throws IOException {
		// 処理を閉じます。
		closeConnection();
	}
	// 位置関連
	public int position() throws Exception {
		if(!open) {
			throw new IOException("stream is closed.");
		}
		return startPos + readSize;
	}
	public URLFileReadChannel position(int newPosition) throws Exception {
		if(!open) {
			throw new IOException("stream is closed.");
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
			// 巻き戻す場合は、接続し直す必要がある。(たとえ近くても無理)
			// それ以上離れている場合は、接続し直した方がよさそう。
			closeConnection();
			openConnection(newPosition);
		}
		return this;
	}
	/**
	 * ファイルサイズを応答する
	 * @return
	 */
	public int size() throws Exception {
		return size;
	}
}
