package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioFrame;

/**
 * adpcmswfの動作ですが、適当なデータがなかったので、いろんなフォーマットをつくってテストしてみました。
 * 1:44100 22050 11025の３つフォーマットしかつくれませんでした。5512はつくれなかった。
 * 2:monoral stereoともに作成可能でした。
 * 3:16bitのみ作成可能でした。(8bitは指定は通るのですが、動作しませんでした。)
 * 4:データはこうなっているみたいです。
 * 0x80 ll ll rr rr 差分データ　stereoの場合
 * 0x80 ss ss 差分データ monoralの場合
 * となっているらしい。
 * 差分データの量 x (2:monoral 1:stereo)がsample数みたいです。
 * channel sampleRate bitCountはflvのデータから取得可能なので、データ量からsampleNumが取り出せたらそれでいいと思う。
 * @author taktod
 */
public abstract class AdpcmswfFrame extends AudioFrame {
}
