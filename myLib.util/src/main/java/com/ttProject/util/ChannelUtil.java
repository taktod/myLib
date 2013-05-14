package com.ttProject.util;

import java.nio.channels.FileChannel;

import com.ttProject.nio.channels.IFileReadChannel;

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
	public static IFileReadChannel safeClose(IFileReadChannel channel) {
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
