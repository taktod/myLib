package com.ttProject.transcode.xuggle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackManager;
import com.ttProject.transcode.ITranscodeListener;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.xuggle.encode.AudioEncodeManager;
import com.ttProject.transcode.xuggle.encode.IEncodeManager;
import com.ttProject.transcode.xuggle.encode.VideoEncodeManager;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.ttProject.transcode.xuggle.packet.IPacketizer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

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
	/** 処理thread pool */
	private ExecutorService executor = null;
	/** デコーダー */
	private IStreamCoder decoder = null;
	/** 元オブジェクトpacket化モジュール */
	private IPacketizer packetizer;
	/** 動作パケット(使い回します) */
	private IPacket packet = null;
	/** 変換マネージャー */
	List<IEncodeManager> encodeManagers = new ArrayList<IEncodeManager>();
	/**
	 * executorの設定
	 * @param executor
	 */
	public void setExecutorService(ExecutorService executor) {
		this.executor = executor;
	}
	/**
	 * エンコード用オブジェクトを設置します。
	 * @param encoder
	 * @param depacketizer
	 * @param executor
	 */
	public void addEncodeObject(IStreamCoder encoder, IDepacketizer depacketizer, ITranscodeListener listener, ExecutorService executor) throws Exception {
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
		encodeManager.setTranscodeListener(listener);
		encodeManager.setExceptionListener(getExpListener());
		encodeManager.setExecutorService(executor);
		encodeManagers.add(encodeManager);
	}
	/**
	 * エンコード用オブジェクトをセットアップします
	 * @param encoder
	 * @param packetizer
	 */
	public void addEncodeObject(IStreamCoder encoder, IDepacketizer depacketizer, ITranscodeListener listener) throws Exception {
		addEncodeObject(encoder, depacketizer, listener, null);
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
	public void transcode(final Unit unit) throws Exception {
		// パケットの確認を実行します。
		if(!packetizer.check(unit)) {
			return;
		}
		if(executor != null) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						process(unit);
					}
					catch(Exception e) {
						getExpListener().exceptionCaught(e);
					}
				}
			});
		}
		else {
			try {
				// threadでない場合はそのまま処理する
				process(unit);
			}
			catch(Exception e) {
				getExpListener().exceptionCaught(e);
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
	}
	/**
	 * 処理を実施する。
	 * @param unit
	 * @throws Exception (なにか問題がでたら例外がでます)
	 */
	private void process(Unit unit) throws Exception {
		// packet化する。
		packet = packetizer.getPacket(unit, packet);
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
				// こっちも時間がずれるっぽいので、治してみよう。
				samples.setPts((long)(packet.getPts() / samples.getTimeBase().getDouble() * packet.getTimeBase().getDouble()));
				// エンコーダーとの型があわなかったらリサンプルする必要あり
				for(IEncodeManager encodeManager : encodeManagers) {
					encodeManager.encode(samples.copyReference());
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
	@Override
	protected ITrackManager makeTrackManager(int newId) {
		return null;
	}
}
