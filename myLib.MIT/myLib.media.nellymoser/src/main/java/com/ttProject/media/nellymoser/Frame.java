package com.ttProject.media.nellymoser;

/**
 * nellymoserのframe
 * @see http://wiki.multimedia.cx/index.php?title=Nelly_Moser
 * nellymoserのframeは1つあたり
 * header + payload + payloadの組み合わせになっています。
 * 最少単位は0x40で構成されるみたいです。
 * header部が6bit(initTableIndex) + 22個の5bit(deltaTable) = 116bit
 * payloadは198bit、これが２つとなります。
 * 116 + 198 + 198 = 512bit -> 64byte -> 0x40となります。
 * flvの場合はnellymoserはモノラルのみらしいです。
 * また、flvのaudioTagには、このデータが1,2,4個含む形ではいっているとのことです。
 * 上記のwikiより
 * 
 * よってsample数をみたいなら、0x40の塊の数 x 256で割り出せることになります。
 * nelly16 nelly8の場合はmonoral強制ですが、そのほかの場合はstereoも仕様上は作成可能っぽいです。
 * その場合0x40がベースになるか0x80がベースになるかは未調査です。
 * 
 * @author taktod
 */
public class Frame {

}
