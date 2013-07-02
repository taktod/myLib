mediaを継承しているプロジェクトは次のようにしておく。

・各コンテナ(mp3 mp4 flv mkv ts)の最小単位となるものをUnitの継承クラスにする。
positionは各unitがファイル上で始まる位置を
sizeは各unitのファイル上でのサイズを示すものとする。



・解析インターフェイスを実装しておく。IAnalyzerをimplementsしているクラスとする。
