/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.adpcmswf;

/**
 * こっちのframeに関してはadpcmの仕様上、1frameあたりのsample数が計算可能。
 * adpcmはベースデータから、次の位置の差分を計算するための4bitがはいっている形になっています。
 * stereoの場合4bit + 4bitで1byteあたり1sample
 * xx aa aa bb bb のこりがdiff
 * monoの場合4bitなので、1byteあたり2sampleはいっていることになります。
 * xx aa aa のこりがdiff
 * というわけでbyte数からframeあたりのsample数が割り出せます。
 * 
 * いまのところ1frameあたり2048samplesのデータしかみたことないです。
 * それ以上のデータが入る可能性が一応ありますが・・・ほぼ確実に計算できます。
 * @author taktod
 *
 */
public class Frame {

}
