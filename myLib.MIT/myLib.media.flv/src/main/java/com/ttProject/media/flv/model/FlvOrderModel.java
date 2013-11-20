package com.ttProject.media.flv.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.ITagAnalyzer;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.TagAnalyzer;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.CacheBuffer;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * IndexFileCreatorでつくったデータを参照して、flvのデータを応答するモデル
 * このモデルで動作させるにはいくつか必要なものがある。
 * 1:全体のデータの長さ
 * 2:映像コーデック
 * 3:音声コーデック
 * 4:audioTagByte
 * 5:アクセス先ファイルの全長
 * @author taktod
 */
public class FlvOrderModel {
	private Logger logger = Logger.getLogger(FlvOrderModel.class);
	private boolean isMshSended;
	private int duration = 0;
	private IFileReadChannel idx;
	private CodecType videoCodec = null;
//	private CodecType audioCodec = null; // こっちはこれは必要なくて、audioTagByteがあれば十分
	private byte audioByte = 0;
	private int size;
	// データの読み込みを実行する開始部分。
	private int position = -1;
	
	private final boolean videoFlg;
	private final boolean audioFlg;
	
	private final int startMilliSecond;
	
	// 各MSHタグデータ保持
	private AudioTag audioMshTag = null;
	private VideoTag videoMshTag = null;
	
