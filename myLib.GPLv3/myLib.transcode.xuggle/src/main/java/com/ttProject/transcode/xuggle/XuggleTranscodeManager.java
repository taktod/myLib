package com.ttProject.transcode.xuggle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.ttProject.media.Unit;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.xuggle.encode.AudioEncodeManager;
import com.ttProject.transcode.xuggle.encode.IEncodeManager;
import com.ttProject.transcode.xuggle.encode.VideoEncodeManager;
import com.ttProject.transcode.xuggle.exception.FormatChangeException;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.ttProject.transcode.xuggle.packet.IPacketizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

/**
 * 変換動作の中心マネージャー
 * 1つのマネージャーでは、１つのコンバートだけ実行します。
 * 音声と映像の両方をコンバートしたければ２つ変換マネージャーが必要となるとします。
 * 
 * TODO xuggleの変換モジュールで、VideoPictureやAudioSampleに変換してから、複数の出力にわける動作をいれると、１つのデータから複数出力できていい感じになると思われます。
 * どうするかな・・・
 * 
 * @author taktod
 */
public class XuggleTranscodeManager extends TranscodeManager {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(XuggleTranscodeManager.class);
	/** thread動作フラグ */
	private boolean threadFlg = false;
	/** 動作用Thread */
	private Thread workerThread = null;
	/** Thread動作実体 */
	private ConvertWorker worker = null;
	/** デコーダー */
	private IStreamCoder decoder = null;
	/** エンコーダー */
//	private IStreamCoder encoder = null;
	/** 元オブジェクトpacket化モジュール */
	private IPacketizer packetizer;
	/** 変換後オブジェクト unit化モジュール */
//	private IDepacketizer depacketizer;
	/** 動作パケット(使い回します) */
	private IPacket packet = null;
	/** エンコード用のパケット(使い回したい)(エンコードとデコードのpacketを同じにしたらchannel element 0.0 is not allocatedとかいうエラーがでた。) */
//	private IPacket encodePacket = null;
	/** 音声リサンプラー */
//	private IAudioResampler audioResampler = null;
	/** 映像リサンプラー */
//	private IVideoResampler videoResampler = null;
	/** 変換マネージャー */
	List<IEncodeManager> encodeManagers = new ArrayList<IEncodeManager>();
	/**
	 * エンコード用オブジェクトをセットアップします
	 * @param encoder
	 * @param packetizer
	 */
	public void addEncodeObject(IStreamCoder encoder, IDepacketizer depacketizer) throws Exception {
		IEncodeManager encodeManager = null;
		if(encoder.getCodecType() == Type.CODEC_TYPE_AUDIO) {
			// 音声の場合
			encodeManager = new AudioEncodeManager();
		}
		else if(encoder.getCodecType() == Type.CODEC_TYPE_VIDEO) {
			// 映像の場合
			encodeManager = new VideoEncodeManager();
		}
		encodeManager.setDepacketizer(depacketizer);
		encodeManager.setEncoder(encoder);
		encodeManager.setTranscodeListener(getTranscodeListener());
		encodeManagers.add(encodeManager);
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
		// パケットの確認を実行します。
		if(!packetizer.check(unit)) {
			throw new FormatChangeException("コーデックが合いませんでした。");
		}
		if(threadFlg) {
			if(worker == null) {
				worker = new ConvertWorker();
			}
			if(workerThread == null) {
				workerThread = new Thread(worker);
				workerThread.setName("XuggleTranscodeThread:" + hashCode());
				workerThread.start();
			}
			worker.addQueue(unit);
		}
		else {
			try {
				// threadでない場合はそのまま処理する
				process(unit);
			}
			catch(Exception e) {
				getTranscodeListener().exceptionCaught(e);
			}
		}
	}
	/**
	 * 停止処理
	 */
	@Override
	public void close() {
		// エンコードマネージャーを閉じておきます。
		for(IEncodeManager encodeManager : encodeManagers) {
			encodeManager.close();
		}
		if(decoder != null) {
			decoder.close();
			decoder = null;
		}
		if(threadFlg) {
			// threadを停止しないとだめ
			if(worker != null) {
				worker.stop();
			}
			if(workerThread != null) {
				workerThread.interrupt();
			}
			workerThread = null;
			worker = null;
		}
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
				throw new Exception("decoderが取得できませんでした");
			}
			if(!decoder.isOpen()) {
				if(decoder.open(null, null) < 0) {
					throw new Exception("decoderが開けませんでした");
				}
			}
		}
		if(decoder.getCodecType() == Type.CODEC_TYPE_AUDIO) {
			// audioの場合
			processAudio(packet);
		}
		else if(decoder.getCodecType() == Type.CODEC_TYPE_VIDEO) {
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
		IAudioSamples samples = IAudioSamples.make(1024, decoder.getChannels());
		int offset = 0;
		while(offset < packet.getSize()) {
			int bytesDecoded = decoder.decodeAudio(samples, packet, offset);
			if(bytesDecoded < 0) {
				throw new Exception("デコード中にエラーが発生");
			}
			offset += bytesDecoded;
			if(samples.isComplete()) {
				// データができあがった
				// エンコーダーとの型があわなかったらリサンプルする必要あり
				for(IEncodeManager encodeManager : encodeManagers) {
					encodeManager.encode(samples);
				}
			}
		}
	}
	/**
	 * 映像処理をすすめる
	 * @param packet
	 * @throws Exception
	 */
	private void processVideo(IPacket packet) throws Exception {
		IVideoPicture picture = IVideoPicture.make(decoder.getPixelType(), decoder.getWidth(), decoder.getHeight());
		int offset = 0;
		while(offset < packet.getSize()) {
			int bytesDecoded = decoder.decodeVideo(picture, packet, offset);
			if(bytesDecoded <= 0) {
				throw new Exception("デコード中にエラーが発生しました。");
			}
			offset += bytesDecoded;
			if(picture.isComplete()) {
				// なんか時間が狂うので、時間の設定し直しを実行しておいた。
				picture.setPts(packet.getPts() * 1000L);
				for(IEncodeManager encodeManager : encodeManagers) {
					encodeManager.encode(picture);
				}
			}
		}
	}
	/**
	 * 処理待ちデータが残っているか確認
	 * @return
	 */
	public boolean isRemaining() {
		if(threadFlg && worker != null) {
			return worker.getRemainUnitCount() != 0;
		}
		return false;
	}
	/**
	 * マルチthread動作用変換worker
	 * @author taktod
	 *
	 */
	private class ConvertWorker implements Runnable {
		/** 変換候補データを保持するqueue */
		private LinkedBlockingQueue<Unit> unitQueue = new LinkedBlockingQueue<Unit>();
		/** 処理中フラグ */
		private boolean workFlg = true;
		/**
		 * 強制停止
		 */
		public void stop() {
			workFlg = false;
		}
		/**
		 * 残っているデータを参照する
		 * @return
		 */
		public int getRemainUnitCount() {
			return unitQueue.size();
		}
		/**
		 * queueを登録します
		 * @param unit
		 */
		public void addQueue(Unit unit) {
			if(!workFlg) {
				return;
			}
			unitQueue.add(unit);
		}
		/**
		 * 変換を実行する
		 */
		@Override
		public void run() {
			try {
				while(workFlg) {
					Unit unit = unitQueue.take();
					process(unit);
				}
			}
			catch(Exception e) {
				// 例外は通知して処理終わりにする。
				getTranscodeListener().exceptionCaught(e);
			}
			workFlg = false;
			// queueの中身を解放しておく
			unitQueue.clear();
		}
	}
}
