/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.client;

import com.flazr.rtmp.client.ClientOptions;

/**
 * clientOptionsの動作を再定義しておく
 * objectEncodingの部分だけ、値を文字列からNumberに変更しておきたい
 * @author taktod
 */
public class ClientOptionsEx extends ClientOptions {
	/**
	 * url解析動作をhookしておく
	 */
	public boolean parseCli(String[] args) {
		// 普通に解析しておく
		boolean result = super.parseCli(args);
		// objectEncodingの値だけ、数値である必要があるので(特にwowza)値を確認して数値化しておく。
		if(getParams() != null) {
			Object encoding = getParams().get("objectEncoding");
			if("3".equals(encoding)) {
				putParam("objectEncoding", 3.0);
			}
			else {
				putParam("objectEncoding", 0.0);
			}
		}
		return result;
	};
}
