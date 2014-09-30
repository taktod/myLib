/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;

/**
 * log appender for ndc injection.
 * this appender works like hook.
 * this appender should be put first.
 * @author taktod
 */
public class NDCInjectAppender extends AppenderSkeleton {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void append(LoggingEvent event) {
		// if the data size is changed, re-initialize.
		if(NDC.getDepth() != AllThreadNDCInjection.data.size()) {
			NDC.clear();
			for(String item : AllThreadNDCInjection.data) {
				NDC.push(item);
			}
		}
	}
}
