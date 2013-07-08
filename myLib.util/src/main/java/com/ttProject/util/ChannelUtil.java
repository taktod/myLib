package com.ttProject.util;

import java.nio.channels.FileChannel;

import com.ttProject.nio.channels.IReadChannel;

/**
 * channel用のUtility
 * @author taktod
 */
public class ChannelUtil {
	public static FileChannel safeClose(FileChannel channel) {
		if(channel != null) {
			try {
				channel.close();
			}
			catch(Exception e) {
			}
		}
		return null;
	}
	public static IReadChannel safeClose(IReadChannel channel) {
		if(channel != null) {
			try {
				channel.close();
			}
			catch (Exception e) {
			}
		}
		return null;
	}
}
