package com.ttProject.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.junit.Test;

public class CacheTest {
	@Test
	public void cacheTest() {
		CacheManager manager = CacheManager.getInstance();
		try {
			Cache cache = new Cache(
				"test",
				1, true,
				false, 5, 5);
			manager.addCache(cache);
			cache.put(new Element("a", "b"));
			cache.put(new Element("b", "c"));
			cache.put(new Element("c", "d"));
			Thread.sleep(2000);
			cache = manager.getCache("test");
			Element e = cache.get("a");
			System.out.println(e.getValue());
			Thread.sleep(6000);
			cache = manager.getCache("test");
			e = cache.get("a");
			System.out.println(e.getValue());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
