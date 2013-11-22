package com.ttProject.transcode.xuggle.exception;

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
