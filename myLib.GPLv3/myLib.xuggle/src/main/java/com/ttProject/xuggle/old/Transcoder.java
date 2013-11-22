package com.ttProject.xuggle.old;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IError;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.ISimpleMediaFile;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.SimpleMediaFile;

/**
 * Xuggleでメディアデータのコンバートを実行します。
 * ・mp3とjpegのsegmentCreator用の中途処理の追加
 * ・mpegtsHandlerにタイムスタンプやキーフレーム情報を参照させるため処理
 * ２点追加したあります。
 * 
 * @author taktod
 */
@SuppressWarnings("deprecation")
public class Transcoder implements Runnable {
	/** ロガー */
	private final Logger logger = LoggerFactory.getLogger(Transcoder.class);
	/** 外部から指定されるもの。 */
	private ISimpleMediaFile outputStreamInfo; // 出力ファイルテンプレート
	private Map<String, String> videoProperties = new HashMap<String, String>(); // 出力プロパティーテンプレート
	private Map<IStreamCoder.Flags, Boolean> videoFlags = new HashMap<IStreamCoder.Flags, Boolean>(); // 出力フラグテンプレート
	/** 外部から指定されるもの。カスタム */
	private final String inputProtocol;  // 入力プロトコル
	private final String inputFormat;    // 入力フォーマット
	private final String outputProtocol; // 出力プロトコル
	private final String outputFormat;   // 出力フォーマット
	private final String taskName;       // このTranscoderの動作タスク名

	/** 内部で定義するもの。 */
	/** 入力データ編 */
	private IContainer   inputContainer  = null; // 入力コンテナ
	private IStreamCoder inputAudioCoder = null; // 入力audioデコード
	private IStreamCoder inputVideoCoder = null; // 入力videoデコード
	private int audioStreamId = -1; // 設定audioストリーム番号
	private int videoStreamId = -1; // 設定videoストリーム番号

	/** リサンプラー編 */
	private boolean isVideoResamplerChecked = false; // videoリサンプラーの必要性を確認済みかどうかフラグ
	private boolean isAudioResamplerChecked = false; // audioリサンプラーの必要性を確認済みかどうかフラグ
	private IVideoResampler videoResampler  = null; // videoリサンプラー
	private IAudioResampler audioResampler  = null; // audioリサンプラー

	/** 出力データ編 */
	private IContainer   outputContainer  = null; // 出力コンテナー
	private IStreamCoder outputAudioCoder = null; // 出力audioエンコード
	private IStreamCoder outputVideoCoder = null; // 出力videoエンコード
	
