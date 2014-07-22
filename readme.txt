タイトル：myLib

　自分が利用するためのライブラリ

ライセンス：MITライセンスとします。

 もともとLGPLv3のつもりでつくっていましたが、MITライセンスとします。
 コードは基本MITライセンスですが、他のライブラリを利用した場合は、そのライセンスに準拠するものとします。
 例：xuggleをつかえばGPLv3

下準備：

1:maven3とjava1.6以降、gitを準備します。
maven2でも動作しますが、remoteリポジトリの設定が合いません。
2:myLibをcloneしてきます。
 $ git clone git://github.com/taktod/myLib.git
3:myLibをコンパイルします。
 $ cd myLib
 $ mvn package install

使い方：

 maven2とjava(開発ではjava6をつかっています。)が必要ですので入手してください。
 pom.xmlのあるディレクトリに移動してmavenのコマンドを実行してください。

 各プロジェクトの使い方はtestコードみてもらえればだいたいわかると思います。

作者情報：

 taktod
 twitter: http://twitter.com/taktod/
 blog: http://poepoemix.blogspot.jp/

ライブラリの説明

構成は次のようになっています。

各ライブラリの説明

 myLib:全部含んだライブラリ
 
 myLib.MIT:MITライセンスのみの構成の部分
 myLib.LGPLv3:LGPLv3ライセンスの部分
 myLib.GPLv3:GPLv3ライセンスの部分
 myLib.GAPLv3:AGPLv3ライセンスの部分(humble-videoをいれる予定ですが、コンパイルとおらないので、とりあえずパス)

 myLib.GPLv3/myLib.setup:テストコード動作用メディアデータ自動生成プロジェクト
 myLib.MIT/myLib.channels:ファイルを扱うための入力チャンネル動作
 myLib.MIT/myLib.util:動作補助関連
 myLib.MIT/myLib.segment:分割後のファイルを扱う動作
 myLib.MIT/myLib.jmx:jmx動作補助
 myLib.MIT/myLib.log4j:log4j動作補助
 myLib.MIT/myLib.media:mediaデータを扱う
 myLib.MIT/myLib.media.mp3:mp3データを扱う
 myLib.MIT/myLib.media.flv:flvデータを扱う
 myLib.MIT/myLib.media.mp4:mp4データを扱う
 myLib.MIT/myLib.media.mkv:mkvデータを扱う(webmも含む)
 myLib.MIT/myLib.media.mpegts:mpegtsデータを扱う
 myLib.MIT/myLib.media.aac:aacデータを扱う
 myLib.MIT/myLib.media.h264:h264データを扱う
 myLib.MIT/myLib.media.extra:mp4+flvの相互拡張
 myLib.MIT/myLib.media.raw:生データを扱う
 myLib.GPLv3/myLib.media.xuggle:xuggleのpacketデータを扱う
 myLib.MIT/myLib.swing:swing動作補助
 myLib.MIT/myLib.chunk:データを塊として扱うchunk動作
 myLib.MIT/myLib.chunk.mpegts:mpegtsベースのhttpLiveStreaming用
 myLib.MIT/myLib.chunk.mpegts.h264:chunk.mpegtsのh264読み込み支援
 myLib.MIT/myLib.chunk.mpegts.flv:chunk.mpegtsのflv読み込み支援
 myLib.MIT/myLib.chunk.mp3:mp3ベースのhttpLiveStreaming用
 myLib.MIT/myLib.chunk.aac:aacベースのhttpLiveStreaming用
 myLib.MIT/myLib.transcode:変換支援
 myLib.MIT/myLib.transcode.ffmpeg:ffmpegベースの変換
 myLib.MIT/myLib.transcode.ffmpeg.flv:flv読み込み支援
 myLib.GPLv3/myLib.transcode.xuggle:xuggleベースの変換
 myLib.GPLv3/myLib.transcode.xuggle.flv:flv読み込み支援
 myLib.GPLv3/myLib.transcode.xuggle.h264:h264読み込み支援
 myLib.GPLv3/myLib.transcode.xuggle.aac:aac読み込み支援
 myLib.GPLv3/myLib.transcode.xuggle.mp3:mp3読み込み支援
 myLib.LGPLv3/myLib.flazr:flazrの利用支援

あとで消す候補
 myLib.MIT/myLib.packet:データを塊として扱うpacket動作
 myLib.MIT/myLib.packet.flv:httpTakStreaming用
 myLib.MIT/myLib.packet.mpegts:httpLiveStreaming用
 myLib.MIT/myLib.packet.mp3:httpLiveStreamingのmp3のみ用
 myLib.MIT/myLib.convert:コンバート関連
 myLib.MIT/myLib.convert.ffmpeg:コンバート関連ffmpeg系
 myLib.GPLv3/myLib.convert.xuggle:コンバート関連xuggle系
 myLib.GPLv3/myLib.xuggle:xuggle動作
 myLib.GPLv3/myLib.xuggle.flv:xuggle動作flv関連連携
 myLib.GPLv3/myLib.xuggle.raw:xuggle動作生データ関連連携

 MITライセンス以外はつかっているライブラリのライセンスに依存しています。

今後の予定
 opusまわりの処理をなんとかしておきたい。
 coder系の処理をつくって、データ圧縮とかも自在にできるようにしたい。
 その他いろいろ


テスト動作が遅いプロジェクトメモ
myLib.setup(11秒) これは変換元のデータをつくっているから仕方ない
myLib.xuggle(2秒)
myLib.xuggle.flv(15秒)
myLib.container.mkv(6秒)
myLib.container.mpegts(14秒)
myLib.container.webm(11秒)
myLib.container.test(52秒)
myLib.xuggle.test(4秒)

こんなところ。
