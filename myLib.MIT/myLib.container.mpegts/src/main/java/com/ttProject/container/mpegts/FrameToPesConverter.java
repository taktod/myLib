package com.ttProject.container.mpegts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.ttProject.container.mpegts.field.PmtElementaryField;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.container.mpegts.type.Pmt;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.frame.h264.type.AccessUnitDelimiter;
import com.ttProject.frame.h264.type.Slice;
import com.ttProject.frame.h264.type.SliceIDR;
import com.ttProject.frame.h264.type.SupplementalEnhancementInformation;

/**
 * frameデータからPesを作成するコンバーター
 * @author taktod
 */
public class FrameToPesConverter {
	/** ロガー */
	private Logger logger = Logger.getLogger(FrameToPesConverter.class);
	/** いままでの中途処理データ保持 */
	private final Map<Integer, Pes> pesMap = new ConcurrentHashMap<Integer, Pes>();
	/** audioデータのpesごとの長さ設定 */
	private final float audioPesDuration = 0.3f;
	/**
	 * frameからpesを作成して応答します
	 * @param pid
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	public Pes getPeses(int pid, Pmt pmt, IFrame frame) throws Exception {
		logger.info("追加フレーム:" + frame);
		if(frame instanceof VideoFrame) {
			return getVideoPes(pid, pmt, (VideoFrame)frame);
		}
		else if(frame instanceof AudioFrame) {
			return getAudioPes(pid, pmt, (AudioFrame)frame);
		}
		throw new Exception("音声でも映像でもないフレームを検知しました。" + frame.toString());
	}
	/**
	 * pesを新たに作成します
	 * @param pid
	 * @param pmt
	 * @return
	 * @throws Exception
	 */
	private Pes makeNewPes(int pid, Pmt pmt) throws Exception {
		logger.info("pesを作成します:" + Integer.toHexString(pid));
		Pes pes = new Pes(pid, pmt.getPcrPid() == pid);
		for(PmtElementaryField peField : pmt.getFields()) {
			if(pid == peField.getPid()) {
				if(pes.getStreamId() != 0) {
					throw new Exception("streamIdが始めから設定されることはないはずです。");
				}
				else {
					pes.setStreamId(peField.getSuggestStreamId());
				}
				break;
			}
		}
		pesMap.put(pid, pes);
		return pes;
	}
	/**
	 * pesを取得します。
	 * @param pid
	 * @param pmt
	 * @return
	 * @throws Exception
	 */
	private Pes getPes(int pid, Pmt pmt) throws Exception {
		Pes pes = pesMap.get(pid);
		if(pes == null) {
			pes = makeNewPes(pid, pmt);
		}
		return pes;
	}
	/**
	 * 音声フレームについて処理します
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getAudioPes(int pid, Pmt pmt, AudioFrame frame) throws Exception {
		Pes pes = getPes(pid, pmt);
		pes.addFrame(frame);
		IAudioFrame audioFrame = (IAudioFrame)pes.getFrame();
		// 保持しているsample数からデータの長さを割り出します。
		if(1.0f * audioFrame.getSampleNum() / audioFrame.getSampleRate() > audioPesDuration) {
			// 新たなpesを作成して登録しておきます。
			makeNewPes(pid, pmt);
			return pes;
		}
		return null;
	}
	/**
	 * 映像フレームについて処理します
	 * @param pid
	 * @param pmt
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getVideoPes(int pid, Pmt pmt, VideoFrame frame) throws Exception {
		if(frame instanceof H264Frame) {
			return getH264Pes(pid, pmt, (H264Frame)frame);
		}
		return null;
	}
	/**
	 * h264のフレームについて処理します
	 * @param pid
	 * @param pmt
	 * @param frame
	 * @return
	 * @throws Exception
	 */
	private Pes getH264Pes(int pid, Pmt pmt, H264Frame frame) throws Exception {
		if(frame instanceof SupplementalEnhancementInformation) {
			// ヌルポの原因になるので、いらない
			return null;
		}
		Pes result = null;
		Pes pes = pesMap.get(pid);
		// 始めのデータがsliceIdrでない場合は処理しない
		if(pes == null && !(frame instanceof SliceIDR)) {
			return null;
		}
		// 前のデータがある場合
		if(pes != null) {
			if(frame instanceof SliceIDR) {
				SliceIDR sliceIDR = (SliceIDR)frame;
				if(sliceIDR.getFirstMbInSlice() == 0) {
					result = pes;
					pes = makeNewPes(pid, pmt);
					AccessUnitDelimiter aud = new AccessUnitDelimiter();
					aud.setPts(frame.getPts());
					aud.setTimebase(frame.getTimebase());
					aud.setDts(frame.getDts());
					pes.addFrame(aud);
					pes.addFrame(sliceIDR.getSps());
					pes.addFrame(sliceIDR.getPps());
				}
			}
			else if(frame instanceof Slice) {
				Slice slice = (Slice)frame;
				if(slice.getFirstMbInSlice() == 0) {
					result = pes;
					pes = makeNewPes(pid, pmt);
					AccessUnitDelimiter aud = new AccessUnitDelimiter();
					aud.setPts(frame.getPts());
					aud.setTimebase(frame.getTimebase());
					aud.setDts(frame.getDts());
					pes.addFrame(aud);
				}
			}
			else {
				logger.info("想定外のフレームを受け取りました:" + frame.toString());
				return null;
			}
		}
		else {
			if(frame instanceof SliceIDR) {
				pes = makeNewPes(pid, pmt);
				SliceIDR sliceIDR = (SliceIDR)frame;
				AccessUnitDelimiter aud = new AccessUnitDelimiter();
				aud.setPts(frame.getPts());
				aud.setTimebase(frame.getTimebase());
				aud.setDts(frame.getDts());
				pes.addFrame(aud);
				pes.addFrame(sliceIDR.getSps());
				pes.addFrame(sliceIDR.getPps());
			}
			else if(frame instanceof Slice) {
				pes = makeNewPes(pid, pmt);
				AccessUnitDelimiter aud = new AccessUnitDelimiter();
				aud.setPts(frame.getPts());
				aud.setTimebase(frame.getTimebase());
				aud.setDts(frame.getDts());
				pes.addFrame(aud);
			}
			else {
				logger.info("想定外のフレームを受け取りました2:" + frame.toString());
				return null;
			}
		}
		pes.addFrame(frame);
		return result;
	}
	public Map<Integer, Pes> getPesMap() {
		return pesMap;
	}
}
