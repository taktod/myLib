package com.ttProject.xuggle;

import com.ttProject.media.Unit;
import com.ttProject.transcode.TranscodeManager;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;

/**
 * 変換動作の中心マネージャー
 * 1つのマネージャーでは、１つのコンバートだけ実行します。
 * 音声と映像の両方をコンバートしたければ２つ変換マネージャーが必要となるとします。
 * @author taktod
 */
public class XuggleTranscodeManager extends TranscodeManager {
	/** thread動作フラグ */
	private boolean threadFlg = false;
	/** デコーダー */
	private IStreamCoder decoder = null;
	/** エンコーダー */
	private IStreamCoder encoder = null;
	/** 元オブジェクトpacket化モジュール */
	private IPacketizer packetizer;
	/** 変換後オブジェクト unit化モジュール */
	private IDepacketizer depacketizer;
	/** 動作パケット(使い回します) */
	private IPacket packet = null;
	/**
	 * エンコーダーを設定する
	 */
	public void setEncoder(IStreamCoder encoder) {
		this.encoder = encoder;
	}
	/**
	 * thread動作に変更する
	 */
	public void setThreadFlg(boolean threadFlg) {
		this.threadFlg = threadFlg;
	}
	/**
	 * パケット化モジュールを設定
	 * @param packetizer
	 */
	public void setPacketizer(IPacketizer packetizer) {
		this.packetizer = packetizer;
	}
	/**
	 * Unit化モジュールを設定
	 * @param depacketizer
	 */
	public void setDepacketizer(IDepacketizer depacketizer) {
		this.depacketizer = depacketizer;
	}
	/*
	 * このマネージャーでやることは
	 * Unitを入力として受け取る
	 * IPacketizerでpacket化する
	 * -----
	 * IPacketizerからdecoderを取得
	 * デコードしてIAudioSamplesかIVideoPictureに分解する。
	 * エンコーダーで指定のデータにエンコードする。
	 * IDepacketizerで目的のUnitに変換する
	 * Listenerに応答を渡す
	 * 
	 * thread化する場合は----以降の処理がThread上の処理になります。
	 */
	/**
	 * 変換実行
	 * @param unit 変換対象データ
	 */
	@Override
	public void transcode(Unit unit) throws Exception {
		if(threadFlg) {
			
		}
		else {
			// threadでない場合はそのまま処理する
			process(unit);
		}
		// packetizerでpacket化する。
		// そのまま処理するなら [デコード→エンコード→depacketizerでunitに戻して応答]
		// threadで処理するなら、一旦queueに登録
		// 無限ループthreadで処理
	}
	/**
	 * 処理を実施する。
	 * @param unit
	 * @throws Exception (なにか問題がでたら例外がでます)
	 */
	private void process(Unit unit) throws Exception {
		// packet化する。
		IPacket packet = packetizer.getPacket(unit, this.packet);
		if(packet == null) {
			// packet化できない場合は処理しない。
			return;
		}
		// デコードを処理する。
		if(decoder == null) {
			decoder = packetizer.createDecoder();
			if(decoder == null) {
				throw new Exception("decoderが作成できませんでした。");
			}
		}
		if(encoder.getCodecType() == Type.CODEC_TYPE_AUDIO) {
			// audioの場合
			processAudio(packet);
		}
		else if(encoder.getCodecType() == Type.CODEC_TYPE_VIDEO) {
			// videoの場合
			processVideo(packet);
		}
		else {
			throw new Exception("変換タイプが不明です");
		}
	}
	/**
	 * 音声処理をすすめる
	 * @param packet
	 * @throws Exception
	 */
	private void processAudio(IPacket packet) throws Exception {
	}
	/**
	 * 映像処理をすすめる
	 * @param packet
	 * @throws Exception
	 */
	private void processVideo(IPacket packet) throws Exception {
		
	}
}
