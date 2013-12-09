package com.ttProject.container.flv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.Unit;

public class AggregateTag extends Unit {
	/** 集合タグの内部リスト */
	private List<FlvTag> tagList = new ArrayList<FlvTag>();
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
	}
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
	public void add(FlvTag tag) {
		tagList.add(tag);
	}
	public List<FlvTag> getList() {
		return new ArrayList<FlvTag>(tagList);
	}
	public int count() {
		return tagList.size();
	}
}
