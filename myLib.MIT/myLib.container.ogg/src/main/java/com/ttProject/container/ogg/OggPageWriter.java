package com.ttProject.container.ogg;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.IFrame;

/**
 * oggの書き込み動作
 * とりあえず、speexに対応するなら、startPageにspeexのコーデック情報(headerFrame)を、次のpageにメタデータ(commentFrame)をいれないとだめ。
 * 他のデータとまじってもいやなので、次のようにする必要がある。
 * 
 * oggデータとしては、
 * @author taktod
 */
public class OggPageWriter implements IWriter {
	@Override
	public void addContainer(IContainer container) throws Exception {

	}
	@Override
	public void addFrame(IFrame frame) throws Exception {

	}
	@Override
	public void prepareHeader() throws Exception {
		
	}
	@Override
	public void prepareTailer() throws Exception {

	}
}
