/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.MkvTagWriter;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.EBMLMaxIDLength;
import com.ttProject.container.mkv.type.EBMLMaxSizeLength;
import com.ttProject.container.mkv.type.EBMLReadVersion;
import com.ttProject.container.mkv.type.EBMLVersion;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * mkvによるデータの書き込み動作テスト
 * @author taktod
 */
public class MkvWriteTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvWriteTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("書き込みテスト開始");
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.h264aac.mkv")
		);
		IReader reader = new MkvTagReader();
		IWriter writer = new MkvTagWriter("output.mkv");
		writer.prepareHeader();
		// mpegtsとかでもあらかじめ書き込みを実行することで対処しているので、mkvもそうすればいいかな・・・
		// ある程度どうなるかわかっているとだいぶ助かるけど・・・
		// frameが着てから解析するというのはやらないでおこうというのがいいのか？
		// ただしmpegtsと違ってサイズやサンプルレートといった情報も必要になるので、そのあたりもきちんと調整しておいた方がよさそうですね。
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof TrackEntry) {
				logger.info(container);
			}
/*			if(container instanceof MkvBlockTag) {
				MkvBlockTag blockTag = (MkvBlockTag) container;
				IFrame frame = blockTag.getFrame();
				blockTag.getTrackId().get();
				logger.info(frame);
				writer.addFrame(blockTag.getTrackId().get(), frame);
			}*/
		}
		writer.prepareTailer();
		logger.info("処理おわり");
	}
/*	private void setupHeader(MkvTagWriter writer) throws Exception {
		EBML ebml = new EBML();

		EBMLVersion ebmlVersion = new EBMLVersion();
		ebmlVersion.setValue(1);
		ebml.addChild(ebmlVersion);

		EBMLReadVersion ebmlReadVersion = new EBMLReadVersion();
		ebmlReadVersion.setValue(1);
		ebml.addChild(ebmlReadVersion);

		EBMLMaxIDLength ebmlMaxIdLength = new EBMLMaxIDLength();
		ebmlMaxIdLength.setValue(4);
		ebml.addChild(ebmlMaxIdLength);

		EBMLMaxSizeLength ebmlMaxSizeLength = new EBMLMaxSizeLength();
		ebmlMaxSizeLength.setValue(8);
		ebml.addChild(ebmlMaxSizeLength);

		DocType docType = new DocType();
		docType.setValue("matroska");
		ebml.addChild(docType);

		DocTypeVersion docTypeVersion = new DocTypeVersion();
		docTypeVersion.setValue(2);
		ebml.addChild(docTypeVersion);

		DocTypeReadVersion docTypeReadVersion = new DocTypeReadVersion();
		docTypeReadVersion.setValue(2);
		ebml.addChild(docTypeReadVersion);

		// ebmlのタグを書き込んでおく
		writer.addContainer(ebml);
		
		// segmentの書き込みを実施する。(ライブだったらsegmentのデータはFFFFFFFFにしておく必要がある。)
		// 基本的にFFFFFFFFにして動作させて、tailerで上書きするってのが最良かな？0x01FFFFFFFFFFFFFFにしているみたい。
		// ffmpegでは、書き込みが増えるたびに更新しているみたいですね。CUEの位置情報も含めて毎度書き直しているみたいですね。
		// これにより中途で止めてもきちんと動作するmkvとして出力される形になるみたいです。
		// うーん。
		// pipe出力にすると、01FFFFFFFFFFFFFFFFとして動作するみたいですね。
		// cueも入力されないみたいです。
		// とりあえずstreamingと同じことができるようにしたいので、次のようにします。
		/*
		 * segmentはinfinity扱いで動作させる。
		 * seekについては、Info、Tracks、Tagsの情報 (通常の動画ならCuesもいれたいが、liveStreamでははいらないっぽい)
		 * がはいっていればよさそう。
		 * Cuesはシークしないので、とりいそぎなくてもいい
		 * Infoについて
		 *   TimecodeScale1000000(ナノ秒していだったか？)これで1ミリ秒刻みになるはず
		 *   MuxingApp情報
		 *   WritingApp情報
		 *   SegmentUID(とりあえずランダム数値をいれておきたいところ。)
		 *   Durationなしにしておきたいところ・・・どうなんだろう。
		 * このあたりの必要な情報はMkvTagWriterにカキコ済みっぽいですね。
		 * /
		Segment segment = new Segment();
		segment.setInfinite(true); // 値的に無限化して書き込んでおく
	} // */
}
