package com.ttProject.transcode.ffmpeg;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackManager;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.ffmpeg.filestream.IDeunitizer;
import com.ttProject.transcode.ffmpeg.filestream.IUnitizer;
import com.ttProject.transcode.ffmpeg.process.ProcessHandler;
import com.ttProject.transcode.ffmpeg.process.ProcessServer;
import com.ttProject.transcode.ffmpeg.track.FfmpegTrackManager;

/**
 * ffmpeg経由で変換を実行するマネージャー
 * こっちの方がどうみてもxuggleよりパフォーマンスがよさそう。
 * 
 * やること
 * ・変換コマンドを登録する。
 * ・stream化プログラム設定
 * ・unit化プログラムを設定
 * ・実行
 * ・あとしまつ
 * これが動作の流れ的なもの
 * @author taktod
 */
public class FfmpegTranscodeManager extends TranscodeManager implements IFfmpegTranscodeManager {
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(FfmpegTranscodeManager.class);
	/** 動作プロセス */
	private ProcessHandler handler = null;
	/** 動作pid */
	private static String pid;
	/** 動作ポート番号 */
	private int portNumber;
	/** 動作サーバー */
	private ProcessServer server = null;
	// 出力用のデータ変換が複数必要か？トラックごとにつくっておいた方がよさそう。
	/** streamデータからそれぞれのstreamのデータを抜き出す処理(映像 + 音声なら２つ、映像 + 映像 + 音声なら３ついる) */
//	private Set<IStreamToUnitHandler> unitHandlers;
	/** streamをunitに戻すmanager */
	/**
	 * 静的初期化
	 */
	static {
		// 実行プロセスのpidを取得
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		pid = bean.getName().split("@")[0];
	}
	/**
	 * コンストラクタ
	 */
	public FfmpegTranscodeManager() {
		ProcessServer processServer = null;
		int portNumber = Integer.parseInt(pid);
		if(portNumber < 1000) {
			portNumber += 1000;
		}
		for(;portNumber < 65535;portNumber += 1000) {
			try {
				processServer = new ProcessServer(portNumber);
				break;
			}
			catch(Exception e) {}
		}
		if(portNumber > 65535) {
			logger.fatal("プロセス番号ベースでローカルサーバー用のポート番号が決定しませんでした。");
			throw new RuntimeException("ローカルサーバーのポート番号が決定しませんでした。");
		}
		logger.info("ポート番号がきまりました。:" + portNumber);
		server = processServer;
		this.portNumber = portNumber;
	}
	/**
	 * 変換コマンドを設置する
	 * @param command
	 */
	public void registerCommand(String command) throws Exception {
		if(handler != null) {
			throw new Exception("すでにhandlerは定義済みです。");
		}
		handler = new ProcessHandler(this, portNumber);
		handler.setCommand(command);
		server.addKey(handler.getKey());
	}
	/**
	 * 変換処理実行
	 */
	@Override
	public void transcode(Unit unit) throws Exception {
		// processがなかったら作る必要あり。
		if(!handler.isRunning()) {
			logger.info("プロセスが開始していないので、開始します。");
			// 起動していなかったら起動する。
			handler.executeProcess();
		}
		server.getSendWorker().send(unit);
	}
	public void process(List<Unit> units) throws Exception {
		// このデータをすべてのTrackManagerに渡したい。
		for(Entry<Integer, ITrackManager> entry : getTrackManagers().entrySet()) {
			FfmpegTrackManager trackManager = (FfmpegTrackManager)entry.getValue();
			for(Unit unit : units) {
				trackManager.applyData(unit);
			}
			trackManager.commit();
		}
	}
	/**
	 * 終了処理
	 */
	@Override
	public void close() {
		if(handler != null) {
			handler.close();
			handler = null;
		}
		if(server != null) {
			server.closeServer();
			server = null;
		}
	}
	@Override
	protected ITrackManager makeTrackManager(int newId) {
		FfmpegTrackManager trackManager = new FfmpegTrackManager(newId);
		return trackManager;
	}
	@Override
	public void setDeunitizer(IDeunitizer deunitizer) {
		server.getSendWorker().setDeunitizer(deunitizer);
	}
	@Override
	public void setUnitizer(IUnitizer unitizer) {
		this.handler.getReceiveWorker().setUnitizer(unitizer);
	}
}
