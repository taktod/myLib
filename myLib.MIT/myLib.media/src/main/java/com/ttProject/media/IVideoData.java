package com.ttProject.media;

/**
 * 映像の生データに追加することで、必要なデータを参照できるようにします。
 * 具体的にはh264やvp8、flv等となります。
 * @author taktod
 * audioとは違い、連続的にあるデータではないので、xuggleやffmpegの出力、ファイルデータのpts、dts値をそのまま使えば問題ないと思われます。
 */
public interface IVideoData extends IMediaData {

}
