package com.ttProject.frame.aac;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.Data;
import com.ttProject.unit.extra.Bit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit24;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * aacのdecode specific infoのデータから
 * @see http://wiki.multimedia.cx/index.php?title=MPEG-4_Audio
 * これ・・・扱い的にはglobalHeaderらしい
 * @author taktod
 */
public class DecoderSpecificInfo extends Data {
	private Bit5  objectType1          = new Bit5(); // profileの事
	private Bit6  objectType2          = null; // objectTypeが31の場合
	private Bit4  frequencyIndex       = new Bit4(); // samplingFrequenceIndexと同じ
	private Bit24 frequency            = null; // indexが15の場合
	private Bit4  channelConfiguration = new Bit4();
	private Bit   fillBit              = null;
	// こいつらいらない気がする。
//	private Bit1 frameLengthFlag = new Bit1(); // 0:each packetcontains 1024 samples 1:960 samples
//	private Bit1 dependsOnCoreCoder = new Bit1();
//	private Bit1 extensionFlag = new Bit1();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		// 特に読み込むものなし。
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		int position = channel.position();
		BitLoader loader = new BitLoader(channel);
		loader.load(objectType1);
		if(objectType1.get() == 31) {
			objectType2 = new Bit6();
			loader.load(objectType2);
		}
		loader.load(frequencyIndex);
		if(frequencyIndex.get() == 15) {
			frequency = new Bit24();
			loader.load(frequency);
		}
		loader.load(channelConfiguration);
		fillBit = loader.getExtraBit();
		super.setSize(channel.position() - position);
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		BitConnector connector = new BitConnector();
		super.setData(connector.connect(objectType1, objectType2, frequencyIndex,
				frequency, channelConfiguration, fillBit));
	}
	/**
	 * objectType参照
	 * @return
	 */
	public int getObjectType() {
		return objectType1.get();
	}
	/**
	 * frequencyIndex参照
	 * @return
	 */
	public int getFrequencyIndex() {
		return frequencyIndex.get();
	}
	/**
	 * channel数参照
	 * @return
	 */
	public int getChannelConfiguration() {
		return channelConfiguration.get();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder("decoderSpecificInfo:");
		data.append(" ot:").append(objectType1);
		data.append(" fi:").append(frequencyIndex);
		data.append(" cc:").append(channelConfiguration);
		return data.toString();
	}
}
