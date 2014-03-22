package com.ttProject.container.mp4.type;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.container.mp4.Type;
import com.ttProject.unit.extra.bit.Bit32;

/**
 * ilstの定義
 * @author taktod
 * 中のデータはそのままTagRecordになっているみたいです。
 * 数の定義は実際にあるデータについては、存在していませんでした。
 * 'trkn': 'Track',
 * '\xa9ART': 'Artist',
 * '\xa9nam': 'Title',
 * '\xa9alb': 'Album',
 * '\xa9day': 'Year',
 * '\xa9gen': 'Genre',
 * '\xa9cmt': 'Comment',
 * '\xa9wrt': 'Writer',
 * '\xa9too': 'Tool', // ffmpegではencoderとして認識されるらしい
 */
public class Ilst extends Mp4Atom {
	/**
	 * コンストラクタ
	 * @param size
	 * @param name
	 */
	public Ilst(Bit32 size, Bit32 name) {
		super(size, name);
	}
	/**
	 * コンストラクタ
	 */
	public Ilst() {
		super(new Bit32(), Type.getTypeBit(Type.Ilst));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
