package com.ttProject.nio.channels;

/**
 * ファイル読み込み
 * IFileReadChannelは、IReadChannelとほぼ同じだけど、positionで巻き戻ることが可能になっているものとします。
 * @author taktod
 */
public interface IFileReadChannel extends IReadChannel {
	/**
	 * アクセスパスの応答
	 * @return
	 */
	public String getUri();
}
