package com.ttProject.transcode.xuggle;

import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackManager;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.xuggle.packet.IPacketizer;
import com.ttProject.transcode.xuggle.track.XuggleTrackManager;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;

/**
 * 変換動作の中心マネージャー
 * 1つのマネージャーでは、１つのコンバートだけ実行します。
 * 音声と映像の両方をコンバートしたければ２つ変換マネージャーが必要となるとします。
 * @author taktod
 */
public class XuggleTranscodeManager extends TranscodeManager implements IXuggleTranscodeManager {
	/** 動作ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(XuggleTranscodeManager.class);
	/** 処理thread pool */
	private ExecutorService executor = null;
	/** 元オブジェクトpacket化モジュール */
	private IPacketizer packetizer = null;
	/** デコーダー */
	private IStreamCoder decoder = null;
	/** 動作パケット(使い回します) */
	private IPacket packet = null;
	/**
	 * executorを設定することで動作を向上させます。
	 * @param executor
	 */
	@Override
	public void setExecutorService(ExecutorService executor) {
		this.executor = executor;
	}
	/**
	 * パケット化モジュールを設定
	 * @param packetizer
	 */
	@Override
	public void setPacketizer(IPacketizer packetizer) {
		this.packetizer = packetizer;
	}
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
						reportException(e);
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
				reportException(e);
			}
		}
	}
	/**
	 * 停止処理
	 */
	@Override
	public void close() {
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
				// ここでtrackManagerに処理をまかせる。
				for(Entry<Integer, ITrackManager> entry : getTrackManagers().entrySet()) {
					ITrackManager trackManager = entry.getValue();
					if(!(trackManager instanceof XuggleTrackManager)) {
						throw new Exception("想定外のtrackManagerを検知しました。");
					}
					XuggleTrackManager xuggleTrackManager = (XuggleTrackManager) trackManager;
					xuggleTrackManager.encode(samples.copyReference());
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
				// ここでtrackManagerに処理をまかせる。
				for(Entry<Integer, ITrackManager> entry : getTrackManagers().entrySet()) {
					ITrackManager trackManager = entry.getValue();
					if(!(trackManager instanceof XuggleTrackManager)) {
						throw new Exception("想定外のtrackManagerを検知しました。");
					}
					XuggleTrackManager xuggleTrackManager = (XuggleTrackManager) trackManager;
					xuggleTrackManager.encode(picture);
				}
			}
		}
	}
	/**
	 * 内部動作用のtrackManagerを生成する処理
	 * (この動作はTranscodeManagerから呼び出されます)
	 * @param newId
	 */
	@Override
	protected ITrackManager makeTrackManager(int newId) {
		XuggleTrackManager trackManager = new XuggleTrackManager(this, newId);
		return trackManager;
	}
}
