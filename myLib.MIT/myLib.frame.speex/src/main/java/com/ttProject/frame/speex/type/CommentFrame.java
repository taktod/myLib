package com.ttProject.frame.speex.type;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * speexのCommentFrame(metaデータみたいなものかな)
 * 
 * 4byte int venderLength
 * nbyte string venderName
 * 4byte int elementNum
 *  4byte elementLength
 *  nbyte elementString
 *  をelementNum分繰り返す
 * @author taktod
 */
public class CommentFrame extends SpeexFrame {
	/** ロガー */
	private Logger logger = Logger.getLogger(CommentFrame.class);
	private String venderName;
	private List<String> elementList = new ArrayList<String>();
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		logger.info("minimumLoad");
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		logger.info("load");
		// ここでデータを読み込んで処理する。
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
