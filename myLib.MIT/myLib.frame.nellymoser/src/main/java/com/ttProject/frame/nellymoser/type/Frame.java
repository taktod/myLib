package com.ttProject.frame.nellymoser.type;

import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.Bit5;
import com.ttProject.unit.extra.Bit6;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.BitN;
import com.ttProject.unit.extra.BitN.Bit64;

/**
 * nellyMoserのframe
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
 * @author taktod
 */
public class Frame extends NellymoserFrame {
	private Bit6 initTableIndex = new Bit6();
	private Bit5[] deltaTable = new Bit5[22];
	private BitN payload1 = new BitN(new Bit64(), new Bit64(), new Bit64(), new Bit6());
	private BitN payload2 = new BitN(new Bit64(), new Bit64(), new Bit64(), new Bit6());
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setReadPosition(channel.position());
		super.setSize(64);
		super.setSampleNum(256);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		BitLoader loader = new BitLoader(channel);
		loader.load(initTableIndex);
		for(int i = 0;i < 22;i ++) {
			deltaTable[i] = new Bit5();
		}
		loader.load(deltaTable);
		loader.load(payload1);
		loader.load(payload2);
		super.update();
	}
	@Override
	public void setBit(int bit) {
		super.setBit(bit);
	}
	@Override
	public void setChannel(int channel) {
		super.setChannel(channel);
	}
	@Override
	public void setPts(long pts) {
		super.setPts(pts);
	}
	@Override
	public void setSampleRate(int sampleRate) {
		super.setSampleRate(sampleRate);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		// ここでbitconnectorが繰り返して追記ができるようにしないとだめっぽい・・・
	}
}