	/** 動作定義 */
	private volatile boolean keepRunning = true; // 動作中フラグ
	/** 時刻操作 */
	private long timestamp = 0;
	/** mp3のセグメント作成 */
//	private Mp3SegmentCreator mp3SegmentCreator = null;
	/** jpegのセグメント作成 */
//	private JpegSegmentCreator jpegSegmentCreator = null;
	/** 動画パケットがキーフレームであるか確認 */
	private boolean isKey = false;
	/**
	 * キーフレームかどうか参照
	 * 動画の方のみ考慮しています。
	 * @return
	 */
	public boolean isKey() {
		return isKey;
	}
	/**
	 * タイムスタンプ応答
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * タイムスタンプを計算します。(ミリ秒単位に書き換えて扱います。)
	 * @param packet
	 */
	private void setOutputContainerData(IPacket packet, Boolean isKey) {
		if(packet == null) {
			return;
		}
		IRational timebase = packet.getTimeBase();
		if(timebase == null) {
			return;
		}
		this.timestamp = packet.getTimeStamp() * 1000 * timebase.getNumerator() / timebase.getDenominator();
		if(isKey != null) {
			this.isKey = isKey;
		}
	}
	/**
	 * コンストラクタ
	 * 入力、出力等の定義データを受け取る
	 */
	public Transcoder(
			IMediaManager inputManager,
			IMediaManager outputManager,
			String name) {
//			Mp3SegmentCreator mp3SegmentCreator,
//			JpegSegmentCreator jpegSegmentCreator) {
		logger.info("transcoderを初期化しました。");
		// 出力定義
		this.outputProtocol = outputManager.getProtocol();
		this.outputFormat   = outputManager.getFormat();
		// 出力変換定義
		this.outputStreamInfo = outputManager.getStreamInfo();
		this.videoProperties.putAll(outputManager.getVideoProperty());
		this.videoFlags.putAll(outputManager.getVideoFlags());
		// 入力定義
		this.inputProtocol  = inputManager.getProtocol();
		this.inputFormat    = inputManager.getFormat();
		// 動作名定義
		this.taskName = name;
		// mp3用動作
//		this.mp3SegmentCreator  = mp3SegmentCreator;
		// jpeg用動作
//		this.jpegSegmentCreator = jpegSegmentCreator;
	}
	/**
	 * threadの実行
	 */
	@Override
	public void run() {
		try {
			// 読み込み用のコンテナをオープン
			openInputContainer();
			// 変換を実行
			transcode();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			logger.info("処理がおわりました。");
			// おわったら変換をとめる。
			closeAll();
		}
	}
	/**
	 * 停止します。
	 */
	public void close() {
		keepRunning = false;
	}
	/**
	 * 必要のないオブジェクトの解放を実施します。
	 */
	private void closeAll() {
		try {
			closeOutputContainer(); // 出力コンテナの解放
			closeInputContainer(); // 入力コンテナの解放
		}
		catch (Exception e) {
			logger.error("停止動作でエラーが発生しました。", e);
		}
		finally {
		}
	}
	/**
	 * 読み込み用のコンテナを開く
	 */
	private void openInputContainer() {
		String url;
		int retval = -1;

		url = inputProtocol + ":" + taskName;
		ISimpleMediaFile inputInfo = new SimpleMediaFile();
		inputInfo.setURL(url);
		inputContainer = IContainer.make();
		IContainerFormat inputFormat = IContainerFormat.make();
		inputFormat.setInputFormat(this.inputFormat); // 形式をflvにしておく。
		retval = inputContainer.open(url, IContainer.Type.READ, inputFormat, true, false);
		if(retval < 0) {
			throw new RuntimeException("入力用のURLを開くことができませんでした。" + url);
		}
	}
	/**
	 * 変換を実行
	 */
	private void transcode() {
		int retval = -1;
		IPacket packet = IPacket.make(); // 動作パケットの受け皿
		while(keepRunning) { // 変換があるかぎりずっと動作しておく。
			// パケットの入力を取得する。
			retval = inputContainer.readNextPacket(packet);
			if(retval < 0) {
				logger.error("パケット取得エラー: {}, {}", IError.make(retval), retval); // このエラー番号は環境に依存する？
				if("Resource temporarily unavailable".equals(IError.make(retval).getDescription())) {
					// リソースが一時的にない場合 つづけていれば動作可能になるので、この場合だけ例外的にスキップする。
					continue;
				}
				keepRunning = false;
				break;
			}

			// 入力コーダーを確認します。
			if(!checkInputCoder(packet)) {
				// 処理する必要のないパケットなのでスキップします。
				continue;
			}

			// 各エレメントの変換処理に移行します。
			int index = packet.getStreamIndex();
			if(index == audioStreamId) {
				executeAudio(packet);
			}
			else if(index == videoStreamId) {
				executeVideo(packet);
			}
		}
	}
	/**
	 * 入力系のオブジェクトを閉じます。
	 */
	private void closeInputContainer() {
		if(inputVideoCoder != null) {
			logger.info("videoコーダーを閉じます。");
			inputVideoCoder.close();
			inputVideoCoder = null;
		}
		if(inputAudioCoder != null) {
			logger.info("audioコーダーを閉じます。");
			inputAudioCoder.close();
			inputAudioCoder = null;
		}
		if(inputContainer != null) {
			logger.info("入力コンテナを閉じます。");
			inputContainer.close();
			inputContainer = null;
		}
	}
	/**
	 * 出力系のオブジェクトを閉じます。
	 */
	private void closeOutputContainer() {
		if(outputContainer != null) {
			logger.info("出力コンテナに停止時データを書き込みます。");
			outputContainer.writeTrailer();
		}
		if(outputVideoCoder != null) {
			logger.info("videoエンコードコーダーを閉じます。");
			outputVideoCoder.close();
			outputVideoCoder = null;
		}
		if(outputAudioCoder != null) {
			logger.info("audioエンコードコーダーを閉じます。");
			outputAudioCoder.close();
			outputAudioCoder = null;
		}
		if(outputContainer != null) {
			logger.info("出力コンテナを閉じます。");
			outputContainer.close();
			outputContainer = null;
		}
	}
	/**
	 * 出力用のコンテナを開く
	 */
	private void openOutputContainer() {
		logger.info("outputContainerを開きます。");
		String url;
		int retval = -1;
		url = outputProtocol + ":" + taskName;
		ISimpleMediaFile outputInfo = new SimpleMediaFile();
		outputInfo.setURL(url);
		outputContainer = IContainer.make();
		IContainerFormat outputFormat = IContainerFormat.make();
		outputFormat.setOutputFormat(this.outputFormat, url, null);
		retval = outputContainer.open(url, IContainer.Type.WRITE, outputFormat);
		if(retval < 0) {
			throw new RuntimeException("出力用のURLを開くことができませんでした。" + url);
		}
		if(videoStreamId != -1) {
			// videoストリームを開く
			openOutputVideoCoder();
		}
		if(audioStreamId != -1) {
			// audioストリームを開く
			openOutputAudioCoder();
		}
		retval = outputContainer.writeHeader();
		if(retval < 0) {
			throw new RuntimeException("出力ヘッダの書き込みに失敗しました。");
		}
	}
	/**
	 * 出力videoコーダーを開きます。
	 */
	private void openOutputVideoCoder() {
		logger.info("出力videoCoderを開きます。");
		IStream outStream = outputContainer.addNewStream(outputContainer.getNumStreams());
		if(outStream == null) {
			throw new RuntimeException("video出力用のストリーム生成ができませんでした。");
		}
		IStreamCoder outCoder = outStream.getStreamCoder();
		ICodec outCodec = ICodec.findEncodingCodec(outputStreamInfo.getVideoCodec());
		if(outCodec == null) {
			throw new RuntimeException("video出力用のエンコードコーデックを取得することができませんでした。");
		}
		outCoder.setCodec(outCodec);
		outCoder.setWidth(outputStreamInfo.getVideoWidth());
		outCoder.setHeight(outputStreamInfo.getVideoHeight());
		outCoder.setPixelType(outputStreamInfo.getVideoPixelFormat());
		outCoder.setGlobalQuality(outputStreamInfo.getVideoGlobalQuality());
		outCoder.setBitRate(outputStreamInfo.getVideoBitRate());
		outCoder.setFrameRate(outputStreamInfo.getVideoFrameRate());
		outCoder.setNumPicturesInGroupOfPictures(outputStreamInfo.getVideoNumPicturesInGroupOfPictures());
		// 細かいプロパティ
		for(String key : videoProperties.keySet()) {
			outCoder.setProperty(key, videoProperties.get(key));
		}
		// flags
		for(IStreamCoder.Flags key : videoFlags.keySet()) {
			outCoder.setFlag(key, videoFlags.get(key));
		}
		if(outputStreamInfo.getVideoTimeBase() != null) {
			outCoder.setTimeBase(outputStreamInfo.getVideoTimeBase());
		}
		else {
			outCoder.setTimeBase(IRational.make(1, 1000));
		}
		if(outCoder.open() < 0) {
			throw new RuntimeException("出力コーダーをオープンするのに失敗しました。");
		}
		// 開くことに成功したので以降これを利用する。
		outputVideoCoder = outCoder;
	}
	/**
	 * 出力audioコーダーを開きます。
	 */
	private void openOutputAudioCoder() {
		logger.info("出力audioCoderを開きます。");
		IStream outStream = outputContainer.addNewStream(outputContainer.getNumStreams());
		if(outStream == null) {
			throw new RuntimeException("audio出力用のストリーム生成ができませんでした。");
		}
		IStreamCoder outCoder = outStream.getStreamCoder();
		ICodec outCodec = ICodec.findEncodingCodec(outputStreamInfo.getAudioCodec());
		if(outCodec == null) {
			throw new RuntimeException("audio出力用のエンコーダーを取得することができませんでした。");
		}
		outCoder.setCodec(outCodec);
		outCoder.setBitRate(outputStreamInfo.getAudioBitRate());
		outCoder.setSampleRate(outputStreamInfo.getAudioSampleRate());
		outCoder.setChannels(outputStreamInfo.getAudioChannels());
		outCoder.open();
		// 開くことに成功したので以降これを利用する。
		outputAudioCoder = outCoder;
	}
	/**
	 * パケット情報から、動作に必要なコーダーを開きます。
	 * また映像or音声のあたらしいパケットを入手した場合は出力ファイルを変更する必要がでてくるので、そっちの処理も実行します。
	 * とりあえず始めはaddNewStreamがあるので、そっちでできることを期待します。
	 * @param packet
	 * @return false:このパケットは処理せず次にすすむ true:コンバート処理に移す。
	 */
	private boolean checkInputCoder(IPacket packet) {
		// どうやらContainerにaddNewStreamをしない限り、動作できるらしい。(あとから追加が可能？っぽい。)
		IStream stream = inputContainer.getStream(packet.getStreamIndex());
		if(stream == null) {
			// ストリームが取得できませんでした。(欠落しているデータの場合があるので、発生してもおかしくない動作)
			return false;
		}
		IStreamCoder coder = stream.getStreamCoder();
		if(coder == null) {
			// coderが取得できませんでした。(欠落しているストリームである場合があるので、発生してもおかしくない動作)
			return false;
		}
		if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
			// audioCodec
			if(inputAudioCoder == null) {
				// audioの入力Coderが設定されていない場合は、はじめてのアクセスなので、処理する必要がある。
				if(!outputStreamInfo.hasAudio()) {
					// audio出力が必要ない場合は処理しない。
					return false;
				}
				audioStreamId = packet.getStreamIndex();
				// 必要があるなら、出力コンテナーを閉じる
				closeOutputContainer();
				// 出力コンテナーを生成する。
				openOutputContainer();
			}
			else if(inputAudioCoder.hashCode() == coder.hashCode()) {
				// コーダーが一致する場合はこのままコーダーをつかって処理すればよい。
				return true;
			}
			else {
				// 一致しない新しいパケットがきた場合は、あたらしいので今後動作するようにする。
				inputAudioCoder.close();
				inputAudioCoder = null;
				audioStreamId = packet.getStreamIndex();
			}
			// 入力Audioコーダーとリサンプラーを準備しておく。
			if(coder.open() < 0) {
				throw new RuntimeException("audio入力用のデコーダーを開くのに失敗しました。");
			}
			isAudioResamplerChecked = false;
			inputAudioCoder = coder;
		}
		else if(coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
			// videoCodec
			if(inputVideoCoder == null) {
				// videoの入力Coderが設定されていない場合は、初回アクセスなので処理する必要がある。
				if(!outputStreamInfo.hasVideo()) {
					// 設定でvideo出力をOFFにしている場合は処理しない。
					return false;
				}
				logger.info("videoCoderのはじめてのOpenです: {}", coder);
				videoStreamId = packet.getStreamIndex();
				// 必要があるなら、出力コンテナーを閉じる
				closeOutputContainer();
				// 出力コンテナーを生成する。
				openOutputContainer();
			}
			else if(inputVideoCoder.hashCode() == coder.hashCode()){
//				logger.info("現行のビデオコーダーと一致するデータです。");
				// コーダーが一致する場合はこのままコーダーをつかって処理をすればよい。
				return true;
			}
			else {
				// 一致しない新しいパケットがきた場合は、今後は新しいので動作するようにする。
				logger.info("現行のビデオコーダーと一致しないデータなので、あたらしく開きます。");
				inputVideoCoder.close();
				inputVideoCoder = null;
				videoStreamId = packet.getStreamIndex();
			}
			// 入力Videoコーダーを準備しておく。
			if(coder.open() < 0) {
				throw new RuntimeException("video入力用のデコーダーを開くのに失敗しました。");
			}
			isVideoResamplerChecked = false;
			inputVideoCoder = coder;
		}
		else {
			// 音声でも映像でもないものは処理しない。
			return false;
		}
		return true;
	}
	/**
	 * 音声のリサンプラーを確認します。
	 * @param samples
	 */
	private void checkAudioResampler(IAudioSamples samples) {
		if(isAudioResamplerChecked) {
			// すでにリサンプラー確認済み
			return;
		}
		audioResampler = null;
		isAudioResamplerChecked = true;
		if(outputAudioCoder.getSampleRate() == samples.getSampleRate()
				&& outputAudioCoder.getChannels() == samples.getChannels()) {
			// サンプリングレートとチャンネル数が一致する場合は変換なしで動作するので、そのまま進める。
			return;
		}
		audioResampler = IAudioResampler.make(outputAudioCoder.getChannels(), samples.getChannels(), outputAudioCoder.getSampleRate(), samples.getSampleRate());
		if(audioResampler == null) {
			throw new RuntimeException("audioリサンプラーが開けませんでした。");
		}
	}
	/**
	 * 映像のリサンプラーを確認します。
	 * @param picture
	 */
	private void checkVideoResampler(IVideoPicture picture) {
		if(isVideoResamplerChecked) {
			// 既にビデオのデータは確認済みなので、処理する必要なし。
			return;
		}
		videoResampler = null;
		isVideoResamplerChecked = true;
		if(outputVideoCoder.getPixelType() == picture.getPixelType()
				&& outputVideoCoder.getWidth() == picture.getWidth()
				&& outputVideoCoder.getHeight() == picture.getHeight()) {
			// サイズとピクセルタイプが一致する場合は変換する必要なし。
			return;
		}
		videoResampler = IVideoResampler.make(outputVideoCoder.getWidth(), outputVideoCoder.getHeight(), outputVideoCoder.getPixelType(), picture.getWidth(), picture.getHeight(), picture.getPixelType());
		if(videoResampler == null) {
			throw new RuntimeException("videoリサンプラーを開くのに失敗しました。");
		}
	}
	/**
	 * 音声データを処理します。
	 * @param targetPacket
	 */
	private void executeAudio(IPacket targetPacket) {
		int retval = -1;
		// デコード後の生入力データ
		IAudioSamples inSamples = IAudioSamples.make(1024, inputAudioCoder.getChannels());
		// リサンプル後の変換前データ
		IAudioSamples reSamples = null;
		int offset = 0;
		while(offset < targetPacket.getSize()) {
			// デコード
			retval = inputAudioCoder.decodeAudio(inSamples, targetPacket, offset);
			if(retval <= 0) {
				logger.warn("デコードに失敗するパケットがきても無視し、次にすすむことにする。");
				return;
			}
			offset += retval;
			
			IAudioSamples postDecode = inSamples;
			if(postDecode.isComplete()) {
				// エンコーダーが解釈できるデータにリサンプルします。
				reSamples = resampleAudio(postDecode);
				
				if(reSamples.isComplete()) {
					// エンコードを実施します。
					encodeAudio(targetPacket, reSamples);
				}
			}
		}
	}
	/**
	 * 音声データをリサンプルします。入力と出力の形式が違うときに合わせます。
	 * @param samples
	 * @return
	 */
	private IAudioSamples resampleAudio(IAudioSamples samples) {
		checkAudioResampler(samples);
		if(audioResampler == null || samples == null) {
			// リサンプルする必要がない場合はそのまま応答する。
			return samples;
		}
		IAudioSamples outSamples = IAudioSamples.make(1024, outputAudioCoder.getChannels());
		IAudioSamples preResample = samples;
		int retval = -1;
		retval = audioResampler.resample(outSamples, preResample, preResample.getNumSamples());
		if(retval < 0) {
			throw new RuntimeException("audioのリサンプルに失敗しました。");
		}
		IAudioSamples postResample = outSamples;
		return postResample;
	}
	/**
	 * オーディオデータをエンコードします。
	 * @param samples
	 */
	private void encodeAudio(IPacket inPacket, IAudioSamples samples) {
		int retval = -1;
		IPacket outPacket = IPacket.make();
		
		IAudioSamples preEncode = samples;
		
		int numSamplesConsumed = 0;
		while(numSamplesConsumed < preEncode.getNumSamples()) {
			retval = outputAudioCoder.encodeAudio(outPacket, preEncode, numSamplesConsumed);
			if(retval <= 0) {
				logger.warn("audioのエンコードに失敗しましたが、無視して、続けます。");
				break;
			}
			numSamplesConsumed += retval;
			
			if(outPacket.isComplete()) {
				// mp3segmentをつくる場合は処理する。
/*				if(mp3SegmentCreator != null) {
					ByteBuffer b = outPacket.getByteBuffer();
					byte[] data = new byte[b.limit()];
					b.get(data);
					mp3SegmentCreator.writeSegment(data, b.limit(), inPacket.getTimeStamp());
				}*/
				// データ参照用の情報を準備しておく。
				setOutputContainerData(outPacket, null); // 音声パケットはキー情報にはさわらないようにしておきます。
				// mpegtsのデータができあがったのでコンテナに出力
				outputContainer.writePacket(outPacket);
			}
		}
	}
	/**
	 * 映像データを処理します。
	 * @param targetPacket
	 */
	private void executeVideo(IPacket targetPacket) {
		int retval = -1;
		// デコード後の生入力データ
		IVideoPicture inPicture = IVideoPicture.make(inputVideoCoder.getPixelType(), inputVideoCoder.getWidth(), inputVideoCoder.getHeight());
		// リサンプル後の変換前データ
		IVideoPicture reSample = null;
		int offset = 0;
		while(offset < targetPacket.getSize()) {
			retval = inputVideoCoder.decodeVideo(inPicture, targetPacket, offset);
			if(retval <= 0) {
				logger.warn("デコードに失敗するパケットがきても、無視し次にすすむことにします。");
				return;
			}
			offset += retval;
			
			IVideoPicture postDecode = inPicture;
			if(postDecode.isComplete()) {
				// このタイミングで必要があるなら、jpegコンバートしておく。
//				if(jpegSegmentCreator != null) {
//					jpegSegmentCreator.makeFramePicture(postDecode);
//				}
				// エンコーダーが解釈できるデータにリサンプルします。
				reSample = resampleVideo(postDecode);
				
				if(reSample.isComplete()) {
					// エンコードを実行します。
					encodeVideo(targetPacket, reSample);
				}
			}
		}
	}
	/**
	 * 映像をリサンプルします。
	 * @param picture
	 * @return
	 */
	private IVideoPicture resampleVideo(IVideoPicture picture) {
		checkVideoResampler(picture);
		if(videoResampler == null || picture == null) {
			// リサンプルする必要なし。
			return picture;
		}
		IVideoPicture outPicture = IVideoPicture.make(outputVideoCoder.getPixelType(), outputVideoCoder.getWidth(), outputVideoCoder.getHeight());
		
		IVideoPicture preResample = picture;
		int retval = -1;
		retval = videoResampler.resample(outPicture, preResample);
		if(retval < 0) {
			throw new RuntimeException("videoのリサンプルに失敗しました。");
		}
		IVideoPicture postResample = outPicture;
		return postResample;
	}
	/**
	 * 映像をエンコードします。
	 * @param picture
	 */
	private void encodeVideo(IPacket inPacket, IVideoPicture picture) {
		int retval = -1;
		IPacket outPacket = IPacket.make();
		
		IVideoPicture preEncode = picture;
		@SuppressWarnings("unused")
		int numBytesConsumed = 0;
		if(preEncode.isComplete()) {
			retval = outputVideoCoder.encodeVideo(outPacket, preEncode, 0);
			if(retval <= 0) {
				// TODO どうしてもここでビデオエンコードがうまくいかない。
				/*
				 * しらべるべきことメモ
				 * まず、エンコード失敗について、音声と映像で違いがあるか確認する。
				 * つづいて、入力されたflvデータについて違いがあるか確認する。
				 * その他確認事項を探し出してなにが違うのか調査する。
				 * 
				 * TODO 結果としては、rtmpで送られてくるflvデータが実はリセットされていなかった模様です。
				 * TranscodeWriterの処理で、publishし直すたびにtimestampをずらすようにしたところ動作しました。
				 * ただし、AACベースにすると、mp3segmentは問題ないですが、tsファイルに音飛びが発生する模様。
				 * 原因は不明ですが、ffmpegの変換パラメーターに難があるのだろうか？
				 */
//				logger.info("inPacket: {}", inPacket);
//				logger.info("videoエンコードに失敗しましたが、このまま続けます。");
				// ここでの失敗は0になるらしい。
				return;
			}
			numBytesConsumed += retval;
			if(outPacket.isComplete()) {
				// mp3segmentをつくる場合は、無音部の対処もしておく。
/*				if(mp3SegmentCreator != null) {
					mp3SegmentCreator.updateSegment(inPacket.getTimeStamp());
				}*/
				// データ参照用の情報を準備しておく。
				setOutputContainerData(outPacket, outPacket.isKey());
				// mpegtsのデータができあがったのでコンテナに出力
				outputContainer.writePacket(outPacket);
			}
		}
	}
}
