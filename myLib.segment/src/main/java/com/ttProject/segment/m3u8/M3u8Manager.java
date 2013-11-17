package com.ttProject.segment.m3u8;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.ttProject.segment.ISegmentManager;

public class M3u8Manager implements ISegmentManager {
	private final String header;
	private final String allowCache;
	private final String targetDuration;
	private final String targetVersion;
	private Integer num;
	// このあたりのstaticデータの管理をするやつがm3u8Managerということにしておきたい。
	private final String m3u8File;
	private final Integer limit; // limitの設定は固定3でいいはずだが、動作検証で全データ出力させてみたいときもあるので、注意が必要。
	List<M3u8Element> elementData;
	/**
	 * コンストラクタ
	 * @param m3u8File
	 */
	public M3u8Manager(String m3u8File, float duration, Integer limit) {
		header         = "#EXTM3U";
		allowCache     = "#EXT-X-ALLOW-CACHE:NO";
		targetDuration = "#EXT-X-TARGETDURATION:" + (int)duration;
		targetVersion  = "#EXT-X-VERSION:3";
		this.m3u8File  = m3u8File;
		this.limit = limit;
		if(limit != null) {
			elementData = new ArrayList<M3u8Element>();
			num = 0;
		}
		else {
			// ファイルに先頭の情報を書き込む
			elementData = null;
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(m3u8File, false)));
				pw.println(header);
				pw.println(allowCache);
				pw.println(targetDuration);
				pw.println(targetVersion);
				pw.close();
				pw = null;
			}
			catch (Exception e) {
			}
		}
	}
	/**
	 * データの書き込み処理
	 * @param target
	 * @param http
	 * @param duration
	 * @param index
	 * @param endFlg
	 */
	@Override
	public void writeData(String target, String http, float duration, int index, boolean endFlg) {
		M3u8Element element = new M3u8Element(target, http, duration, index);
		if(limit != null) {
			// limitが設定されている場合は、m3u8上のデータ量がきまっている。
			elementData.add(element); // エレメントを追加する。
			if(elementData.size() > limit) {
				// elementデータよりサイズが大きい場合は必要のないデータがあるので、先頭のデータを落とす
				M3u8Element removedData = elementData.remove(0);
				// いらなくなったファイルは削除する必要があるので、消す
				File deleteFile = new File(removedData.getFile());
				if(deleteFile.exists()) {
					// 削除しておく。
					deleteFile.delete();
				}
			}
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(m3u8File, false)));
				pw.println(header);
				pw.println(allowCache);
				pw.println(targetDuration);
				pw.println(targetVersion);
				pw.print("#EXT-X-MEDIA-SEQUENCE:");
				num ++;
				pw.println(num);
				// 内容を書き込む
				for(M3u8Element data : elementData) {
					if(data.isFirst()) {
						pw.println("#EXT-X-DISCONTINUITY");
					}
					pw.println(data.getInfo());
					pw.println(data.getHttp());
				}
				if(endFlg) {
					pw.println("#EXT-X-ENDLIST");
				}
				pw.close();
				pw = null;
			}
			catch (Exception e) {
			}
		}
		else {
			try {
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(m3u8File, true)));
				pw.println(element.getInfo());
				pw.println(element.getHttp());
				if(endFlg) {
					pw.println("#EXT-X-ENDLIST");
				}
				pw.close();
				pw = null;
			}
			catch (Exception e) {
			}
		}
	}
}
