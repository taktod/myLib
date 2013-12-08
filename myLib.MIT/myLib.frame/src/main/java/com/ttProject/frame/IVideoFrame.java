package com.ttProject.frame;

/**
 * 映像フレームのインターフェイス
 * @author taktod
 */
public interface IVideoFrame extends IFrame {
	/**
	 * dts値
	 * @return
	 */
	public long getDts(); // dtsはdecodeするときの時間
	/**
	 * width
	 * @return
	 */
	public int getWidth();
	/**
	 * height
	 * @return
	 */
	public int getHeight();
}
