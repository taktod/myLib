package com.ttProject.xuggle;

import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.transcode.TranscodeManager;
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
	/** 音声リサンプラー */
	private IAudioResampler audioResampler = null;
	/** 映像リサンプラー */
	private IVideoResampler videoResampler = null;
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
				throw new Exception("decoderが取得できませんでした");
			}
			if(!decoder.isOpen()) {
				if(decoder.open(null, null) < 0) {
					throw new Exception("decoderが開けませんでした");
				}
			}
		}
		if(encoder == null) {
			throw new Exception("encoderが未設定です");
		}
		if(!encoder.isOpen()) {
			if(encoder.open(null, null) < 0) {
				throw new Exception("encoderが開けませんでした");
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
				if(samples.getSampleRate() != encoder.getSampleRate()
				|| samples.getFormat() != encoder.getSampleFormat()
				|| samples.getChannels() != encoder.getChannels()) {
					// resamplerをつくってリサンプルする必要あり。
					if(audioResampler == null
					|| audioResampler.getOutputRate() != encoder.getSampleRate()
					|| audioResampler.getOutputFormat() != encoder.getSampleFormat()
					|| audioResampler.getOutputChannels() != encoder.getChannels()) {
						if(audioResampler != null) {
							// これ消さなくてもいいかも・・・xuggleがエラーになる場合はコメントアウトしておきたい。
							audioResampler.delete();
						}
						audioResampler = IAudioResampler.make(
								encoder.getChannels(), samples.getChannels(),
								encoder.getSampleRate(), samples.getSampleRate(),
								encoder.getSampleFormat(), samples.getFormat());
					}
					IAudioSamples spls = IAudioSamples.make(1024, encoder.getChannels());
					int retval = audioResampler.resample(spls, samples, samples.getNumSamples());
					if(retval <= 0) {
						throw new Exception("音声のリサンプルに失敗しました。");
					}
					samples = spls;
				}
				int samplesConsumed = 0;
				while(samplesConsumed < samples.getNumSamples()) {
					// TODO ここでpacket使いまわしても大丈夫か？入力ソースが別で利用されていたらやばくない？
					int retval = encoder.encodeAudio(packet, samples, samplesConsumed);
					if(retval < 0) {
						throw new Exception("音声変換失敗");
					}
					samplesConsumed += retval;
					if(packet.isComplete()) {
						List<Unit> units = depacketizer.getUnit(encoder, packet);
						getTranscodeListener().receiveData(units);
					}
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
				if(picture.getWidth() != encoder.getWidth()
				|| picture.getHeight() != encoder.getHeight()
				|| picture.getPixelType() != encoder.getPixelType()) {
					if(videoResampler == null
					|| videoResampler.getOutputWidth() != encoder.getWidth()
					|| videoResampler.getOutputHeight() != encoder.getHeight()
					|| videoResampler.getOutputPixelFormat() != encoder.getPixelType()) {
						if(videoResampler != null) {
							// 消さなくてもいいかもしれない。xuggleがエラーになるなら消したい。
							videoResampler.delete();
						}
						videoResampler = IVideoResampler.make(
								encoder.getWidth(), encoder.getHeight(), encoder.getPixelType(),
								picture.getWidth(), picture.getHeight(), picture.getPixelType());
						IVideoPicture pct = IVideoPicture.make(encoder.getPixelType(), encoder.getWidth(), encoder.getHeight());
						int retval = videoResampler.resample(pct, picture);
						if(retval <= 0) {
							throw new Exception("映像リサンプル失敗");
						}
						picture = pct;
					}
				}
				if(encoder.encodeVideo(packet, picture, 0) < 0) {
					throw new Exception("映像変換失敗");
				}
				if(packet.isComplete()) {
					List<Unit> units = depacketizer.getUnit(encoder, packet);
					getTranscodeListener().receiveData(units);
				}
			}
		}
	}
}
