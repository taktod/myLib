package com.ttProject.container;

import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;

/**
 * ファイルの読み込みを実行する動作
 * @author taktod
 */
public abstract class Reader implements IReader {
	/** 動作selector */
	private final ISelector selector;
	/**
	 * コンストラクタ
	 * @param selector
	 */
	public Reader(ISelector selector) {
		this.selector = selector;
	}
	/**
	 * selector参照
	 * @return
	 */
	protected ISelector getSelector() {
		return selector;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IContainer read(IReadChannel channel) throws Exception {
		IContainer container = (IContainer)selector.select(channel);
		if(container != null) {
			container.load(channel);
		}
		return container;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IContainer> getRemainData() throws Exception {
		return null;
	}
}
