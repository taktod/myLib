package com.ttProject.xuggle.in.flv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xuggle.xuggler.io.IURLProtocolHandler;
import com.xuggle.xuggler.io.IURLProtocolHandlerFactory;
import com.xuggle.xuggler.io.URLProtocolManager;

/**
 * FLVのストリームデータをffmpegに流し込むための動作
 * xuggleでカスタム入力を利用する場合のメモ
 * 1:IURLProtocolHandlerFactoryを実装したクラスを作ります。
 * 2:実装したクラスをURLProtocolManagerに登録しておきます。
 * 3:指定したURLの形式になっているコンテナが要求された場合、このクラスのgetHandlerによって処理IURLProtocolHandlerが決定されて処理されます。
 * 
 * @author taktod
 */
public class FlvHandlerFactory implements IURLProtocolHandlerFactory{
	/** シングルトンインスタンス */
	private static FlvHandlerFactory instance = new FlvHandlerFactory();
	/** このFactoryが扱うプロトコル名 */
	public static final String DEFAULT_PROTOCOL = "flvStreamInput";
	/** 内部で処理しているFlvHandlerの保持 */
	private final Map<String, FlvHandler> handlers = new ConcurrentHashMap<String, FlvHandler>();
	/**
	 * ffmpegからurlが合致する場合にhandlerを求められます。
	 */
	@Override
	public IURLProtocolHandler getHandler(String protocol, String url, int flags) {
		String streamName = URLProtocolManager.getResourceFromURL(url);
		return handlers.get(streamName);
	}
	/**
	 * コンストラクタ
	 */
	private FlvHandlerFactory() {
		// URLProtocolManagerに登録することで、今後(flvStreamInput:xxxx)のURL向けの処理はこのfactoryが返すHandlerを利用して動作するようになります。
		URLProtocolManager manager = URLProtocolManager.getManager();
		manager.registerFactory(DEFAULT_PROTOCOL, this);
	}
	/**
	 * factoryの取得
	 * @return
	 */
	public static FlvHandlerFactory getFactory() {
		if(instance == null) {
			throw new RuntimeException("no flvStreamInput factory.");
		}
		return instance;
	}
	/**
	 * 処理Handlerを登録する。
	 * @param manager
	 */
	public void registerHandler(String name, FlvHandler handler) {
		handlers.put(name, handler);
	}
}
