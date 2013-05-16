package com.ttProject.xuggle.out.mpegts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ttProject.xuggle.Transcoder;
import com.xuggle.xuggler.io.IURLProtocolHandler;

/**
 * mpegtsのデータの出力を扱うクラス
 * Xuggleでカスタム出力を利用する場合のメモ
 * 0:IURLProtocolHandlerFactoryから必要に応じて呼び出されるようにしておく。
 * 1:それぞれのoverrideメソッドがffmpegから呼び出されるのでそれに応じた動作をすればOK
 * このクラスの場合はopen close writeあたりが利用されます。
 * 出力されるデータはmpegtsのフォーマットにきちんと変換されたものになります。
 * 
 * @author taktod
 */
public class MpegtsHandler implements IURLProtocolHandler {
	/** ロガー */
	private final Logger logger = LoggerFactory.getLogger(MpegtsHandler.class);
	/** tsファイルのセグメントを作成オブジェクト */
//	private TsSegmentCreator creator;
	/** transcoderオブジェクト */
	private Transcoder transcoder;
	/**
	 * コンストラクタ
	 * @param creator
	 */
//	public MpegtsHandler(TsSegmentCreator creator, Transcoder transcoder) {
	public MpegtsHandler(Transcoder transcoder) {
//		this.creator = creator;
		this.transcoder = transcoder;
	}
	/**
	 * 閉じる要求がきたときの処理
	 * @return -1エラー 0以上それ以外
	 */
	@Override
	public int close() {
		return 0;
	}
	/**
	 * xuggler固有の処理、ストリームサポートしているか？ストリームを名乗った場合はseekイベントがこなくなる。
	 * @param url 入力urlデータ
	 * @param flags 入力フラグ
	 * @return trueストリームである、falseストリームでない
	 */
	@Override
	public boolean isStreamed(String url, int flags) {
		return true;
	}
	/**
	 * ファイルオープン処理(ffmpegから呼び出されます。)
	 * @param url 入力urlデータ(たぶんredfile:xxxx)
	 * @param flags 入力フラグ
	 * @return 0以上で成功 -1エラー
	 */
	@Override
	public int open(String url, int flags) {
		return 0;
	}
	/**
	 * 読み込み(ffmpegから読み込みする場合に呼び出されます。)
	 * この処理では、半端なデータをなげて問題ありません。
	 * @param buf 読み込み実体バッファです。ここに書き込めば、ffmpegに渡されます。参照わたしみたいなものです。
	 * @param size 最大で書き込み可能なサイズです、このサイズをこえてはいけません。
	 * @return 0ファイル終了 数値読み込めたバイト数 -1エラー
	 */
	@Override
	public int read(byte[] buf, int size) {
		logger.error("出力のみ考慮されたクラスなので、読み込みデータが要求されないでほしいです。");
		return -1;
	}
	/**
	 * ffmpegからシークの要求があった場合の処理
	 * @param offset データのオフセット
	 * @param whence 元の位置
	 * @return -1:サポートしない。 それ以外の数値はwhenceからの相対位置
	 */
	@Override
	public long seek(long offset, int whence) {
		return -1;
	}
	/**
	 * 書き込み(ffmpegから出力される場合に呼び出されます。)
	 * この処理では、半端なデータが届く可能性があります。
	 * @param buf 書き込み実体バッファ
	 * @param size 書き込みサイズ
	 * @return 0ファイル終了 数値書き込めたバッファ量 -1エラー
	 */
	@Override
	public int write(byte[] buf, int size) {
/*		if(creator != null) {
			creator.writeSegment(buf, size, transcoder.getTimestamp(), transcoder.isKey());
		}*/
		return size;
	}
}
