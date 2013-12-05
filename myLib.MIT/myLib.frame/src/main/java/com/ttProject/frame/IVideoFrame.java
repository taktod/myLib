package com.ttProject.frame;

public interface IVideoFrame extends IMediaFrame {
	public long getDts(); // dtsはdecodeするときの時間
	public int getWidth();
	public int getHeight();
}
