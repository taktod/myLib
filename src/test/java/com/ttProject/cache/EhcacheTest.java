package com.ttProject.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;

import com.ttProject.library.HexUtils;

public class EhcacheTest {
	public static Cache getEncacheInstance() throws Exception {
		CacheManager manager = CacheManager.getInstance();
		if(manager.cacheExists("test")) {
			return manager.getCache("test");
		}
		Cache cache = new Cache(
			"test", // cacheの名前
			1, // あまりにデータ量がおおい場合はdiskcacheにまわす。
			true, // overFlowしたものはdiskに書き込んでcacheさせる
			false, // 永続にはしないでexpireが有効になるようにしておく
			0, // maxLiveは0にしておいて作成してからのexpireはつけない
			5); // maxIdleは適当な値をつけておいて、アクセスがなかったらexpireするようにしておく
		manager.addCache(cache);
		return cache;
	}
	@Test
	public void ehcacheTest() {
		try {
			Cache cache = getEncacheInstance();
			// とりあえずcacheにデータをぶち込んでみようか
			cache.put(new Element("a", "b"));
			Thread.sleep(2000);
			Element e = cache.get("a");
			System.out.println(e.getValue());
			Thread.sleep(2000);
			e = cache.get("a");
			System.out.println(e.getValue());
			Thread.sleep(2000);
			e = cache.get("a");
			System.out.println(e.getValue());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 大きなデータをぶち込んでみるテストをやってみる。
	 */
	@Test
	public void binaryTest() {
		try {
			Cache cache = getEncacheInstance();
			
			cache.put(new Element("binary", "test".getBytes()));
			Element e = cache.get("binary");
			System.out.println(HexUtils.toHex((byte[])e.getValue(), true));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
