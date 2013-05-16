package com.ttProject.xuggle.out.mpegts;

import java.util.HashMap;
import java.util.Map;

import com.ttProject.xuggle.IMediaManager;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.ISimpleMediaFile;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.SimpleMediaFile;

/**
 * mpegtsDataoutputを管理するマネージャー
 * このクラスはbeanやpropertiesでの定義情報を保持するだけのクラスです。
 * 
 * TODO 現状では、staticな変数にsimplemediafileをいれてしまっているので、このクラスが呼び出されたタイミングでxuggleが必須になってしまいます。
 * このままではxuggleのない環境では、利用できないので、そのあたり修正しておきたいところ。
 * @author taktod
 */
public class MpegtsOutputManager implements IMediaManager{
	/** 出力用のmpegtsのストリーム情報の保持 */
	private static final ISimpleMediaFile streamInfo = new SimpleMediaFile();
	/** ffmpegに渡すvideo用のプロパティの詳細設定 */
	private static final Map<String, String> videoProperties = new HashMap<String, String>();
	/** ffmpegに渡すvideo用のフラグデータの詳細情報 */
	private static final Map<IStreamCoder.Flags, Boolean> videoFlags = new HashMap<IStreamCoder.Flags, Boolean>();
	// beanによる設定部、音声
	/**
	 * 音声の有無確認
	 */
	public void setHasAudio(Boolean flg) {
		streamInfo.setHasAudio(flg);
	}
	/**
	 * 音声ビットレート
	 * @param bitRate
	 */
	public void setAudioBitRate(int bitRate) {
		streamInfo.setAudioBitRate(bitRate);
	}
	/**
	 * 音声チャンネル定義 1:モノラル 2:ステレオ等
	 * @param channels
	 */
	public void setAudioChannels(int channels) {
		streamInfo.setAudioChannels(channels);
	}
	/**
	 * 音声サンプリングレート
	 * @param sampleRate
	 */
	public void setAudioSampleRate(int sampleRate) {
		streamInfo.setAudioSampleRate(sampleRate);
	}
	/**
	 * 音声コーデック
	 * @param codecName
	 */
	public void setAudioCodec(String codecName) {
		try {
			streamInfo.setAudioCodec(ICodec.ID.valueOf(codecName));
		}
		catch (Exception e) {
		}
	}
	// beanによる設定部、映像
	/**
	 * 映像の有無
	 */
	public void setHasVideo(Boolean flg) {
		streamInfo.setHasVideo(flg);
	}
	/**
	 * 映像の横幅
	 * @param width
	 */
	public void setVideoWidth(int width) {
		streamInfo.setVideoWidth(width);
	}
	/**
	 * 映像の縦幅
	 * @param height
	 */
	public void setVideoHeight(int height) {
		streamInfo.setVideoHeight(height);
	}
	/**
	 * 映像のビットレート
	 * @param bitRate
	 */
	public void setVideoBitRate(int bitRate) {
		streamInfo.setVideoBitRate(bitRate);
	}
	/**
	 * 映像のフレームレート
	 * 映像のキーフレーム間隔はgのプロパティでいれてほしい。
	 * @param frameRate
	 */
	public void setVideoFrameRate(int frameRate) {
		streamInfo.setVideoFrameRate(IRational.make(1, frameRate));
	}
	/**
	 * globalクオリティー
	 * @param quality
	 */
	public void setVideoGlobalQuality(int quality) {
		streamInfo.setVideoGlobalQuality(quality);
	}
	/**
	 * 映像コーデック
	 * @param codecName
	 */
	public void setVideoCodec(String codecName) {
		try {
			streamInfo.setVideoCodec(ICodec.ID.valueOf(codecName));
		}
		catch (Exception e) {
		}
	}
	/**
	 * ビデオ用の細かいプロパティー
	 * @param properties
	 */
	public void setVideoProperty(Map<String, String> properties) {
		videoProperties.putAll(properties);
	}
	/**
	 * ビデオ用の細かいフラグ
	 * @param flags
	 */
	public void setVideoFlags(Map<String, Boolean> flags) {
		for(String key : flags.keySet()) {
			videoFlags.put(IStreamCoder.Flags.valueOf(key), flags.get(key));
		}
	}
	// 以下内部処理用
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISimpleMediaFile getStreamInfo() {
		return streamInfo;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getVideoProperty() {
		return videoProperties;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<IStreamCoder.Flags, Boolean> getVideoFlags() {
		return videoFlags;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProtocol() {
		return MpegtsHandlerFactory.DEFAULT_PROTOCOL;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormat() {
		return "mpegts";
	}
}
