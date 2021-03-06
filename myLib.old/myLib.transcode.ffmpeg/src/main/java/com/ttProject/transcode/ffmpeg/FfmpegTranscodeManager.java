/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.ffmpeg;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ttProject.media.Unit;
import com.ttProject.transcode.ITrackManager;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.ffmpeg.process.ProcessHandler;
import com.ttProject.transcode.ffmpeg.process.ProcessServer;
import com.ttProject.transcode.ffmpeg.track.FfmpegTrackManager;
import com.ttProject.transcode.ffmpeg.unit.IDeunitizer;
import com.ttProject.transcode.ffmpeg.unit.IUnitizer;

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
	/** unitをstreamに戻すときに利用するプログラム */
	private IDeunitizer deunitizer = null;
	/** streamをunitに戻すときに利用するプログラム */
	private IUnitizer unitizer = null;
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
				processServer = new ProcessServer(this, portNumber);
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
	@Override
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
	public void process(List<?> units) throws Exception {
		if(units == null) {
			return;
		}
		// このデータをすべてのTrackManagerに渡したい。
		for(Entry<Integer, ITrackManager> entry : getTrackManagers().entrySet()) {
			FfmpegTrackManager trackManager = (FfmpegTrackManager)entry.getValue();
			for(Object obj : units) {
				if(obj instanceof Unit) {
					trackManager.applyData((Unit)obj);
				}
			}
			trackManager.commit();
		}
	}
	/**
	 * 終了処理
	 */
	@Override
	public synchronized void close() {
		if(handler != null) {
			handler.close();
			handler = null;
		}
		if(server != null) {
			server.closeServer();
			server = null;
		}
		if(unitizer != null) {
			unitizer.close();
			unitizer = null;
		}
		if(deunitizer != null) {
			deunitizer.close();
			deunitizer = null;
		}
		for(Entry<Integer, ITrackManager> entry : getTrackManagers().entrySet()) {
			FfmpegTrackManager trackManager = (FfmpegTrackManager)entry.getValue();
			trackManager.close();
		}
		getTrackManagers().clear();
	}
	/**
	 * 内部動作用のtrackをつくる必要がある
	 */
	@Override
	protected ITrackManager makeTrackManager(int newId) {
		FfmpegTrackManager trackManager = new FfmpegTrackManager(newId);
		return trackManager;
	}
	@Override
	public void setDeunitizer(IDeunitizer deunitizer) {
		this.deunitizer = deunitizer;
	}
	@Override
	public void setUnitizer(IUnitizer unitizer) {
		this.unitizer = unitizer;
	}
	public IDeunitizer getDeunitizer() {
		return deunitizer;
	}
	public IUnitizer getUnitizer() {
		return unitizer;
	}
	// wait中はデータがこなくなったらサーバーを殺す・・・がよさそう。
	public void waitForEnd() throws Exception {
		System.out.println("transcodeManager");
		if(handler != null) {
			handler.waitForEnd();
		}
	}
}
