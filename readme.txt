myLib

自分が利用するためのライブラリ
とりあえずmp4解析動作をぱぱっと書いてみた。

ライセンスはあとで変更するかもしれませんが、とりあえずLGPLとします。

1:簡易的な音声offにするには
ftyp[majorBrand:3gp6][minorVersion:256][compatibleBrand:isom 3gp6]
moov[size:0x3f365e][position:0x18][
  mvhd[size:0x6c][position:0x20]
  iods[size:0x15][position:0x8c]
  trak[size:0x14f470][position:0xa1][ ←tkhdの内容から動画データであるのでこのtrakをfreeに変更すればよい
    tkhd[size:0x5c][position:0xa9][width:320 height:240 volume:0]
    mdia[size:0x14f40c][position:0x105][
      mdhd[size:0x20][position:0x10d]
      hdlr[size:0x4c][position:0x12d]
      minf[size:0x14f398][position:0x179][
        vmhd[size:0x14][position:0x181]
        dinf[size:0x24][position:0x195][
          dref[size:0x1c][position:0x19d]
        ]
        stbl[size:0x14f358][position:0x1b9][
          stsd[size:0xb8][position:0x1c1]
          stts[size:0x18][position:0x279]
          stss[size:0x3528][position:0x291]
          stsc[size:0x1c][position:0x37b9]
          stsz[size:0xa5ea0][position:0x37d5]
          stco[size:0xa5e9c][position:0xa9675]
        ]
      ]
    ]
  ]
  trak[size:0x2a3e6f][position:0x14f511][
    tkhd[size:0x5c][position:0x14f519][width:0 height:0 volume:256]
    mdia[size:0x2a3e0b][position:0x14f575][
      mdhd[size:0x20][position:0x14f57d]
      hdlr[size:0x4c][position:0x14f59d]
      minf[size:0x2a3d97][position:0x14f5e9][
        smhd[size:0x10][position:0x14f5f1]
        dinf[size:0x24][position:0x14f601][
          dref[size:0x1c][position:0x14f609]
        ]
        stbl[size:0x2a3d5b][position:0x14f625][
          stsd[size:0x5b][position:0x14f62d]
          stts[size:0x18][position:0x14f688]
          stsc[size:0x98a1c][position:0x14f6a0]
          stsz[size:0x165428][position:0x1e80bc]
          stco[size:0xa5e9c][position:0x34d4e4]
        ]
      ]
    ]
  ]
  udta[size:0x2f6][position:0x3f3380]
]
mdat[size:0x8edb6b2][position:0x3f3676]

tkhdを調査することができれば、あとはそのままproxyすればよい

2:メディアデータごと落とすには
ftyp[majorBrand:3gp6][minorVersion:256][compatibleBrand:isom 3gp6]
moov[size:0x3f365e][position:0x18][
  mvhd[size:0x6c][position:0x20]
  iods[size:0x15][position:0x8c]
  trak[size:0x14f470][position:0xa1][ // データごと消す、ただしstco(co64)の内容をみて、詰めるデータ量を知る必要あり
    tkhd[size:0x5c][position:0xa9][width:320 height:240 volume:0]
    mdia[size:0x14f40c][position:0x105][
      mdhd[size:0x20][position:0x10d]
      hdlr[size:0x4c][position:0x12d]
      minf[size:0x14f398][position:0x179][
        vmhd[size:0x14][position:0x181]
        dinf[size:0x24][position:0x195][
          dref[size:0x1c][position:0x19d]
        ]
        stbl[size:0x14f358][position:0x1b9][
          stsd[size:0xb8][position:0x1c1]
          stts[size:0x18][position:0x279]
          stss[size:0x3528][position:0x291]
          stsc[size:0x1c][position:0x37b9]
          stsz[size:0xa5ea0][position:0x37d5]
          stco[size:0xa5e9c][position:0xa9675]
        ]
      ]
    ]
  ]
  trak[size:0x2a3e6f][position:0x14f511][ // stcoを動画データと比較して、再計算する必要あり。
    tkhd[size:0x5c][position:0x14f519][width:0 height:0 volume:256]
    mdia[size:0x2a3e0b][position:0x14f575][
      mdhd[size:0x20][position:0x14f57d]
      hdlr[size:0x4c][position:0x14f59d]
      minf[size:0x2a3d97][position:0x14f5e9][
        smhd[size:0x10][position:0x14f5f1]
        dinf[size:0x24][position:0x14f601][
          dref[size:0x1c][position:0x14f609]
        ]
        stbl[size:0x2a3d5b][position:0x14f625][
          stsd[size:0x5b][position:0x14f62d]
          stts[size:0x18][position:0x14f688]
          stsc[size:0x98a1c][position:0x14f6a0]
          stsz[size:0x165428][position:0x1e80bc]
          stco[size:0xa5e9c][position:0x34d4e4]
        ]
      ]
    ]
  ]
  udta[size:0x2f6][position:0x3f3380]
]
mdat[size:0x8edb6b2][position:0x3f3676] // stcoの計算により省かれるデータを抜いたデータに書き換える必要あり。

動画と音声のstco(co64の場合もあり)を解析してmoovの内容を変更するのとmdatの応答を調整すればよい。
