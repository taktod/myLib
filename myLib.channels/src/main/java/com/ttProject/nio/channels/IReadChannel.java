package com.ttProject.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * ファイル読み込み
 * TODO こっち側にRead系のchannelをすべてマージしておきたいね。
 * @author taktod
 */
public interface IReadChannel extends ReadableByteChannel {
	/**
	 * 開いているか確認
	 * @return true:アクセス可能 false:アクセス不能
	 */
	public boolean isOpen();
	/**
	 * ファイルサイズ取得
	 * @return ファイルサイズ
	 * @throws IOException
	 */
	public int size() throws IOException;
	/**
	 * 現在位置取得
	 * @return 現在位置
	 * @throws IOException
	 */
	public int position() throws IOException;
	/**
	 * 位置変更
	 * @param newPosition 移動位置
	 * @return 動作オブジェクト
	 * @throws IOException
	 */
	public IReadChannel position(int newPosition) throws IOException;
	/**
	 * 読み込み(確実に指定読み込み量が読めるわけではないです)
	 * @param dst 読み込みバッファ
	 * @return 読み込めたサイズ
	 * @throws IOException
	 */
	public int read(ByteBuffer dst) throws IOException;
	/**
	 * 閉じる処理
	 * @throws IOException
	 */
	public void close() throws IOException;
	/**
	 * アクセスパスの応答
	 * @return
	 */
	public String getUri();
}
