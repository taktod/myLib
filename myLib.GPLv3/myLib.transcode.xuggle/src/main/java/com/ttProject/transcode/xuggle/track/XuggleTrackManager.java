package com.ttProject.transcode.xuggle.track;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackListener;
import com.ttProject.transcode.xuggle.XuggleTranscodeManager;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * xuggleのtrackManagerの動作定義
 * @author taktod
 */
public class XuggleTrackManager implements IXuggleTrackManager {
	/** 動作ID */
	private final int id;
	/** 出来上がったデータを参照するlistener */
	private ITrackListener trackListener = null;
	/** 変換マネージャー */
	private final XuggleTranscodeManager transcodeManager;
	/** エンコーダー */
	private IStreamCoder encoder = null;
	/** 出力packetをunitに変換するプログラム */
	private IDepacketizer depacketizer = null;
	/** マルチthread処理をさせるときのexecutorServer設定 */
	private ExecutorService executorService = null;
	/** media処理の部分を抜き出したモジュール */
	private TrackModule module = null;
	/**
	 * コンストラクタ
	 * @param id
	 */
	public XuggleTrackManager(XuggleTranscodeManager transcodeManager, int id) {
		this.transcodeManager = transcodeManager;
		this.id = id;
	}
	/**
	 * id参照
	 * @return idの値
	 */
	@Override
	public int getId() {
		return id;
	}
	/**
	 * track参照するlistenerの設定
	 */
	@Override
	public void setTrackListener(ITrackListener listener) {
		this.trackListener = listener;
	}
	/**
	 * encoderを設置します。
	 */
	@Override
	public void setEncoder(IStreamCoder encoder) throws Exception {
		if(encoder.getDirection() != Direction.ENCODING) {
			throw new Exception("エンコード用のcoderではありませんでした。");
		}
		// encoderを設置するときに、音声動作か映像動作かわかるので、moduleを選択しておく。
		switch(encoder.getCodecType()) {
		case CODEC_TYPE_AUDIO:
			module = new AudioTrackModule(this);
			break;
		case CODEC_TYPE_VIDEO:
			module = new VideoTrackModule(this);
			break;
/*		case CODEC_TYPE_ATTACHMENT:
		case CODEC_TYPE_DATA:
		case CODEC_TYPE_SUBTITLE:
		case CODEC_TYPE_UNKNOWN:*/
		default:
			throw new Exception("エンコードタイプが不明です。");
		}
		this.encoder = encoder;
	}
	/**
	 * エンコードオブジェクト参照
	 * @return
	 */
	protected IStreamCoder getEncoder() {
		return encoder;
	}
	/**
	 * データを設定する動作
	 * @param packet
	 * @throws Exception
	 */
	protected void applyData(IPacket packet) throws Exception{
		if(packet.isComplete()) {
			if(depacketizer != null) {
				List<Unit> units = depacketizer.getUnits(encoder, packet);
				if(trackListener != null) {
					trackListener.receiveData(units);
				}
			}
		}
	}
	/**
	 * 例外をレポートする。
	 * @param e
	 */
	protected void reportException(Exception e) {
		transcodeManager.reportException(e);
	}
	/**
	 * 非パケット化用プログラム定義
	 */
	@Override
	public void setDepacketizer(IDepacketizer depacketizer) {
		this.depacketizer = depacketizer;
	}
	/**
	 * マルチスレッド動作させる場合のexecutorService定義
	 */
	@Override
	public void setExecutorService(ExecutorService executor) {
		this.executorService = executor;
	}
	/**
	 * 停止処理
	 */
	public void close() {
		if(module != null) {
			module.close();
			module = null;
		}
		if(encoder != null && encoder.isOpen()) {
			encoder.close();
			encoder = null;
		}
	}
	/**
	 * 変換処理のベース
	 * @param xuggleObject
	 */
	public void encode(final Object xuggleObject) {
		// 処理可能か確かめる。
		if(!module.checkObject(xuggleObject)) {
			return;
		}
		if(executorService != null) {
			// マルチスレッドで動作させる。
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					module.process(xuggleObject);
				}
			});
		}
		else {
			// このまま処理させる。
			module.process(xuggleObject);
		}
	}
}
