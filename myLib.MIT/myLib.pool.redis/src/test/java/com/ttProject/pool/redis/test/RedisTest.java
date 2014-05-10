package com.ttProject.pool.redis.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redisの動作テスト
 * @author taktod
 */
public class RedisTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(RedisTest.class);
	/**
	 * Poolテスト
	 * @throws Exception
	 */
	@Test
	public void poolTest() throws Exception {
		logger.info("jedisPoolテスト");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(12);
		JedisPool pool = new JedisPool(config, "localhost", 6379);
		Jedis jedis = pool.getResource();
		jedis.set("test", "hoge");
		logger.info("result:" + jedis.get("test"));
	}
	/**
	 * 単体テスト
	 * @throws Exception
	 */
	@Test
	public void singleTest() throws Exception {
		logger.info("jedisテスト");
		Jedis jedis = null;
		try {
			jedis = new Jedis("localhost", 6379);
			logger.info("result:" + jedis.get("test"));
		}
		finally {
			if(jedis != null) {
				try {
					jedis.close();
				}
				catch(Exception e) {
				}
				jedis = null;
			}
		}
	}
}
