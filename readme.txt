タイトル：myLib

　自分が利用するためのライブラリ

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

使い方メモ：個人用

myLibでmvn deployを実行すると現状のデータをmvn-repoのプロジェクトにコピーします。
mvn-repoのデータをコミットしてpushすればよし。

<repository>
    <id>taktod-mvn-repo</id>
    <url>https://github.com/taktod/mvn-repo/tree/master/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
上記をpomに追加すればリモートリポジトリをどこからでも引っ張れるようになるはず。

リリースする場合はmvn release:prepareを実行すればサブリポジトリの番号をあげつつ、作成したプログラムのスナップショットができて、githubにデータがコミットされると思われます。
(要検証)

とりあえずこんなとこかな。

モジュールの説明：

プロジェクト作成補助
m2eHelper:
　eclipseのm2eでは、pomの命令で解釈できないものがあり、エラーになります。
　このpom(もしくは継承しているpom)を親にするとその辺りが自動的に解決されます。
　また、junit(テストのみ)とjava1.6でのコンパイルくくりもはいっています。

projectHelper:
　jsegmenterみたいな実行プログラムをリリースするのにつかえそうなbuild pluginsのパッケージ
　依存関係のあるjarファイルを出力したりtarget内にプロジェクトのデータを出力したりします。

