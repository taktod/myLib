package com.ttProject.media.flv.amf;

import java.util.LinkedHashMap;

/**
 * AMF0のオブジェクト用のデータ
 * 内容はマップと同じだけど、Amf0Valueに通したときにAMF0のオブジェクトとしてのデータをかえす。
 * @author taktod
 *
 * @param <K>
 * @param <V>
 */
public class Amf0Object<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 5706263714148888484L;
}
