package com.ttProject.media.mp4.atom.stsd;

import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public interface IRecordAnalyzer extends IAtomAnalyzer {
	public Record analyze(IFileReadChannel ch) throws Exception;
}
