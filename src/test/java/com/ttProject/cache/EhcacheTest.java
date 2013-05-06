package com.ttProject.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

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
			5, // maxLiveは0にしておいて作成してからのexpireはつけない
			0); // maxIdleは適当な値をつけておいて、アクセスがなかったらexpireするようにしておく
		manager.addCache(cache);
		return cache;
	}
	@Test
	public void ehcacheTest() {
		try {
			Cache cache = getEncacheInstance();
			cache.getCacheEventNotificationService().registerListener(new CacheEventListener() {
				@Override
				public Object clone() throws CloneNotSupportedException {
					return super.clone();
				}
				public void notifyRemoveAll(Ehcache arg0) {
					System.out.println("remove all");
				}
				public void notifyElementUpdated(Ehcache arg0, Element arg1)
						throws CacheException {
					System.out.println("notify element update");
				}
				public void notifyElementRemoved(Ehcache arg0, Element arg1)
						throws CacheException {
					System.out.println("notify element removed");
				}
				public void notifyElementPut(Ehcache arg0, Element arg1)
						throws CacheException {
					System.out.println("notify element put");
				}
				public void notifyElementExpired(Ehcache arg0, Element arg1) {
					// これでexpireしたというイベントが取得可能なわけか・・・
					System.out.println(arg1);
					System.out.println("notify element expired");
				}
				public void notifyElementEvicted(Ehcache arg0, Element arg1) {
					System.out.println("notify element evicted");
				}
				public void dispose() {
					System.out.println("dispose");
				}
			});
			// とりあえずcacheにデータをぶち込んでみようか
			cache.put(new Element("a", "b"));
			Thread.sleep(2000);
			Element e = cache.get("a");
			System.out.println(e.getValue());
			Thread.sleep(2000);
			e = cache.get("a");
			System.out.println(e.getValue());
			Thread.sleep(2000);
//			e = cache.get("a");
//			System.out.println(e.getValue());
			Thread.sleep(200000);
			System.out.println("owari");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * バイナリデータ
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
	/**
	 * streamの形で徐々に取得するってできないだろうかな・・・
	 */
	@Test
	public void streamTest() {
		try {
//			Cache cache = getEncacheInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
