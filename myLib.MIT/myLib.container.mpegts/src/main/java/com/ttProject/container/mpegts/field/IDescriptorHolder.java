package com.ttProject.container.mpegts.field;

/**
 * descriptorを保持しているfieldのinterface
 * @author taktod
 */
public interface IDescriptorHolder {
	/**
	 * descriptorのデータが変更されたときに呼び出される動作
	 */
	public void updateSize();
}
