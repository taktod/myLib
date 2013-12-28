package com.ttProject.container.ogg;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.ogg.type.Page;
import com.ttProject.container.ogg.type.StartPage;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * oggの書き込み動作
 * とりあえず、speexに対応するなら、startPageにspeexのコーデック情報(headerFrame)を、次のpageにメタデータ(commentFrame)をいれないとだめ。
 * 他のデータとまじってもいやなので、次のようにする必要がある。
 * 
 * @author taktod
 * 
 * こちらの動作としては、つぎつぎにframeデータをいれていくが、frameデータがいっぱいになったら次のoggPageに移動しないとだめ。
 * 現在のページにつぎつぎとデータをいれていく。
 */
public class OggPageWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggPageWriter.class);
	/**  */
	private Map<Integer, OggPage> pageMap = new HashMap<Integer, OggPage>();
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	private long addedSampleNum = 0;
	/**
	 * コンストラクタ
	 * @param fileName
	 * @throws Exception
	 */
	public OggPageWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public OggPageWriter(FileOutputStream fileOutputStream) {
		this.outputChannel = fileOutputStream.getChannel();
	}
	public OggPageWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContainer(IContainer container) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		// 利用不能なフレームは除外しておく
		if(!(frame instanceof SpeexFrame)) {
			return;
		}
		addedSampleNum += ((SpeexFrame)frame).getSampleNum();
		logger.info("フレーム追加:" + frame);
		OggPage targetPage = null;
		if(pageMap.get(trackId) == null) {
			targetPage = new StartPage(new Bit8(), new Bit5(),new Bit1(), new Bit1(1), new Bit1());
			targetPage.setStreamSerialNumber(trackId);
			pageMap.put(trackId, targetPage);
		}
		else {
			targetPage = pageMap.get(trackId);
		}
		logger.info("targetPage:" + targetPage);
		// pageにデータを設定します。
		targetPage.getFrameList().add(frame);
		if(targetPage.getFrameList().size() >= 255) {
			completePage(trackId);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareHeader() throws Exception {
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareTailer() throws Exception {
		// 残っているpageはすべて停止します
		for(Integer key : pageMap.keySet()) {
			logger.info("target:" + key);
			OggPage page = pageMap.get(key);
			// 残っているpageMapのendFlagを1にたてて止める必要がある。
			page.setLogicEndFlag(true);
			// このタイミングでoutputChannelを止めてしまう。
			outputChannel.write(page.getData());
		}
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {}
			outputStream = null;
		}
	}
	/**
	 * 現在扱っているページが完了したとする
	 */
	public void completePage(int trackId) throws Exception {
		logger.info("強制pageComplete");
		OggPage page = pageMap.get(trackId);
		logger.info(page);
		// このタイミングでgranulePositionを書き込まないとだめ
		// frameの合計位置を計算して加えないとだめ
		page.setAbsoluteGranulePosition(addedSampleNum);
		// このタイミングでoutputChannelに必要な情報を書き込む
		outputChannel.write(page.getData());
		int lastSequenceNo = page.getPageSequenceNo();
		// 次のpageMapを設定しておく。
		page = new Page(new Bit8(), new Bit5(), new Bit1(), new Bit1(), new Bit1());
		page.setStreamSerialNumber(trackId);
		page.setPageSequenceNo(lastSequenceNo + 1);
		pageMap.put(trackId, page);
	}
}
