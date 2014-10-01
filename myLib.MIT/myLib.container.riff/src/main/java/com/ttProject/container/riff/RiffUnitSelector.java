/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.type.Data;
import com.ttProject.container.riff.type.Fact;
import com.ttProject.container.riff.type.Fmt;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;
import com.ttProject.util.BufferUtil;

/**
 * riffのunit解析動作
 * @author taktod
 */
public class RiffUnitSelector implements ISelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(RiffUnitSelector.class);
	/** headerUnit */
	private RiffHeaderUnit headerUnit = null;
	/** format情報 */
	private Fmt fmt = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		// はじめの4byteを確認する。(RIFFになっていて)
		Type type = Type.getType(BufferUtil.safeRead(channel, 4).getInt());
		RiffUnit unit = null;
		switch(type) {
		case RIFF: // header
			if(channel.position() != 4) {
				throw new Exception("position of header is invalid.");
			}
			unit = new RiffHeaderUnit();
			headerUnit = (RiffHeaderUnit)unit;
			break;
		case FMT: // フォーマット情報必須
			unit = new Fmt();
			fmt = (Fmt)unit;
			break;
		case FACT: // サンプル数等、なくてもいい
			unit = new Fact();
			break;
		case DATA: // データ本体
			unit = new Data(); // データはでかいので、このままおいとく。
			break;
		case LIST: // なくてもいい
			break;
		default:
			throw new RuntimeException("unexpected frame type.:" + type);
		}
		if(unit == null) {
			throw new Exception("unit is undefined.maybe non-support type.:" + type);
		}
		if(!(unit instanceof RiffHeaderUnit)) {
			unit.setHeaderUnit(headerUnit);
			if(!(unit instanceof Fmt)) {
				unit.setFmt(fmt);
			}
		}
		unit.minimumLoad(channel);
		return unit;
	}
}
