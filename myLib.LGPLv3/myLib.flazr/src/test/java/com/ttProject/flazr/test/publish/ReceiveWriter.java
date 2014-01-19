package com.ttProject.flazr.test.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpWriter;
import com.ttProject.container.flv.AggregateTag;
import com.ttProject.container.flv.FlvTag;
import com.ttProject.flazr.unit.MessageManager;

/**
 * データをダウンロードするところ
 * @author taktod
 */
public class ReceiveWriter implements RtmpWriter {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(ReceiveWriter.class);
	private MessageManager messageManager = new MessageManager();
	// rtmp配信側のプログラム
	private SendReader sendReader = new SendReader("rtmp://localhost:1935/live");
	
	@Override
	public void close() {
		// おわった場合の処理
	}
	/**
	 * 
	 */
	public void publishStart() {
		try {
			sendReader.open();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 */
	public void publishStop() {
		
	}
	/**
	 * 
	 */
	public void playStart() {
		
	}
	// messageをpublishしなおした場合は、rtmpサーバーから送られてくるデータのtimestampが0からにならないっぽいので、調整が必要。
	// どうやら続きになるらしい。
	@Override
	public void write(RtmpMessage message) {
		// rtmpサーバーからメッセージをうけとった場合の処理
		try {
			FlvTag tag = messageManager.getTag(message);
			// このtagを別のサーバーにまわしたい。
			// publish中でない状態でデータをうけとったら、publishのプロセス準備が必要。
			// publish中にデータをうけとったら、相手サーバーに送ればOK
			if(tag instanceof AggregateTag) {
				AggregateTag aTag = (AggregateTag) tag;
				for(FlvTag flvTag : aTag.getList()) {
					sendReader.send(flvTag); // flvTagを渡しておく。
				}
			}
			else {
				sendReader.send(tag); // flvTagを渡しておく。
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0); // とりあえずDLに例外が発生したら、処理を中断しておく。
		}
	}
}
