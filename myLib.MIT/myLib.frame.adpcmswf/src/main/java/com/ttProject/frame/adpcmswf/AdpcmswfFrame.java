/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;

/**
 * adpcmswfの動作ですが、適当なデータがなかったので、いろんなフォーマットをつくってテストしてみました。
 * 1:44100 22050 11025の３つフォーマットしかつくれませんでした。5512はつくれなかった。
 * 2:monoral stereoともに作成可能でした。
 * 3:16bitのみ作成可能でした。(8bitは指定は通るのですが、動作しませんでした。)
 * 4:データはこうなっているみたいです。
 * ?? ll ll rr rr 差分データ　stereoの場合
 * ?? ss ss 差分データ monoralの場合
 * となっているらしい。
 * ??の部分は0x80固定かとおもったけど、そうではないみたいです
 * 差分データの量 x (2:monoral 1:stereo)がsample数みたいです。
 * channel sampleRate bitCountはflvのデータから取得可能なので、データ量からsampleNumが取り出せたらそれでいいと思う。
 * 
 * githubにあがっているソースから調べてみた。
 * @see https://github.com/alexgirao/adpcm_swf/blob/50b5c9b57a989220b8a2d1729c6ddb9c1d463c79/adpcm_swf2raw.c
 * 308行目あたりにADPCMSoundDataという部分があるみたいです。
 * 2bit adpcm_code_size 1 -> 3bit 2 -> 4bit 3 -> 5bit 4 -> 6bit
 * 16bit initial_samples
 * 6bit initial_index
 * あとは単なるデータらしいけど、4095sampleもないけど・・・
 * 作成データをみてみると一応あたっているみたいです。
 * たとえば44100hzモノラルのデータは1tag0x0404byteありました。
 * 初めの1byte (codec(4bit)sampleRate(2bit) bit(1bit) channel(1bit))
 * 2bit adpcm_code_size
 * 16bit initialSample
 * 6bit initial_index
 * 2048sampleあるので2047あればよい。
 * 2047 * 4 = 8188bits
 * 合計が24 + 8188 = 8212bits -> (1026 + 4/8)bytes
 * 0x403 + 1(codecとかの部分) = 0x404
 * 
 * 各tagの終端をみてみたら、下位4bitが0になっているので、正解かも
 * 
 * 44100hzステレオのデータは1tag 0x0806byteありました
 * 同様に1byte(codecその他)
 * 2bit adpcm_code_size
 * 16bit initSample
 * 6bit initialIndex
 * 16bit initSample
 * 6bit initialIndex
 * 2047sample
 * 2047 * 4 * 2 = 16376 bits
 * 前半:46bit + 16376 = 16422 bits = (2052 + 6/8)byte
 * 0x805 + 1(codecとか) = 0x806
 * 
 * こちらも終端をみれば0 8 Cになっていて下位2bitが0でうまっている
 * というわけで上記のプログラムとhexの状態をみてみたところ
 * 
 * 4bit:codecId
 * 2bit:sampleRateId:
 * 1bit:bitDepth
 * 1bit:channels(ここまでaudioTag共通)
 * 
 * 2bit adpcmCodeSize 0:2bit 1:3bit 2:4bit 3:5bit
 * 16bit initSample
 * 6bit initialIndex
 * 
 * 16bit initSample(ステレオの場合)
 * 6bit initialIndex(ステレオの場合)
 * 
 * ((sample数 - 1) * (adpcmCodeSize + 2) * (channel数))bit adpcm差分データ
 * byteに満たない場合は0でうめる
 * 
 * という形になっているっぽい。
 * ただしあくまでそうなっているであろう・・・のレベルだけどね。
 * @author taktod
 */
public abstract class AdpcmswfFrame extends AudioFrame {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.ADPCM_SWF;
	}
}
