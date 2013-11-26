package com.ttProject.transcode.xuggle.track;

import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

/**
 * 映像処理の細部抜き出し
 * @author taktod
 *
 */
public class VideoTrackModule extends TrackModule {
	/** trackManager参照 */
	private final XuggleTrackManager trackManager;
	/** 処理パケット(可能なら使い回します) */
	private IPacket packet = null;
	/** リサンプル動作(必要なら実行する) */
	private IVideoResampler resampler = null;
	/**
	 * コンストラクタ
	 * @param trackManager
	 */
	protected VideoTrackModule(XuggleTrackManager trackManager) {
		this.trackManager = trackManager;
	}
	/**
	 * 処理実行
	 * @param xuggleObject IVideoPictureであることを期待しています。
	 */
	@Override
	protected void process(Object xuggleObject) {
		try {
			if(xuggleObject instanceof IVideoPicture) {
				throw new Exception("データがVideoPictureではありませんでした。異常です。");
			}
			IVideoPicture picture = (IVideoPicture) xuggleObject;
			// エンコーダー参照
			IStreamCoder encoder = trackManager.getEncoder();
			if(encoder == null) {
				throw new Exception("プロセス開始前にencoderが設定されていませんでした。");
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
//								videoResampler.delete();
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
			if(!encoder.isOpen()) {
				if(encoder.open(null, null) < 0) {
					throw new Exception("エンコーダーが開けませんでした");
				}
			}
			if(encoder.encodeVideo(packet, picture, 0) < 0) {
				throw new Exception("映像変換失敗");
			}
			trackManager.applyData(packet);
		}
		catch(Exception e) {
			trackManager.reportException(e);
		}
	}
	/**
	 * 対象オブジェクトがこのプログラムで実行可能であるか確認しておく。
	 * @return true:可能 false:不能
	 */
	@Override
	protected boolean checkObject(Object xuggleObject) {
		if(!(xuggleObject instanceof IVideoPicture)) {
			// video用の処理でなければスルー
			return false;
		}
		IVideoPicture picture = (IVideoPicture) xuggleObject;
		if(!picture.isComplete()) {
			// データが完成していなかったらスルー
			return false;
		}
		return true;
	}
	/**
	 * 終了処理
	 */
	@Override
	protected void close() {
	}
}
