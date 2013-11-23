package com.ttProject.transcode.xuggle.encode;

import java.util.List;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITranscodeListener;
import com.ttProject.transcode.xuggle.packet.IDepacketizer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.ICodec.Type;
import com.xuggle.xuggler.IStreamCoder.Direction;

/**
 * videoエンコード動作
 * @author taktod
 */
public class VideoEncoderManager {
	private IPacket packet = null;
	private IVideoResampler resampler = null;
	private boolean threadFlg = false;
	private IStreamCoder encoder = null;
	private IDepacketizer depacketizer = null;
	private ITranscodeListener listener = null;
	/**
	 * 変換結果出力先を保持しておく。
	 * @param listener
	 */
	public void setTranscodeListener(ITranscodeListener listener) {
		this.listener = listener;
	}
	/**
	 * エンコーダーを設定します
	 * @param encoder
	 * @throws Exception
	 */
	public void setEncoder(IStreamCoder encoder) throws Exception {
		if(encoder.getDirection() != Direction.DECODING) {
			throw new Exception("デコーダーが設定されています");
		}
		if(encoder.getCodecType() != Type.CODEC_TYPE_VIDEO) {
			throw new Exception("映像エンコーダーではありません。");
		}
		if(!encoder.isOpen()) {
			if(encoder.open(null, null) < 0) {
				throw new Exception("エンコーダーを開くことができませんでした。");
			}
		}
		// 設定しておく。
		this.encoder = encoder;
	}
	/**
	 * パケットを分解する動作オブジェクトを設定する。
	 * @param depacketizer
	 */
	public void setDepacketizer(IDepacketizer depacketizer) {
		this.depacketizer = depacketizer;
	}
	/**
	 * エンコード処理実体
	 * @param samples
	 * @throws Exception
	 */
	public void encode(IVideoPicture picture) throws Exception {
		if(!picture.isComplete()) {
			// completeしていなかったら処理できません。
			return;
		}
		// thread動作かどうか
		process(picture);
	}
	/**
	 * エンコード内部処理
	 * @param picture
	 * @throws Exception
	 */
	private void process(IVideoPicture picture) throws Exception {
		if(encoder == null) {
			return;
		}
		if(picture.getWidth() != encoder.getWidth()
		|| picture.getHeight() != encoder.getHeight()
		|| picture.getPixelType() != encoder.getPixelType()) {
			if(resampler == null
			|| resampler.getOutputWidth() != encoder.getWidth()
			|| resampler.getOutputHeight() != encoder.getHeight()
			|| resampler.getOutputPixelFormat() != encoder.getPixelType()) {
				if(resampler != null) {
					// 消さなくてもいいかもしれない。xuggleがエラーになるなら消したい。
//					videoResampler.delete();
				}
				resampler = IVideoResampler.make(
						encoder.getWidth(), encoder.getHeight(), encoder.getPixelType(),
						picture.getWidth(), picture.getHeight(), picture.getPixelType());
			}
			IVideoPicture pct = IVideoPicture.make(encoder.getPixelType(), encoder.getWidth(), encoder.getHeight());
			int retval = resampler.resample(pct, picture);
			if(retval <= 0) {
				throw new Exception("映像リサンプル失敗");
			}
			picture = pct;
		}
		if(packet == null) {
			packet = IPacket.make();
		}
		if(encoder.encodeVideo(packet, picture, 0) < 0) {
			throw new Exception("映像変換失敗");
		}
		if(packet.isComplete()) {
			if(depacketizer != null) {
				List<Unit> units = depacketizer.getUnits(encoder, packet);
				if(listener != null) {
					listener.receiveData(units);
				}
			}
		}
	}
}
