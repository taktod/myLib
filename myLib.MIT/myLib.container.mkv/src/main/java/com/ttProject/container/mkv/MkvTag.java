package com.ttProject.container.mkv;

import java.nio.ByteBuffer;

import com.ttProject.container.Container;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.EbmlValue;

/**
 * mkvデータのTagの基本動作
 * @author taktod
 */
public abstract class MkvTag extends Container {
	private final EbmlValue id;
	private final EbmlValue size;
	private MkvTagReader reader = null;
	/**
	 * コンストラクタ
	 * @param id
	 * @param size
	 */
	public MkvTag(Type id, EbmlValue size) {
		this.id = new EbmlValue();
		this.id.setLong(Type.getValue(id));
		this.size = size;
		super.setSize((int)(size.getLong() + (this.id.getBitCount() + this.size.getBitCount()) / 8));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		setPosition(channel.position() - (id.getBitCount() + size.getBitCount()) / 8);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		channel.position(getPosition() + getSize());
	}
	/**
	 * mkv解析用Readerを外部から設定します。
	 * @param reader
	 */
	public void setMkvTagReader(MkvTagReader reader) {
		this.reader = reader;
	}
	/**
	 * mp4の解析用readerを参照します
	 * @return
	 */
	protected MkvTagReader getMkvTagReader() {
		return reader;
	}
	/**
	 * 先頭のbufferを応答します。
	 * @return
	 */
	protected ByteBuffer getHeaderBuffer() {
		BitConnector connector = new BitConnector();
		return connector.connect(id, size);
	}
	/**
	 * 内容の大きさを応答
	 * @return
	 */
	protected int getMkvSize() {
		return size.get();
	}
}
