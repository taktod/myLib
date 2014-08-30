/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.io.FileOutputStream;
import java.nio.channels.WritableByteChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.EBMLMaxIDLength;
import com.ttProject.container.mkv.type.EBMLMaxSizeLength;
import com.ttProject.container.mkv.type.EBMLReadVersion;
import com.ttProject.container.mkv.type.EBMLVersion;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;

/**
 * mkvを作成するためのwriter
 * @author taktod
 */
public class MkvTagWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvTagWriter.class);
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	// 最少の場合はMuxer名だけ追加入力してもらって、あとは自動入力でなんとかした方がよさそう。
	// frameを入力する前に送ったmkvTagがある場合は、そっちを使うようにする。(なるべく)
	// 実際の書き込みはframeうけとってから実行みたいな感じがいいとおもう。
	/*
	 * とりあえずこれをつくろうとおもったら、各MkvTagの動作の書き込み動作をまずつくる必要がありそうだ。
	 * テストコードで読み取ったデータをそのまま書き込むみたいな動作がほしいところ。
	 * 
	 * ebmlは適当に書く
	 * Segmentの内容、tracksまでは１つのファイルに書き出しておく。
	 * tracks指定がなかったらclusterの１つ目をつくりつつデータを構築する。
	 * １つ目のclusterが処理おわったら、tracksは決定したものとする。
	 * Clusterの内容は１つずつどこかに書き出しておく
	 * Cueデータをつくっておく。(Clusterの並びしだい)
	 * 最後にデータを結合して、おわり。
	 */
	/*
	 * 必要であろう、エレメントリスト
06:25:14,113 [main] INFO [MkvTagReader] - EBML size:23*
06:25:14,111 [main] INFO [MkvTagReader] -  EBMLVersion size:1 uint:1
06:25:14,112 [main] INFO [MkvTagReader] -  EBMLReadVersion size:1 uint:1
06:25:14,112 [main] INFO [MkvTagReader] -  EBMLMaxIDLength size:1 uint:4
06:25:14,112 [main] INFO [MkvTagReader] -  EBMLMaxSizeLength size:1 uint:8
06:25:14,113 [main] INFO [MkvTagReader] -  DocType size:8 string:matroska : webmだったら別の名前になる。
06:25:14,113 [main] INFO [MkvTagReader] -  DocTypeVersion size:1 uint:2
06:25:14,113 [main] INFO [MkvTagReader] -  DocTypeReadVersion size:1 uint:2
06:25:14,114 [main] INFO [MkvTagReader] - Segment size:5a1f*
06:25:14,117 [main] INFO [MkvTagReader] -  SeekHead size:3b*
06:25:14,115 [main] INFO [MkvTagReader] -   Seek size:b*
06:25:14,115 [main] INFO [MkvTagReader] -    SeekID size:4 binary:4 // 対象のmkvのtag名みたい
06:25:14,115 [main] INFO [MkvTagReader] -    SeekPosition size:1 uint:223 // mkvのtagのsizeを読み終わった場所からの相対位置っぽい。
06:25:14,116 [main] INFO [MkvTagReader] -   Seek size:c*
06:25:14,115 [main] INFO [MkvTagReader] -    SeekID size:4 binary:4
06:25:14,116 [main] INFO [MkvTagReader] -    SeekPosition size:2 uint:302
06:25:14,117 [main] INFO [MkvTagReader] -   Seek size:c*
06:25:14,116 [main] INFO [MkvTagReader] -    SeekID size:4 binary:4
06:25:14,116 [main] INFO [MkvTagReader] -    SeekPosition size:2 uint:489
06:25:14,117 [main] INFO [MkvTagReader] -   Seek size:c*
06:25:14,117 [main] INFO [MkvTagReader] -    SeekID size:4 binary:4
06:25:14,117 [main] INFO [MkvTagReader] -    SeekPosition size:2 uint:22957
06:25:14,118 [main] INFO [MkvTagReader] -  (Void size:95)
06:25:14,121 [main] INFO [MkvTagReader] -  Info size:43*
06:25:14,119 [main] INFO [MkvTagReader] -   TimecodeScale size:3 uint:1000000
06:25:14,119 [main] INFO [MkvTagReader] -   MuxingApp size:c string:Lavf54.2.100
06:25:14,119 [main] INFO [MkvTagReader] -   WritingApp size:c string:Lavf54.2.100
06:25:14,119 [main] INFO [MkvTagReader] -   SegmentUID size:10 binary:10 (ランダムでOKっぽい)
06:25:14,121 [main] INFO [MkvTagReader] -   Duration size:8 float:913.0 (ライブならスキップでOKか？それともFFFFFFFFFFFFにすべき？)
06:25:14,152 [main] INFO [MkvTagReader] -  Tracks size:af*
06:25:14,126 [main] INFO [MkvTagReader] -   TrackEntry size:64*
06:25:14,123 [main] INFO [MkvTagReader] -    TrackNumber size:1 uint:1(この数値はSimpleBlockと一致させる必要あり)
06:25:14,123 [main] INFO [MkvTagReader] -    TrackUID size:1 uint:1 (ランダムでOKっぽい)
06:25:14,123 [main] INFO [MkvTagReader] -    FlagLacing size:1 uint:0 (いらないだろ)(デフォでONなのでOFF強制するなら必要)
06:25:14,123 [main] INFO [MkvTagReader] -    Language size:3 string:und
06:25:14,124 [main] INFO [MkvTagReader] -    CodecID size:f string:V_MPEG4/ISO/AVC
06:25:14,124 [main] INFO [MkvTagReader] -    TrackType size:1 uint:1
06:25:14,124 [main] INFO [MkvTagReader] -    DefaultDuration size:3 uint:1000000 fpsを知るために必要っぽい
06:25:14,125 [main] INFO [MkvTagReader] -    Video size:7*
06:25:14,125 [main] INFO [MkvTagReader] -     PixelWidth size:2 uint:320
06:25:14,125 [main] INFO [MkvTagReader] -     PixelHeight size:1 uint:240
06:25:14,125 [main] INFO [MkvTagReader] -    CodecPrivate size:25 binary:25
06:25:14,147 [main] INFO [MkvTagReader] -   TrackEntry size:39*
06:25:14,145 [main] INFO [MkvTagReader] -    TrackNumber size:1 uint:2
06:25:14,145 [main] INFO [MkvTagReader] -    TrackUID size:1 uint:2
06:25:14,145 [main] INFO [MkvTagReader] -    FlagLacing size:1 uint:0
06:25:14,145 [main] INFO [MkvTagReader] -    Language size:3 string:und
06:25:14,146 [main] INFO [MkvTagReader] -    CodecID size:9 string:A_MPEG/L3
06:25:14,146 [main] INFO [MkvTagReader] -    TrackType size:1 uint:2
06:25:14,147 [main] INFO [MkvTagReader] -    Audio size:11*
06:25:14,146 [main] INFO [MkvTagReader] -     Channels size:1 uint:2
06:25:14,147 [main] INFO [MkvTagReader] -     SamplingFrequency size:8 float:44100.0
06:25:14,147 [main] INFO [MkvTagReader] -     BitDepth size:1 uint:32

06:25:14,154 [main] INFO [MkvTagReader] -  Tags size:37*
06:25:14,154 [main] INFO [MkvTagReader] -   Tag size:2d*
06:25:14,153 [main] INFO [MkvTagReader] -    Targets size:0*
06:25:14,154 [main] INFO [MkvTagReader] -    SimpleTag size:19*
06:25:14,154 [main] INFO [MkvTagReader] -     TagName size:7 string:ENCODER
06:25:14,154 [main] INFO [MkvTagReader] -     TagString size:c string:Lavf54.2.100

06:25:14,155 [main] INFO [MkvTagReader] - Cluster size:177f*
06:25:14,155 [main] INFO [MkvTagReader] - Timecode size:1 uint:0
06:25:14,156 [main] INFO [MkvTagReader] - SimpleBlock size:4b4 binary:4b0 trackId:1 timeDiff:0
06:25:14,157 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:0
06:25:14,157 [main] INFO [MkvTagReader] - SimpleBlock size:c9 binary:c5 trackId:1 timeDiff:25
06:25:14,157 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:25
06:25:14,158 [main] INFO [MkvTagReader] - SimpleBlock size:111 binary:10d trackId:1 timeDiff:50
06:25:14,158 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:51
06:25:14,158 [main] INFO [MkvTagReader] - SimpleBlock size:e6 binary:e2 trackId:1 timeDiff:75
06:25:14,159 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:77
06:25:14,159 [main] INFO [MkvTagReader] - SimpleBlock size:b2 binary:ae trackId:1 timeDiff:100
06:25:14,159 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:103
06:25:14,160 [main] INFO [MkvTagReader] - SimpleBlock size:249 binary:245 trackId:1 timeDiff:125
06:25:14,160 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:129
06:25:14,160 [main] INFO [MkvTagReader] - SimpleBlock size:f5 binary:f1 trackId:1 timeDiff:150
06:25:14,161 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:156
06:25:14,161 [main] INFO [MkvTagReader] - SimpleBlock size:fa binary:f6 trackId:1 timeDiff:175
06:25:14,161 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:182
06:25:14,162 [main] INFO [MkvTagReader] - SimpleBlock size:ff binary:fb trackId:1 timeDiff:200
06:25:14,162 [main] INFO [MkvTagReader] - Cluster size:1970*
06:25:14,162 [main] INFO [MkvTagReader] - Timecode size:1 uint:208
06:25:14,163 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:0
06:25:14,163 [main] INFO [MkvTagReader] - SimpleBlock size:23a binary:236 trackId:1 timeDiff:17
06:25:14,164 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:26
06:25:14,164 [main] INFO [MkvTagReader] - SimpleBlock size:11b binary:117 trackId:1 timeDiff:42
06:25:14,164 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:52
06:25:14,165 [main] INFO [MkvTagReader] - SimpleBlock size:e8 binary:e4 trackId:1 timeDiff:67
06:25:14,165 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:78
06:25:14,165 [main] INFO [MkvTagReader] - SimpleBlock size:101 binary:fd trackId:1 timeDiff:92
06:25:14,166 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:104
06:25:14,166 [main] INFO [MkvTagReader] - SimpleBlock size:a4 binary:a0 trackId:1 timeDiff:117
06:25:14,167 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:130
06:25:14,167 [main] INFO [MkvTagReader] - SimpleBlock size:229 binary:225 trackId:1 timeDiff:142
06:25:14,167 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:157
06:25:14,168 [main] INFO [MkvTagReader] - SimpleBlock size:14b binary:147 trackId:1 timeDiff:167
06:25:14,168 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:183
06:25:14,168 [main] INFO [MkvTagReader] - SimpleBlock size:153 binary:14f trackId:1 timeDiff:192
06:25:14,169 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:209
06:25:14,169 [main] INFO [MkvTagReader] - SimpleBlock size:147 binary:143 trackId:1 timeDiff:217
06:25:14,169 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:235
06:25:14,170 [main] INFO [MkvTagReader] - SimpleBlock size:db binary:d7 trackId:1 timeDiff:242
06:25:14,170 [main] INFO [MkvTagReader] - Cluster size:1723*
06:25:14,170 [main] INFO [MkvTagReader] - Timecode size:2 uint:469
06:25:14,171 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:0
06:25:14,171 [main] INFO [MkvTagReader] - SimpleBlock size:234 binary:230 trackId:1 timeDiff:6
06:25:14,171 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:26
06:25:14,172 [main] INFO [MkvTagReader] - SimpleBlock size:12b binary:127 trackId:1 timeDiff:31
06:25:14,172 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:52
06:25:14,173 [main] INFO [MkvTagReader] - SimpleBlock size:168 binary:164 trackId:1 timeDiff:56
06:25:14,173 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:78
06:25:14,173 [main] INFO [MkvTagReader] - SimpleBlock size:ea binary:e6 trackId:1 timeDiff:81
06:25:14,174 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:105
06:25:14,174 [main] INFO [MkvTagReader] - SimpleBlock size:1c4 binary:1c0 trackId:1 timeDiff:106
06:25:14,174 [main] INFO [MkvTagReader] - SimpleBlock size:207 binary:203 trackId:1 timeDiff:131
06:25:14,175 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:131
06:25:14,175 [main] INFO [MkvTagReader] - SimpleBlock size:cf binary:cb trackId:1 timeDiff:156
06:25:14,175 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:157
06:25:14,176 [main] INFO [MkvTagReader] - SimpleBlock size:16f binary:16b trackId:1 timeDiff:181
06:25:14,176 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:183
06:25:14,176 [main] INFO [MkvTagReader] - SimpleBlock size:146 binary:142 trackId:1 timeDiff:206
06:25:14,177 [main] INFO [MkvTagReader] - Cluster size:f3f*
06:25:14,177 [main] INFO [MkvTagReader] - Timecode size:2 uint:678
06:25:14,177 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:0
06:25:14,178 [main] INFO [MkvTagReader] - SimpleBlock size:29d binary:299 trackId:1 timeDiff:22
06:25:14,178 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:26
06:25:14,179 [main] INFO [MkvTagReader] - SimpleBlock size:154 binary:150 trackId:1 timeDiff:47
06:25:14,179 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:52
06:25:14,179 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:78
06:25:14,180 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:105
06:25:14,180 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:131
06:25:14,180 [main] INFO [MkvTagReader] - SimpleBlock size:13e binary:13a trackId:2 timeDiff:157
06:25:14,181 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:183
06:25:14,182 [main] INFO [MkvTagReader] - SimpleBlock size:13d binary:139 trackId:2 timeDiff:209

いるのかなぁ・・・一応liveじゃなかったら必要ってかいてあるか・・・
06:25:14,190 [main] INFO [MkvTagReader] - Cues size:66*
06:25:14,183 [main] INFO [MkvTagReader] -  CuePoint size:c*
06:25:14,182 [main] INFO [MkvTagReader] -   CueTime size:1 uint:0
06:25:14,183 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,183 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,183 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:556
06:25:14,184 [main] INFO [MkvTagReader] -  CuePoint size:c*
06:25:14,184 [main] INFO [MkvTagReader] -   CueTime size:1 uint:125
06:25:14,184 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,184 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,184 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:556
06:25:14,185 [main] INFO [MkvTagReader] -  CuePoint size:c*
06:25:14,185 [main] INFO [MkvTagReader] -   CueTime size:1 uint:225
06:25:14,185 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,185 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,185 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:6583
06:25:14,187 [main] INFO [MkvTagReader] -  CuePoint size:d*
06:25:14,186 [main] INFO [MkvTagReader] -   CueTime size:2 uint:350
06:25:14,186 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,186 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,186 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:6583
06:25:14,188 [main] INFO [MkvTagReader] -  CuePoint size:d*
06:25:14,187 [main] INFO [MkvTagReader] -   CueTime size:2 uint:475
06:25:14,188 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,187 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,188 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:13107
06:25:14,189 [main] INFO [MkvTagReader] -  CuePoint size:d*
06:25:14,188 [main] INFO [MkvTagReader] -   CueTime size:2 uint:575
06:25:14,189 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,189 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,189 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:13107
06:25:14,190 [main] INFO [MkvTagReader] -  CuePoint size:d*
06:25:14,189 [main] INFO [MkvTagReader] -   CueTime size:2 uint:700
06:25:14,190 [main] INFO [MkvTagReader] -   CueTrackPositions size:7*
06:25:14,190 [main] INFO [MkvTagReader] -    CueTrack size:1 uint:1
06:25:14,190 [main] INFO [MkvTagReader] -    CueClusterPosition size:2 uint:19042
	 */
	public MkvTagWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	public MkvTagWriter(FileOutputStream fileOutputStream) {
		this(fileOutputStream.getChannel());
	}
	public MkvTagWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	@Override
	public void prepareHeader(CodecType ...codecs) throws Exception {
		// EBMLの動作はwebmになったらoverrideしてmatroskaではなく、webmと記入してやりたいところ
		// EBML
		setupEbml();
		// Segment
		setupSegment();
		// infoはいいけど、tracksの作成が不能になるわけか・・・
		// 仮データをつくっておいて、データがうけとったら、必要なものを書き込むようにしておく。
		// といった感じにしておこうかね
		// コーデックの詳細情報がわからないとどうしていいかわからなくなる
		// SeekHead
		setupSeekHead();
		// Info
		// Tracks
		// あたりは記入できるか？
	}
	private void setupSeekHead() {
		// この部分では、Info Tracks TagsとVoidを準備しておく(voidのところにあとでcuesを追加する(tailer?))
		// infoとtracks、tagsについては長さを知っている必要があるので、あらかじめつくらないとだめか？
	}
	/**
	 * segmentの冒頭部つくっておく
	 * @throws Exception
	 */
	private void setupSegment() throws Exception {
		Segment segment = new Segment();
		segment.setInfinite(true);
		addContainer(segment);
	}
	/**
	 * ebmlの部分の初期化を実施
	 */
	protected void setupEbml() throws Exception {
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
		addContainer(ebml);
	}
	@Override
	public void prepareTailer() throws Exception {
		if(outputStream != null) {
			try {
				outputStream.close();
			}
			catch(Exception e) {
			}
			outputStream = null;
		}
	}
	@Override
	public void addContainer(IContainer container) throws Exception {
		if(container instanceof MkvMasterTag) {
			MkvMasterTag masterTag = (MkvMasterTag)container;
			outputChannel.write(masterTag.getData());
			for(MkvTag tag : masterTag.getChildList()) {
				addContainer(tag);
			}
		}
		else {
			outputChannel.write(container.getData());
		}
	}
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
		// vp8やvp9の場合はinvisible判定をとっておかないとこまったことになるかもしれない。(BlockTagに設定項目があるため。)
	}
}
