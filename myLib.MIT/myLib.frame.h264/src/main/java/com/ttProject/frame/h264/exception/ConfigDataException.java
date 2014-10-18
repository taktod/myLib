package com.ttProject.frame.h264.exception;

/**
 * configDataException
 * during analyze configData, I got sps is null for some avi file.
 * In this case, throw this exception and try to use NalAnalyzer instead of DataNalAnalyzer.
 * @author taktod
 */
public class ConfigDataException extends Exception {
	private static final long serialVersionUID = 6305048396033775109L;
	public ConfigDataException() {
		super();
	}
	public ConfigDataException(String message) {
		super(message);
	}
	public ConfigDataException(Throwable cause) {
		super(cause);
	}
	public ConfigDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
