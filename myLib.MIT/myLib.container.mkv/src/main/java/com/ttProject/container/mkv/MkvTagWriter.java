/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.container.mkv.type.Audio;
import com.ttProject.container.mkv.type.Channels;
import com.ttProject.container.mkv.type.CodecID;
import com.ttProject.container.mkv.type.CodecPrivate;
import com.ttProject.container.mkv.type.DefaultDuration;
import com.ttProject.container.mkv.type.DocType;
import com.ttProject.container.mkv.type.DocTypeReadVersion;
import com.ttProject.container.mkv.type.DocTypeVersion;
import com.ttProject.container.mkv.type.EBML;
import com.ttProject.container.mkv.type.EBMLMaxIDLength;
import com.ttProject.container.mkv.type.EBMLMaxSizeLength;
import com.ttProject.container.mkv.type.EBMLReadVersion;
import com.ttProject.container.mkv.type.EBMLVersion;
import com.ttProject.container.mkv.type.FlagLacing;
import com.ttProject.container.mkv.type.Info;
import com.ttProject.container.mkv.type.Language;
import com.ttProject.container.mkv.type.MuxingApp;
import com.ttProject.container.mkv.type.PixelHeight;
import com.ttProject.container.mkv.type.PixelWidth;
import com.ttProject.container.mkv.type.SamplingFrequency;
import com.ttProject.container.mkv.type.Seek;
import com.ttProject.container.mkv.type.SeekHead;
import com.ttProject.container.mkv.type.SeekID;
import com.ttProject.container.mkv.type.SeekPosition;
import com.ttProject.container.mkv.type.Segment;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.container.mkv.type.SimpleTag;
import com.ttProject.container.mkv.type.Tag;
import com.ttProject.container.mkv.type.TagName;
import com.ttProject.container.mkv.type.TagString;
import com.ttProject.container.mkv.type.Tags;
import com.ttProject.container.mkv.type.Targets;
import com.ttProject.container.mkv.type.TimecodeScale;
import com.ttProject.container.mkv.type.TrackEntry;
import com.ttProject.container.mkv.type.TrackNumber;
import com.ttProject.container.mkv.type.TrackType;
import com.ttProject.container.mkv.type.TrackUID;
import com.ttProject.container.mkv.type.Tracks;
import com.ttProject.container.mkv.type.Video;
import com.ttProject.container.mkv.type.Void;
import com.ttProject.container.mkv.type.WritingApp;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.frame.h264.H264Frame;
import com.ttProject.util.HexUtil;

/**
 * mkvを作成するためのwriter
 * とりあえず、headerの部分はできたつもり、次はCluster
 * Clusterはまとまり分できたら一気に出力するという形にしないとだめっぽいですね。
 * ffmpegの出力のmkvをみてみたけど、別にkeyFrameからはじまらないとだめということはないみたいですね。
 * timediffは線形増加になっているので、転置が発生するとまずいかもしれませんね。
 * 
 * 一応、keyFrame間隔をベースにつくっておいた方がシークとしては、有利に働きそうだが・・・
 * cuesの動作については、いまのところ不明・・・あとでファイルを解析すればいいか・・・
 * とりあえず、h264 + aacのデータを再生成させて、再生できるところまでもっていきたいね。
 * @author taktod
 */
