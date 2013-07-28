package com.ttProject.media.h264.frame;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.h264.Frame;

/**
 * SequenceParameterSet
 * profileとかlevelとか、その他の細かい設定とかがはいっているみたい。
 * @author taktod
 */
public class SequenceParameterSet extends Frame {
	// 先頭の３バイトからこのデータが取得可能
	private Bit8 profile;
	private Bit1 constraintSet0;
	private Bit1 constraintSet1;
	private Bit1 constraintSet2;
	private Bit1 constraintSet3;
	private Bit1 constraintSet4;
	private Bit1 constraintSet5;
	private Bit2 zero;
	private Bit8 level;
	// このあとにもデータはあるけど、flvのmediaSequenceHeaderでは特に考慮せずにつらねてるだけっぽい。
	// 実質はつかっているbframeとかの情報があるのだろうか？
	public SequenceParameterSet(int size, byte frameTypeData) {
		super(size, frameTypeData);
	}
	public SequenceParameterSet(byte frameTypeData) {
		this(0, frameTypeData);
	}
}
