package com.ttProject.unit;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 返すデータが特にないときに応答する仮データ
 * mpegtsやh264の動作で利用します。
 * 
 * @author taktod
 */
public class NullUnit extends Unit {
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
		
	}
	@Override
	protected void requestUpdate() throws Exception {
		
	}
}