public class MkvTagWriter implements IWriter {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvTagWriter.class);
	private long position = 0; // すでに書き込んだデータ量
	private final WritableByteChannel outputChannel;
	private FileOutputStream outputStream = null;
	
	private SeekHead seekHead = null;
	private Info info = null;
	private Tracks tracks = null;
	private Tags tags = null;
	// ライブストリームとして動作することを考慮すると、seekHead、info、tracks、tagsの情報はよこから取得できるようになっていると有利になりそう
	// またclusterの作成を実施した時点で、そのデータを出力として取り上げることができると助かるけど・・・
	// trackEntryのデータを保持しておいて、frameを取得したときに、対応するtrackEntryの詳細情報を構築するようにしむける
	/** trackEntryの紐付け用の一時データ */
	private List<TrackEntry> trackEntries = new ArrayList<TrackEntry>();
	/** 追加frameがどのtrackEntryであるかのmap保持(これ必要ないかもしれませんね。再生中にtrackEntryのデータを参照する必要ないのでは？) */
	private Map<Integer, TrackEntry> trackEntryMap = new HashMap<Integer, TrackEntry>();
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
06:25:14,121 [main] INFO [MkvTagReader] -   Duration size:8 float:913.0 (ライブならスキップでOKか？)一応voidいれておいてtailerで書き込むようにしておくべし
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
	/**
	 * コンストラクタ
	 * @param fileName
	 * @throws Exception
	 */
	public MkvTagWriter(String fileName) throws Exception {
		outputStream = new FileOutputStream(fileName);
		this.outputChannel = outputStream.getChannel();
	}
	/**
	 * コンストラクタ
	 * @param fileOutputStream
	 */
	public MkvTagWriter(FileOutputStream fileOutputStream) {
		this(fileOutputStream.getChannel());
	}
	/**
	 * コンストラクタ
	 * @param outputChannel
	 */
	public MkvTagWriter(WritableByteChannel outputChannel) {
		this.outputChannel = outputChannel;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareHeader(CodecType ...codecs) throws Exception {
		// EBMLの動作はwebmになったらoverrideしてmatroskaではなく、webmと記入してやりたいところ
		// EBML
		setupEbml();
		// Segment
		setupSegment();
		// ここまではここで書き込みを実施しておく
		// seekHeadは作成しません。大きさは0x60になるように調整しますが、Voidタグで埋めを実施します。
		// あとで情報がそろってから作成します。(masterTagのremakeができないため)
		setupInfo();
		setupTracks(codecs);
		setupTags();
		// header情報そのものは、必要なcodecsに対応するframeデータを取得完了したときに、完了します。
	}
	/**
	 * ebmlの部分の初期化を実施
	 * webmのwriterではこの部分をoverrideさせて終わらせようと思う。
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
	 * info情報をつくっておく
	 */
	private void setupInfo() throws Exception {
		info = new Info();
		TimecodeScale timecodeScale = new TimecodeScale();
		timecodeScale.setValue(1000000L);
		info.addChild(timecodeScale);
		MuxingApp muxingApp = new MuxingApp();
		muxingApp.setValue("ttProject.mkvMuxer");
		info.addChild(muxingApp);
		WritingApp writingApp = new WritingApp();
		writingApp.setValue("ttProject.mkvWriter");
		info.addChild(writingApp);
//		Duration duration = new Duration(); // durationはtailerで書き込む (上書きを実施するときには、必要な箇所にbyteデータを書き込むという形で対処します。)
		Void voidTag = new Void();
		voidTag.setTagSize(16);
		info.addChild(voidTag);
	}
	/**
	 * tracks情報をつくっておく(この場では完了しない)
	 * @param codecs
	 * @throws Exception
	 */
	private void setupTracks(CodecType ...codecs) throws Exception {
		tracks = new Tracks();
		for(CodecType codecType : codecs) {
			// trackEntryの基本となる、codecTypeの指定のみ実施しておきます。
			TrackEntry trackEntry = new TrackEntry();
			CodecID codecId = new CodecID();
			codecId.setCodecType(codecType);
			trackEntry.addChild(codecId);
			// 全部取得済みであるかをこのリストのsizeが0になったかで判定するので、必要か・・・
			trackEntries.add(trackEntry); // trackEntryを保持しておく。
			tracks.addChild(trackEntry);
		}
	}
	/**
	 * tagsをつくっておく
	 * @throws Exception
	 */
	private void setupTags() throws Exception {
		tags = new Tags();
		Tag tag = new Tag();
		tags.addChild(tag);
		Targets targets = new Targets(); // masterタグなのに空っぽなんだ、へぇ〜
		tag.addChild(targets);
		SimpleTag simpleTag = new SimpleTag();
		tags.addChild(simpleTag);
		TagName tagName = new TagName();
		tagName.setValue("ENCODER");
		simpleTag.addChild(tagName);
		TagString tagString = new TagString();
		tagString.setValue("ttProject.mkvMuxer");
		simpleTag.addChild(tagString);
	}
	/**
	 * {@inheritDoc}
	 * TODO INFO のduration設定、Segmentのデータ量設定、SeekHeadにCuesへのseek動作の追加
	 * Cuesとその配下データの構築が必要になると思う。
	 */
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
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContainer(IContainer container) throws Exception {
		if(container instanceof MkvMasterTag) {
			MkvMasterTag masterTag = (MkvMasterTag)container;
			writeData(masterTag.getData());
			for(MkvTag tag : masterTag.getChildList()) {
				addContainer(tag);
			}
		}
		else {
			writeData(container.getData());
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addFrame(int trackId, IFrame frame) throws Exception {
		// マルチframeデータの場合は分解させる
		if(frame instanceof AudioMultiFrame) {
			AudioMultiFrame multiFrame = (AudioMultiFrame)frame;
			for(IAudioFrame aFrame : multiFrame.getFrameList()) {
				addFrame(trackId, aFrame);
			}
			return;
		}
		else if(frame instanceof VideoMultiFrame) {
			VideoMultiFrame multiFrame = (VideoMultiFrame)frame;
			for(IVideoFrame vFrame : multiFrame.getFrameList()) {
				addFrame(trackId, vFrame);
			}
			return;
		}
		// TODO この部分別関数化しておく。
		// 正直邪魔
		if(!trackEntryMap.containsKey(trackId)) {
			// trackEntryMap化していないトラック
			logger.info("trackEntryMap化されていないトラック");
			TrackEntry findTrackEntry = null;
			for(TrackEntry trackEntry : trackEntries) {
				for(MkvTag tag : trackEntry.getChildList()) {
					if(tag instanceof CodecID) {
						CodecID codecID = (CodecID) tag;
						if(codecID.getCodecType() == MkvCodecType.getCodecType(frame.getCodecType())) {
							logger.info("コーデック情報が一致した。:" + frame.getCodecType() + " / " + codecID.getCodecType());
							findTrackEntry = trackEntry;
							break;
						}
					}
				}
				if(findTrackEntry != null) {
					break;
				}
			}
			if(findTrackEntry != null) {
				trackEntryMap.put(trackId, findTrackEntry);
				trackEntries.remove(findTrackEntry);
				// 共通情報をつけておく
				TrackNumber trackNumber = new TrackNumber();
				trackNumber.setValue(trackId);
				findTrackEntry.addChild(trackNumber);
				
				TrackUID trackUID = new TrackUID();
				trackUID.setValue(trackId);
				findTrackEntry.addChild(trackUID);
				
				FlagLacing flagLacing = new FlagLacing();
				flagLacing.setValue(0);
				findTrackEntry.addChild(flagLacing);
				
				Language language = new Language();
				language.setValue("und");
				findTrackEntry.addChild(language);
				
				// 必要な情報を追記しておく。
				if(frame instanceof IAudioFrame) {
					IAudioFrame aFrame = (IAudioFrame)frame;
					
					TrackType trackType = new TrackType();
					trackType.setValue(2); // 音声は2
					findTrackEntry.addChild(trackType);
					
					Audio audio = new Audio();
					findTrackEntry.addChild(audio);
					
					Channels channels = new Channels();
					channels.setValue(aFrame.getChannel());
					audio.addChild(channels);
					
					SamplingFrequency samplingFrequency = new SamplingFrequency();
					samplingFrequency.setValue(aFrame.getSampleRate());
					audio.addChild(samplingFrequency);
					
					switch(aFrame.getCodecType()) {
					case AAC:
						{
							DecoderSpecificInfo dsi = ((com.ttProject.frame.aac.type.Frame)aFrame).getDecoderSpecificInfo();
							CodecPrivate codecPrivate = new CodecPrivate();
							codecPrivate.setValue(dsi.getData());
							findTrackEntry.addChild(codecPrivate);
						}
						break;
					case VORBIS:
					case SPEEX:
					case OPUS:
						logger.error(aFrame.getCodecType() + "の処理はまだ未作成");
						break;
					default:
						break;
					}
				}
				else if(frame instanceof IVideoFrame) {
					IVideoFrame vFrame = (IVideoFrame)frame;
					
					TrackType trackType = new TrackType();
					trackType.setValue(1); // 動画は1
					findTrackEntry.addChild(trackType);
					
					// これはいらないかもしれないな
					DefaultDuration defaultDuration = new DefaultDuration();
					defaultDuration.setValue(1000000); // 10fps
					findTrackEntry.addChild(defaultDuration);
					
					Video video = new Video();
					findTrackEntry.addChild(video);
					
					PixelWidth pixelWidth = new PixelWidth();
					pixelWidth.setValue(vFrame.getWidth());
					video.addChild(pixelWidth);
					
					PixelHeight pixelHeight = new PixelHeight();
					pixelHeight.setValue(vFrame.getHeight());
					video.addChild(pixelHeight);
					
					// あとはh264やh265ならcodecPrivateを設定する必要あり
					switch(vFrame.getCodecType()) {
					case H264:
						{
							H264Frame h264Frame = (H264Frame) vFrame;
							// h264のcodecPrivateを作る必要あり
							com.ttProject.frame.h264.ConfigData configData = new com.ttProject.frame.h264.ConfigData();
							CodecPrivate codecPrivate = new CodecPrivate();
							codecPrivate.setValue(configData.makeConfigData(h264Frame.getSps(), h264Frame.getPps()));
							findTrackEntry.addChild(codecPrivate);
						}
						break;
					case H265:
						logger.error("h265はまだ未実装です。");
						break;
					default:
						break;
					}
				}
				// この完了して、headerを構築する部分も別関数化しておきたいけど・・・
				if(trackEntries.size() == 0) {
					logger.info("全トラック情報がみつかったので、調整しておく。");
					// 強制的にsizeを更新しておく
					// seekHeadのサイズは0x80くらいとする
					int originalSeekHeadSize = 0x60; // seekHeadのサイズが大きくなった分、voidのサイズを削らないとだめ
					// それぞれのデータについてgetDataを実施して、サイズを確定させる
					info.getData();
					tracks.getData();
					tags.getData();
					// スキップさせるデータ量を確認し、seekの値として保持させる。
					long skipSize = originalSeekHeadSize;
					seekHead = new SeekHead();
					seekHead.addChild(setupSeek(Type.Info, skipSize));
					skipSize += info.getSize();
					seekHead.addChild(setupSeek(Type.Tracks, skipSize));
					skipSize += tracks.getSize();
					seekHead.addChild(setupSeek(Type.Tags, skipSize));

					// seekHeadのデータ構築開始
					seekHead.getData();
					if(seekHead.getSize() != originalSeekHeadSize) {
						// 空白がある場合はVoidでうめておく。
						int diff = originalSeekHeadSize - seekHead.getSize();
						Void voidTag = new Void();
						voidTag.setTagSize(diff - 2);
						seekHead.addChild(voidTag);
						seekHead.getData();
					}
					// データの書き込みを実施
					addContainer(seekHead);
					addContainer(info);
					addContainer(tracks);
					addContainer(tags);
					// header部作成完了
				}
				else {
					// ここにきた場合はframeデータをcacheしておかないとだめ
					// データが構築済みになっていないので、データを保持しておく必要あるかも・・・
					// とりあえず１つsimpleTagをつくってみようと思う。
					// 過去あつかったときには、mp3はlacingをつかって、１つのsimpleTagに大量にデータがはいっていたけど、aacのデータを確認してみたところ、1つのAACFrameに対して1つのsimpleTagで動作しているみたいですね。
				}
			}
		}
		if(frame instanceof IAudioFrame) {
			logger.info("音声の場合のみちょっとsimpleTag化してみよう");
			SimpleBlock simpleBlock = new SimpleBlock();
			simpleBlock.addFrame(trackId, frame, 0);
			logger.info(HexUtil.toHex(simpleBlock.getData()));
		}
		// vp8やvp9の場合はinvisible判定をとっておかないとこまったことになるかもしれない。(BlockTagに設定項目があるため。)
	}
	/**
	 * seekの中身を作成する
	 * @param type
	 * @return
	 */
	private MkvTag setupSeek(Type type, long pos) throws Exception {
		Seek seek = new Seek();
		SeekID seekId = new SeekID();
		ByteBuffer idBuffer = ByteBuffer.allocate(4);
		idBuffer.putInt(type.intValue());
		idBuffer.flip();
		seekId.setValue(idBuffer);
		seek.addChild(seekId);
		SeekPosition position = new SeekPosition();
		position.setValue(pos);
		seek.addChild(position);
		return seek;
	}
	/**
	 * 実データの書き込み処理
	 * @param buffer
	 * @throws Exception
	 */
	private void writeData(ByteBuffer buffer) throws Exception {
		position += buffer.remaining(); // 書き込む量を追加していく
		outputChannel.write(buffer);
	}
}
