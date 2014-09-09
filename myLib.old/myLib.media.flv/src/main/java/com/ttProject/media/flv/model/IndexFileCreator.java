/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.flv.model;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.media.flv.CodecType;
import com.ttProject.media.flv.FlvHeader;
import com.ttProject.media.flv.Tag;
import com.ttProject.media.flv.tag.AudioTag;
import com.ttProject.media.flv.tag.VideoTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * 高速seek用の一時ファイルを作成するプログラム
 * とりあえず知りたいことは
 * mshもtimestamp -> 位置
 * timestamp -> keyFrameの位置
 * これが知りたい
 * あとはmshのデータも欲しい
 * 
 * とりあえずこうする。
 * １：先頭のデータを読み取ってmshを回収しておく。
 * ２：終端のデータを読み取ってflvの長さを知っておく。
 * ３：ここから解析開始。
 * ４：解析がおわっている部分へのアクセスの場合
 * →解析結果から正しいmshとkeyFrame位置を取得してそれに合わせて動作させる。
 * ５：解析がおわっていない部分へのアクセスの場合
 * 全体のbyte数と終端データのtimestampからおおよそのデータ位置を割り出す。
 * そこから00 00 00 XXもしくは00 00 00 00 XXになっている部分を割り出す。(これがtagのtrackIDである可能性があるため。)
 * みつけたら11バイト前からのデータを確認して、tagデータであると仮定して処理する。
 * skip後の位置に次のtagを見つけることができたらtag確定
 * tagに沿って位置を探していって、keyFrameがきたらその位置から再生を実行することにする。
 * 
 * の２本立てでいいと思われる。
 * 
 * というわけでやらないといけないことは次のようになります。
 * 現在の位置から解析させるためのデータ記録動作を作成。
 * [開始位置][内容]の羅列のみでいいと思う。
 * [tagの位置(int 4byte)] [type(byte 1byte)]
 * type:00 keyFrame 01 videoMSH 02 audioMSH
 * みたいな感じのファイルをつくっておく。
 * 作成は始めのmshの確認だけ、本流でやっておく。(別途アクセスがあったときに必要だから)
 * その後の作成は別threadにやらせる(任意だけどやった方がよい:間にmshがある場合とかにも対応できるし・・・)
 * 
 * データへのアクセスは別の関数で実行、実行するとその時間からはじまる映像のkeyFrame部が応答される。
 * 映像のないデータの場合は音声のフレームの開始位置が応答される。
 * 見つけ方は上記に書いた方法
 * 
 * みたいな感じのプログラムをつくっておく。
 * @author taktod
 */
public class IndexFileCreator {
	private Logger logger = Logger.getLogger(IndexFileCreator.class);
	private final File targetFile;
	private FileOutputStream idx; // 書き込み対象
	
	private long startTime;
	private long lastFoundTime;
	
