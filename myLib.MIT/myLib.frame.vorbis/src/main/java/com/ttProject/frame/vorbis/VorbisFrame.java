package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioFrame;

/**
 * vorbisのframe
 * @author taktod
 * vorbisのframeもspeexと同様
 * header部
 * コメント部
 * 情報部
 * データ部にわかれるっぽい。
 * 
 * で、データ部だけ、xuggleのframeとしてほしいところ。
 */
public abstract class VorbisFrame extends AudioFrame {
}
