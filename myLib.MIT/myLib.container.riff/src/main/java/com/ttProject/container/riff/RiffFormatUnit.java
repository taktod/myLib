package com.ttProject.container.riff;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAnalyzer;

/**
 * RiffFmtUnit
 * unit to hold format information.
 * @author taktod
 */
public abstract class RiffFormatUnit extends RiffSizeUnit {
	/**
	 * constructor
	 * @param type
	 */
	public RiffFormatUnit(Type type) {
		super(type);
	}
	/**
	 * ref the codecType
	 * @return
	 */
	public abstract CodecType getCodecType();
	/**
	 * ref frame analyzer.
	 * @return
	 */
	public abstract IAnalyzer getFrameAnalyzer();
	/**
	 * ref frame size.
	 * @return
	 */
	public abstract int getBlockSize();
}
