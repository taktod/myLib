package com.ttProject.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;

public class NDCInjectAppender extends AppenderSkeleton {
	@Override
	public void close() {

	}
	@Override
	public boolean requiresLayout() {
		return false;
	}
	@Override
	protected void append(LoggingEvent event) {
		if(NDC.getDepth() != AllThreadNDCInjection.data.size()) {
			NDC.clear();
			for(String item : AllThreadNDCInjection.data) {
				NDC.push(item);
			}
		}
	}
}
