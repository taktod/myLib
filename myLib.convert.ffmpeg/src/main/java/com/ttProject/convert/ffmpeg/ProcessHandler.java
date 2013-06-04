package com.ttProject.convert.ffmpeg;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ttProject.convert.IConvertListener;

public class ProcessHandler {
	private final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);
	private final String key;
	private final int port;
	private String processCommand = null;
	private final Set<IConvertListener> listeners = new HashSet<IConvertListener>();
	private Map<String, String> envExtra = null;
	/**
	 * コンストラクタ
	 * @param port
	 */
	protected ProcessHandler(int port) {
		this.port = port;
		this.key = UUID.randomUUID().toString();
	}
	protected String getKey() {
		return key;
	}
	/**
	 * 動作コマンドを設定する
	 * @param command
	 */
	public void setCommand(String command) {
		this.processCommand = command;
	}
	public void setEnvExtra(Map<String, String> envExtra) {
		this.envExtra = envExtra;
	}
	public void addListener(IConvertListener listener) {
		listeners.add(listener);
	}
	public void removeListener(IConvertListener listener) {
		listeners.remove(listener);
	}
	/**
	 * プロセスの実行
	 */
	protected void executeProcess() throws Exception {
		if(processCommand == null) {
			throw new Exception("process用のコマンドが設定されていません。");
		}
		// たぶん、動作用のclasspathはjava.class.pathのデータをみればいいと思う。
		StringBuilder command = new StringBuilder();
		command.append("java -Dfile.encoding=UTF-8 -cp").append(" ");
		command.append(System.getProperty("java.class.path")).append(" ");
		command.append("com.ttProject.convert.ffmpeg.process.ProcessEntry").append(" ");
		command.append(port).append(" ");
		command.append(key).append(" ");
		command.append("2>/dev/null");
		command.append(" | ");
		command.append(processCommand);
		System.out.println("プロセスを起動します。");
		System.out.println(command);
		ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		if(envExtra != null) {
			Map<String, String> env = processBuilder.environment();
			for(String key : envExtra.keySet()) {
				String envData = env.get(key);
				if(env == null || "".equals(envData)) {
					envData = envExtra.get(key);
				}
				else {
					envData += ":" + envExtra.get(key);
				}
				env.put(key, envData);
			}
		}
//		パスの変更が必要だったりする場合はここでenvをいじっておく必要あり。
		Process process = processBuilder.start();
		final ReadableByteChannel outputChannel = Channels.newChannel(process.getInputStream());
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						ByteBuffer buffer = ByteBuffer.allocate(65536);
						outputChannel.read(buffer);
						buffer.flip();
						// うけとったデータは対象のIConvertListenerに渡す。
						for(IConvertListener listener : listeners) {
							listener.receiveData(buffer);
						}
					}
				}
				catch (Exception e) {
					logger.error("プロセスからのデータ応答取得エラー", e);
				}
			}
		});
		// 応答読み取りスレッド
		thread.start();
	}
}
