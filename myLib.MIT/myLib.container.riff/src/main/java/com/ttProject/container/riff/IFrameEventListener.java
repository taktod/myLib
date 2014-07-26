/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff;

import com.ttProject.frame.IFrame;

/**
 * フレームを取り出したときのレポート動作
 * @author taktod
 * Frameの取得をRiffのunitベースにしない理由は、
 * riffの場合１つのデータユニットが全フレームを保持していることがあります。
 * よって、データユニットの読み込みを待つと全データを読み込まないと処理が開始できません。
 * 
 * そこで、listenerという形をとることで、全体の読み込みを完了しなくても、処理できるようになっています。
 */
public interface IFrameEventListener {
	/**
	 * dataの解析動作中にframeを見つけたら実施されるイベント
	 * @param frame
	 */
	public void onNewFrame(IFrame frame);
}
