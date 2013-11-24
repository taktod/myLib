package com.ttProject.transcode.ffmpeg;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.ByteBuffer;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.ttProject.media.Unit;
import com.ttProject.transcode.TranscodeManager;
import com.ttProject.transcode.ffmpeg.filestream.IStreamToUnitHandler;
import com.ttProject.transcode.ffmpeg.filestream.IUnitToStreamHandler;
import com.ttProject.transcode.ffmpeg.process.ProcessServer;

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
public class FfmpegTranscodeManager extends TranscodeManager{
	/** 動作ロガー */
	private final Logger logger = Logger.getLogger(FfmpegTranscodeManager.class);
	/** 動作プロセス */
	private ProcessHandler handler;
	/** 動作pid */
	private static String pid;
	/** 動作ポート番号 */
	private int portNumber;
	/** 動作サーバー */
	private ProcessServer server;
	/** unitデータをffmpegに流すstreamに変換する処置 */
	private IUnitToStreamHandler unitToStreamHandler;
	// 出力用のデータ変換が複数必要か？トラックごとにつくっておいた方がよさそう。
	/** streamデータからそれぞれのstreamのデータを抜き出す処理(映像 + 音声なら２つ、映像 + 映像 + 音声なら３ついる) */
	private Set<IStreamToUnitHandler> streamToUnitHandlers;
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
		handler = new ProcessHandler(portNumber);
		handler.setCommand(command);
		server.addKey(handler.getKey());
	}
	/**
	 * unitデータをstreamに変換するプログラムを設置する
	 * @param handler
	 */
	public void setUnitToStreamHandler(IUnitToStreamHandler handler) {
		unitToStreamHandler = handler;
	}
	/**
	 * streamデータをunitに戻すプログラムを書いておく
	 * @param handler
	 */
	public void addStreamToUnitHandler(IStreamToUnitHandler handler) {
		// 登録しておく
		streamToUnitHandlers.add(handler);
	}
	/**
	 * 変換処理実行
	 */
	@Override
	public void transcode(Unit unit) throws Exception {
		// 自分が処理するものでなければ、処理やめ
		if(!unitToStreamHandler.check(unit)) {
			return;
		}
		// ffmpegに送るべきデータを取得
		ByteBuffer buffer = unitToStreamHandler.getBuffer(unit);
		// channelBufferに変換する。
		ChannelBuffer cBuffer = ChannelBuffers.wrappedBuffer(buffer);
		// 送る
		server.sendData(cBuffer);
	}
	/**
	 * 終了処理
	 */
	@Override
	public void close() {
		
	}
}
