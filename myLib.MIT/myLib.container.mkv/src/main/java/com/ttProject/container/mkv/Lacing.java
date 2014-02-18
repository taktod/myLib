package com.ttProject.container.mkv;

/**
 * lacingのデータ用enum
 * @author taktod
 */
public enum Lacing {
	No(0),
	Xiph(1),
	EBML(3),
	FixedSize(2);
	private int value;
	private Lacing(int value) {
		this.value = value;
	}
	public int intValue() {
		return value;
	}
	public static Lacing getType(int value) throws Exception {
		for(Lacing t : values()) {
			if(t.value == value) {
				return t;
			}
		}
		throw new Exception("lacingデータが解析できませんでした。:" + value);
	}
}
