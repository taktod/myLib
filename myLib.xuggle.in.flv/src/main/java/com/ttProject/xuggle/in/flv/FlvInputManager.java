package com.ttProject.xuggle.in.flv;

import java.util.Map;

import com.ttProject.xuggle.IMediaManager;
import com.xuggle.xuggler.ISimpleMediaFile;
import com.xuggle.xuggler.IStreamCoder.Flags;

/**
 * flvDataInputを管理するマネージャー
 * このクラスはbeanやpropertiesでの定義情報を保持するためのものです。
 * が、入力フォーマットは定義するものではなく、ffmpegが勝手に解釈するので、することは特にありません。
 * @author taktod
 */
public class FlvInputManager implements IMediaManager {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProtocol() {
		return FlvHandlerFactory.DEFAULT_PROTOCOL;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormat() {
		return "flv";
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISimpleMediaFile getStreamInfo() {
		throw new RuntimeException("入力用のマネージャーなので必要のない処理です。");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getVideoProperty() {
		throw new RuntimeException("入力用のマネージャーなので必要のない処理です。");
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Flags, Boolean> getVideoFlags() {
		throw new RuntimeException("入力用のマネージャーなので必要のない処理です。");
	}
}
