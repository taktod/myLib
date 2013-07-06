ffmpegにpipeライン接続でデータを流して動作させる。
linuxやそれ互換のサーバーで動作するものとするので、win機ではうまく動作しないと思う。

とりあえずjavaのprocessBuilderでは、標準出力をjavaから受け取ることが可能ではあるが
連続的にうけとって順次処理するということができないので次のようにします。

１：processBuilderでjavaプロセス | ffmpegというパイプをつくる。
２：元プロセスをサーバーとして、javaプロセスをクライアントとしてnettyで接続させる。
３：ffmpegの吐くデータをprocessBuilderの標準入力としてうけとる。

ということをさせます。

cvlcでコピーする場合のコンバートパラメーターのメモ

cvlc screen:// :screen-fps=15 :live-caching=30 --sout '#transcode{vcodec=FLV1}:std{access=file,mux=flv,dst=-}' | ffmpeg -y -i - -an -vcodec copy -f flv output.flv

とりあえず、これで動作できた。
h.264の入出力は、なんかあるっぽい。

cvlc v4l2:// :v4l2-standard= :live-caching=30 --sout '#transcode{vcodec=H264,venc=x264{profile=baseline,level=3.0,nocabac,nobframes,ref=1},vb=1560,scale=1,aspect=4:3,padd=true,vfilter=canvas{width=1024,height=768}}:std{access=file,mux=flv,dst=-}' | ffmpeg -y -i - -an -vcodec copy -f flv output.flv

