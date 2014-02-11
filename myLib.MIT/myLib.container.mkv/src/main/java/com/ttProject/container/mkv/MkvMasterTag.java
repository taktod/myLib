package com.ttProject.container.mkv;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.EbmlValue;

/**
 * 他のTagを内包するTagの動作
 * @author taktod
 */
public abstract class MkvMasterTag extends MkvTag {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(MkvMasterTag.class);
	/** 保持タグリスト */
	private List<MkvTag> childTags = new ArrayList<MkvTag>();
	/**
	 * コンストラクタ
	 * @param tag
	 * @param size
	 */
	public MkvMasterTag(Type id, EbmlValue size) {
		super(id, size);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		int targetSize = getMkvSize();
		IContainer container = null;
		while(targetSize > 0 && (container = getMkvTagReader().read(channel)) != null) {
			targetSize -= container.getSize();
			if(container instanceof MkvTag) {
				childTags.add((MkvTag)container);
			}
		}
		super.load(channel);
	}
	public List<MkvTag> getChildList() {
		return new ArrayList<MkvTag>(childTags);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space));
		data.append("*");
		return data.toString();
	}
}
