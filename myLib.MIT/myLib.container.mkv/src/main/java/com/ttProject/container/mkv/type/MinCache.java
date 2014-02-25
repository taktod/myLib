package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvUnsignedIntTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * MinCacheタグ
 * どういうものなのかここに記述がありました
 * @see http://lists.matroska.org/pipermail/matroska-devel/2003-March/000332.html
 * @author taktod
 */
public class MinCache extends MkvUnsignedIntTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public MinCache(EbmlValue size) {
		super(Type.MinCache, size);
	}
	/**
	 * コンストラクタ
	 */
	public MinCache() {
		this(new EbmlValue());
	}
}
