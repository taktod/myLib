package com.ttProject.container.ogg;

/**
 * oggはoggPageというものができているらしい。
 * @see http://www.xiph.org/vorbis/doc/framing.html
 * @see http://ja.wikipedia.org/wiki/Ogg%E3%83%9A%E3%83%BC%E3%82%B8
 * 内容は次のような感じ
 * pageの開始
 * 4バイト:OggS
 * 1バイト:stream_structure_version (現在は0x00のみ)
 * 1バイト:bitFlag 0000 0abc c:フラグが立っていたら続きpacket b:フラグがたっていたらロジックストリームの開始のページ a:フラグがたっていたらロジックストリームの最後のページ
 * 16バイト:absoluteGranulePosition 位置情報(含有物次第の値らしい)
 * 4バイト:streamSerialNumber とりあえずなにがしの番号
 * 4バイト:pageSequenceNo ページの番号 mpegtsのcounterみたいなもんかな
 * 4バイト:pageChecksum headerから導くCRC値らしい
 * 1バイト:ページが保持するsegmentsの数
 * 以下セグメントデータ
 *  1バイト:セグメントサイズ(Nとする) ←segmentsの数だけならぶ
 *  Nバイト:セグメント実体 ←segmentsの数だけならぶ
 * みたいな感じになってる。
 * 
 * 以下これの繰り返しっぽい。
 * avconvでつくったoggデータの確認しつつやってみた結果。
 * どこか正しくないことがあっても怒らないこと。
 * 
 * @author taktod
 */
public class OggPage {

}