	private ITagAnalyzer analyzer = new TagAnalyzer();
	/**
	 * コンストラクタ
	 * @param idxFile
	 * @param videoFlg
	 * @param audioFlg
	 * @param startMilliSecond
	 * @throws Exception
	 */
	public FlvOrderModel(IndexFileCreator idxFileCreator, boolean videoFlg, boolean audioFlg, int startMilliSecond) throws Exception {
		idx        = FileReadChannel.openFileReadChannel(idxFileCreator.getIdxFile().toURI().toURL());
		duration   = idxFileCreator.getDuration();
		videoCodec = idxFileCreator.getVideoCodec();
		audioByte  = idxFileCreator.getAudioTagByte();
		size       = idxFileCreator.getSize();
		this.videoFlg = videoFlg;
		this.audioFlg = audioFlg;
		this.startMilliSecond = startMilliSecond;
		// 始めのアクセスデータについて調査しておく必要あり。
		// idxFileを読み込んでaudioMshとvideoMshについて調査
		// 可能なら、自身のデータの先頭位置についても調査しておく(一番はじめにアクセスするデータ位置(開始時刻依存))
	}
	/**
	 * 初期セットアップ
	 * TODO これ・・・sourceへのアクセスがないとinitializeできない。
	 * あっちの場合は(mp4の方)、mshデータをタグとして記録しているので、このままでも運用できたわけか・・・
	 * 仕方ないのでpublic化した。始めに呼び出す必要あり。
	 */
	public void initialize(IFileReadChannel source) throws Exception {
		isMshSended = false;
		position = -1;
		while(idx.position() < idx.size()) {
			ByteBuffer buffer = BufferUtil.safeRead(idx, 9);
			byte flg = buffer.get();
			// 位置
			int position = buffer.getInt();
			// timestamp
			int timestamp = buffer.getInt();
			Tag tag = null;
			switch(flg) {
			case 0: // keyFrame
				break;
			case 1: // videoMsh
				logger.info("初期化でvideoMsh発見");
				source.position(position);
				tag = analyzer.analyze(source);
				videoMshTag = (VideoTag) tag;
				continue;
			case 2: // audioMsh
				logger.info("初期化でaudioMsh発見");
				source.position(position);
				tag = analyzer.analyze(source);
				audioMshTag = (AudioTag) tag;
				continue;
			default:
				throw new Exception("解析不能なデータを受け取りました。");
			}
			if(timestamp >= startMilliSecond) {
				// 読み込みを実行する位置
				source.position(position);
				this.position = position;
				// 位置が決定した場合は、mshを応答してやらないとだめなんだが・・・
				break;
			}
		}
	}
	/**
	 * flvHeaderを応答する。
	 * @return
	 */
	public FlvHeader getFlvHeader() {
		FlvHeader flvHeader = new FlvHeader();
		if(!audioFlg) {
			flvHeader.setAudioFlg(false);
		}
		else {
			flvHeader.setAudioFlg(audioByte != 0x00);
		}
		if(!videoFlg) {
			flvHeader.setVideoFlg(false);
		}
		else {
			flvHeader.setVideoFlg(videoCodec != CodecType.NONE);
		}
		return flvHeader;
	}
	/**
	 * 応答データのvideoMshTagを応答します。
	 * @return
	 */
	public VideoTag getVideoMsh() {
		return videoMshTag;
	}
	/**
	 * 応答データのaudioMshTagを応答します。
	 * @return
	 */
	public AudioTag getAudioMsh() {
		return audioMshTag;
	}
	/**
	 * データを順番に応答します。データがない場合はnullを返します。まだあるけど、準備できていない場合はlistを返します。
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public List<Tag> nextTagList(IReadChannel source) throws Exception {
		List<Tag> result = new ArrayList<Tag>();
		if(position == -1) {
			logger.info("初アクセスなので初期化する");
			// 始めのデータの場合はindexファイルのデータを確認して、自分の欲しいtimestampデータがない場合
			// 推定でアクセスして、そのデータにアクセスするようにしておく。

			// positionが-1の場合は位置が決定しなかったので、推測する。
			position = (int)((long)startMilliSecond * size / duration);
			logger.info("startPos:" + Integer.toHexString(position));
			// 1:アクセスがきたら00 00 00 XXもしくは 00 00 00 00 XXの位置のデータをみつける。
			source.position(position);
			// ここから読み込んでいく。shortで0がでたら、そこがあやしい。
			CacheBuffer cacheBuffer = new CacheBuffer(source);
			// shortで取得していって中身を見たいがあとで巻き戻す動作が若干必要。
			while(cacheBuffer.remaining() > 1) {
				if(cacheBuffer.getShort() == 0) {
					// ここで取得できるpositionは00 00 [XX
					// 次のデータも確認して、00もしくは00 00ならtrackIDである可能性が高い
					// 次以降のデータで0x00以外のデータがくるまで、読み込みつづける。
					byte b = 0;
					while((b = cacheBuffer.get()) == 0) {
						;
					}
					logger.info("position:" + Integer.toHexString(cacheBuffer.position()));
					logger.info(Integer.toHexString(b));
					// bの値がaudioのtagByteか
					int pos = cacheBuffer.position() - 12;
					if(b == audioByte) {
						logger.info("タグをみつけたと思われる。");
						// 12バイト前のデータを見てみる。
						source.position(pos);
						cacheBuffer = new CacheBuffer(source);
						if(cacheBuffer.get() != 0x08) {
							logger.info("音声タグではなかったのでやり直し");
							continue;
						}
						source.position(pos);
						break;
					}
					else if((b & 0x0F) == CodecType.getVideoByte(videoCodec)) {
						logger.info("タグをみつけたと思われる。");
						// 12バイト前のデータを見てみる。
						source.position(pos);
						cacheBuffer = new CacheBuffer(source);
						if(cacheBuffer.get() != 0x09) {
							logger.info("映像タグではなかったのでやり直し");
							continue;
						}
						source.position(pos);
						break;
					}
				}
			}
			// tagを見つけたので、あとはkeyFrameの開始位置までスキップさせる。
			Tag tag = null;
			TagPositionAnalyzer analyzer = new TagPositionAnalyzer();
			while((tag = analyzer.analyze(source)) != null) {
				if(!videoFlg || videoCodec == CodecType.NONE) {
					// 音声タグをみつけたらそこから実行させる。
					if(tag instanceof AudioTag) {
						AudioTag aTag = (AudioTag) tag;
						aTag.analyze(source);
						position = tag.getPosition();
						if(audioMshTag != null) {
							audioMshTag.setTimestamp(aTag.getTimestamp());
							result.add(audioMshTag);
						}
						result.add(aTag);
						return result;
					}
				}
				else {
					if(tag instanceof VideoTag) {
						VideoTag vTag = (VideoTag) tag;
						if(vTag.isKeyFrame()) {
							vTag.analyze(source);
							position = tag.getPosition();
							if(videoMshTag != null) {
								videoMshTag.setTimestamp(vTag.getTimestamp());
								result.add(videoMshTag);
							}
							if(audioMshTag != null) {
								audioMshTag.setTimestamp(vTag.getTimestamp());
								result.add(audioMshTag);
							}
							result.add(vTag);
							return result;
						}
					}
				}
			}
			logger.info("ループぬけた");
		}
		// このタイミングで続きの動作を実行します。
		Tag tag = analyzer.analyze(source);
		if(tag == null) {
			if(source.position() == source.size()) {
				logger.info("終端？");
			}
			else {
				logger.info("まだ");
			}
			// 終端までいったのかが問題。
			return null;
		}
		if(!isMshSended) {
			if(audioMshTag != null) {
				audioMshTag.setTimestamp(tag.getTimestamp());
				result.add(audioMshTag);
			}
			if(videoMshTag != null) {
				videoMshTag.setTimestamp(tag.getTimestamp());
				result.add(videoMshTag);
			}
			isMshSended = true;
		}
		result.add(tag);
		return result;
	}
	public void close() {
		if(idx != null) {
			try {
				idx.close();
			}
			catch (Exception e) {
			}
			idx = null;
		}
	}
}
