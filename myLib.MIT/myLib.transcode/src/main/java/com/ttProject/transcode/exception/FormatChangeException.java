/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.transcode.exception;

/**
 * フォーマットが変更になった場合の例外
 * @author taktod
 */
public class FormatChangeException extends Exception {
	/** シリアル番号 */
	private static final long serialVersionUID = -868158287597825444L;
	public FormatChangeException() {
		super();
	}
	public FormatChangeException(String message) {
		super(message);
	}
	public FormatChangeException(Throwable cause) {
		super(cause);
	}
	public FormatChangeException(String message, Throwable cause) {
		super(message, cause);
	}
}
