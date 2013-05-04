タイトル：myLib

自分が利用するためのライブラリ
とりあえずmp4解析動作をぱぱっと書いてみた。

ライセンス：LGPL

ライセンスはあとで変更するかもしれませんが、とりあえずLGPLとします。

使い方：

maven2とjava(開発ではjava6をつかっています。)が必要ですので入手してください。
pom.xmlのあるディレクトリに移動してmavenのコマンドを実行してください。

１：とりあえず動作させてみる。(junitテスト)
$ mvn test
２：自分のローカルレポに登録する。
$ mvn install
３：jarファイルをつくる
$ mvn package

この３つくらいあれば十分かと思います。

作者情報：

taktod
twitter: http://twitter.com/taktod/
blog: http://poepoemix.blogspot.jp/
