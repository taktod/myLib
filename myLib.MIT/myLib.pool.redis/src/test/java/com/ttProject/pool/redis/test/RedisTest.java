/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.pool.redis.test;

import org.apache.log4j.Logger;

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
//	@Test
	public void poolTest() throws Exception {
		logger.info("jedisPool test");
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(12);
		JedisPool pool = new JedisPool(config, "localhost", 6379);
		Jedis jedis = pool.getResource();
//		jedis.set("test", "hoge");
		jedis.rpush("test", "1");
		jedis.rpush("test", "2");
		jedis.rpush("test", "3");
		jedis.rpush("test", "4");
		jedis.rpush("test", "5");
		jedis.rpush("test", "6");
		logger.info("result:" + jedis.lrange("test", 0, -1));
	}
	/**
	 * 単体テスト
	 * @throws Exception
	 */
//	@Test
	public void singleTest() throws Exception {
		logger.info("jedis test");
		Jedis jedis = null;
		try {
			jedis = new Jedis("localhost", 6379);
//			logger.info("result:" + jedis.get("test"));
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
