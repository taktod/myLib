package com.ttProject.container.ogg;

import com.ttProject.container.IContainer;
import com.ttProject.container.IWriter;
import com.ttProject.frame.IFrame;

/**
 * oggの書き込み動作
 * とりあえず、speexに対応するなら、startPageにspeexのコーデック情報(headerFrame)を、次のpageにメタデータ(commentFrame)をいれないとだめ。
 * 他のデータとまじってもいやなので、次のようにする必要がある。
 * 
 * @author taktod
 * 
 * こちらの動作としては、つぎつぎにframeデータをいれていくが、frameデータがいっぱいになったら次のoggPageに移動しないとだめ。
 * 現在のページにつぎつぎとデータをいれていく。
 */
public class OggPageWriter implements IWriter {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContainer(IContainer container) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addFrame(int trackId, IFrame frame) throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareHeader() throws Exception {
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareTailer() throws Exception {
	}
	/**
	 * 現在扱っているページが完了したとする
	 */
	public void completePage() {
		
	}
}
