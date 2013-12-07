package com.ttProject.frame;

public interface IVideoFrame extends IFrame {
	public long getDts(); // dtsはdecodeするときの時間
	public int getWidth();
	public int getHeight();
}
