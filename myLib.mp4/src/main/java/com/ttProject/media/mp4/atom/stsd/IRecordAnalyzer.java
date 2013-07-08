package com.ttProject.media.mp4.atom.stsd;

import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public interface IRecordAnalyzer extends IAtomAnalyzer {
	public Record analyze(IReadChannel ch) throws Exception;
}
