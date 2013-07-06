package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

/**
 * メタデータ保持タグ
 * @author taktod
 */
public class Moov extends ParentAtom {
	public Moov(int position, int size) {
		super(Moov.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("");
	}
}
