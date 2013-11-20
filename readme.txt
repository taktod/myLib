タイトル：myLib

　自分が利用するためのライブラリ

ライセンス：MITライセンスとします。

 もともとLGPLv3のつもりでつくっていましたが、MITライセンスとします。
 コードは基本MITライセンスですが、他のライブラリを利用した場合は、そのライセンスに準拠するものとします。
 例：xuggleをつかえばGPLv3

下準備：

1:maven2(maven3でもOK)とjava1.6以降、gitを準備します。
2:myLibをcloneしてきます。
 $ git clone git://github.com/taktod/myLib.git
3:myLib.m2eHelperのライブラリをインストールします
 $ cd myLib/myLib.m2eHelper
 $ mvn install
4:myLibをいれます。
 $ cd ..
 $ mvn install

使い方：

 maven2とjava(開発ではjava6をつかっています。)が必要ですので入手してください。
 pom.xmlのあるディレクトリに移動してmavenのコマンドを実行してください。

作者情報：

 taktod
 twitter: http://twitter.com/taktod/
 blog: http://poepoemix.blogspot.jp/

ライブラリの説明

構成は次のようになっています。

各ライブラリの説明

 myLib:全部含んだライブラリ
 
 myLib.MIT:MITライセンスのみの構成の部分
  myLib.channels:入出力動作関連
  myLib.chunk:塊としてメディアデータを扱う
   myLib.chunk.aac:aac用(HLS)
   myLib.chunk.mp3:mp3用(HLS)
   myLib.chunk.mpegts:mpegts用(HLS)
    myLib.chunk.mpegts.h264:mpegts用h264データから作成拡張
  myLib.convert:変換絡み
   myLib.convert.ffmpeg:外部プロセスとしてpipelineでデータをやり取りするコンバート動作
  myLib.jmx:jmxを扱う
  myLib.log4j:log4jの動作用拡張
  myLib.m2eHelper:pomの動作補助、よく使うものをまとめてある。
  myLib.media:メディアデータ扱い用
   myLib.media.aac:aac用
   myLib.media.h264:h264データ用
   myLib.media.mp3:mp3用
   myLib.media.flv:flv用
   myLib.media.mp4:mp4用
    myLib.media.extra:mp4とflvの相互利用
   myLib.media.raw:生データ用
   myLib.media.mpegts:mpegts用
  myLib.packet:塊としてメディアデータを扱う(旧)
   myLib.packet.flv:前つくったhttpTakStreaming用(旧)
   myLib.packet.mp3:mp3用(HLS)(旧)
   myLib.packet.mpegts:mpegts用(HLS)(旧)
  myLib.productHelper:実行プログラムとして吐くプロジェクト用のpomの動作補助
  myLib.segment:HLSの分割ファイル用(旧)
  myLib.swing:swingの動作補助
  myLib.util:データを扱うときの補助プログラム
 
 myLib.LGPLv3:LGPLv3ライセンスの部分
  myLib.flazr:flazrのコードの動作補助(rtmpのメッセージの受信、送信関連)
 
 myLib.GPLv3:GPLv3ライセンスの部分
  myLib.setup:テスト用のメディアデータをxuggleで自動生成するためだけのプロジェクト
  myLib.xuggle.flv:flvのデータとxuggleの仲介
  myLib.xuggle.raw:生データとxuggleの仲介
 
 myLib.GAPLv3:AGPLv3ライセンスの部分
 
 MITライセンス以外はつかっているライブラリのライセンスに依存しています。
