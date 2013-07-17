package com.ttProject.media.h264.nal;

import com.ttProject.media.extra.Bit1;
import com.ttProject.media.extra.Bit2;
import com.ttProject.media.extra.Bit8;
import com.ttProject.media.h264.INalAnalyzer;
import com.ttProject.media.h264.Nal;
import com.ttProject.nio.channels.IReadChannel;

/**
 * SequenceParameterSet
 * profileとかlevelとか、その他の細かい設定とかがはいっているみたい。
 * @author taktod
 */
public class SequenceParameterSet extends Nal {
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
	public SequenceParameterSet() {
		super(0, 0);
	}
	@Override
	public void analyze(IReadChannel ch, INalAnalyzer analyzer)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}