	private int duration = 0;
	private IFileReadChannel source;
	private CodecType videoCodec = null;
	private CodecType audioCodec = null;
	private byte audioTagByte = 0;
	private int size;
	/**
	 * コンストラクタ
	 * @param targetFile 一時ファイル出力先
	 */
	public IndexFileCreator(File targetFile, IFileReadChannel source) throws Exception {
		this.targetFile = targetFile;
		idx = new FileOutputStream(targetFile);
		startTime = System.currentTimeMillis();
		lastFoundTime = startTime;
		// オブジェクトはコピーにしておく。
		this.source = FileReadChannel.openFileReadChannel(source.getUri());
	}
	public File getIdxFile() {
		return targetFile;
	}
	public int getDuration() {
		return duration;
	}
	public CodecType getVideoCodec() {
		return videoCodec;
	}
	public CodecType getAudioCodec() {
		return audioCodec;
	}
	public byte getAudioTagByte() {
		return audioTagByte;
	}
	public int getSize() {
		return size;
	}
	/**
	 * 初期セットアップ
	 */
	public void initSetup() throws Exception {
		size = source.size();
		// そこまで進んだという状態でいきたいので、先にデータを確認しておく。
		source.position(source.size() - 4);
		int lastTagSize = BufferUtil.safeRead(source, 4).getInt();
		int lastTagPos = source.size() - 4 - lastTagSize;
		source.position(lastTagPos);
		TagPositionAnalyzer analyzer = new TagPositionAnalyzer();
		Tag tag = analyzer.analyze(source);
		duration = tag.getTimestamp(); // 最終タグの時刻位置を最終サイズとします。
		source.position(0);
		// 内部データを解析していく。(ただし、5つのtagのみにしておく。(それ以外のデータは別スレッドで解析していけばいいと思う。))
		FlvHeader header = new FlvHeader();
		header.analyze(source);
		videoCodec = CodecType.NONE;
		audioCodec = CodecType.NONE;
//		boolean noVideoFlg = header.hasVideo(); // flvHeader的にvideoFlgがなければ、始めからaudioTagでシークできるようにしておきたいところ。
		// tagが5つみつかったら終わりとする。
		int foundedCount = 0;
		while(foundedCount < 5) {
			tag = analyzer.analyze(source);
			if(tag == null) {
				break;
			}
			foundedCount ++;
			if(tag instanceof AudioTag) {
				// msh判定のみほしい
				AudioTag aTag = (AudioTag) tag;
				audioTagByte = aTag.getTagByte();
				audioCodec = aTag.getCodec();
				if(aTag.isMediaSequenceHeader()) {
					logger.info("msh発見:");
					logger.info(tag.getPosition());
					long now = System.currentTimeMillis();
					logger.info((now - lastFoundTime));
					logger.info((now - startTime));

					ByteBuffer buffer = ByteBuffer.allocate(9);
					buffer.put((byte)0x02);
					buffer.putInt(aTag.getPosition());
					buffer.putInt(aTag.getTimestamp());
					buffer.flip();
					idx.getChannel().write(buffer);
					lastFoundTime = now;
				}
			}
			else if(tag instanceof VideoTag) {
				VideoTag vTag = (VideoTag) tag;
				videoCodec = vTag.getCodec();
				if(vTag.isKeyFrame()) {
					logger.info("keyFrame発見:");
					ByteBuffer buffer = ByteBuffer.allocate(9);
					if(vTag.isMediaSequenceHeader()) {
						logger.info("msh発見:");
						buffer.put((byte)0x01);
					}
					else {
						buffer.put((byte)0x00);
					}
					buffer.putInt(vTag.getPosition());
					buffer.putInt(vTag.getTimestamp());
					buffer.flip();
					idx.getChannel().write(buffer);

					logger.info(tag.getPosition());
					long now = System.currentTimeMillis();
					logger.info((now - lastFoundTime));
					logger.info((now - startTime));
					lastFoundTime = now;
				}
			}
		}
	}
	/**
	 * 解析本家sourceの内容の解析の続きを請け負っておく。でいいはず
	 * @throws Exception
	 */
	public void analyze() throws Exception {
		// 内部データを解析していく。
		// 適当な位置からすすんでいった先の00 00 00になっているところをとりあえず探したい。
		// 00 00 00 XXもしくは 00 00 00 00 XXになっているところがメディアデータの母体と思われる。
		// tagを解析していって、動画のキーフレームの部分を抜き出したい。
		Tag tag = null;
		TagPositionAnalyzer analyzer = new TagPositionAnalyzer();
		while((tag = analyzer.analyze(source)) != null) {
			if(tag instanceof AudioTag) {
				// msh判定のみほしい
				AudioTag aTag = (AudioTag) tag;
				if(aTag.isMediaSequenceHeader()) {
					logger.info("msh発見:");
					logger.info(tag.getPosition());
					long now = System.currentTimeMillis();
					logger.info((now - lastFoundTime));
					logger.info((now - startTime));
					lastFoundTime = now;
				}
			}
			else if(tag instanceof VideoTag) {
				VideoTag vTag = (VideoTag) tag;
				if(vTag.isKeyFrame()) {
					logger.info("keyFrame発見:");
					if(vTag.isMediaSequenceHeader()) {
						logger.info("msh発見:");
					}
					logger.info(tag.getPosition());
					long now = System.currentTimeMillis();
					logger.info((now - lastFoundTime));
					logger.info((now - startTime));
					lastFoundTime = now;
				}
			}
		}
	}
	public void close() {
		if(source != null) {
			try {
				source.close();
			}
			catch (Exception e) {
			}
			source = null;
		}
		if(idx != null) {
			try {
				idx.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			idx = null;
		}
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("IndexFileCreator:");
		data.append("[duration:").append(duration).append("]");
		data.append("[videoCodec:").append(videoCodec).append("]");
		data.append("[audioCodec:").append(audioCodec).append("]");
		data.append("[size:").append(size).append("]");
		return data.toString();
	}
}
