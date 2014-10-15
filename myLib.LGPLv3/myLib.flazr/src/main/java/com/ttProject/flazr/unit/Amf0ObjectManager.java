/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.unit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * management for amf0Object.
 * there are two amf0Object. for myLib.container.flv and flazr.
 * @author taktod
 */
@SuppressWarnings("unchecked")
public class Amf0ObjectManager {
	/**
	 * from myLib.container.flv.AMF to flazr.AMF
	 */
	public Object toFlazrObject(Object obj) {
		if(obj instanceof com.ttProject.container.flv.amf.Amf0Object) {
			com.ttProject.container.flv.amf.Amf0Object<String, Object> amf0Object = (com.ttProject.container.flv.amf.Amf0Object<String, Object>)obj;
			com.flazr.amf.Amf0Object result = new com.flazr.amf.Amf0Object();
			for(Entry<String, Object> entry : amf0Object.entrySet()) {
				result.put(entry.getKey(), toFlazrObject(entry.getValue()));
			}
			return result;
		}
		else if(obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)obj;
			Map<String, Object> result = new LinkedHashMap<String, Object>();
			for(Entry<String, Object> entry : map.entrySet()) {
				result.put(entry.getKey(), toFlazrObject(entry.getValue()));
			}
			return result;
		}
		else if(obj instanceof List) {
			List<Object> list = (List<Object>)obj;
			List<Object> result = new ArrayList<Object>();
			for(Object element : list) {
				result.add(toFlazrObject(element));
			}
		}
		return obj;
	}
	/**
	 * from flazr.AMF to myLib.container.flv.AMF
	 */
	public Object toMyLibObject(Object obj) {
		if(obj instanceof com.flazr.amf.Amf0Object) {
			com.flazr.amf.Amf0Object amf0Object = (com.flazr.amf.Amf0Object)obj;
			com.ttProject.container.flv.amf.Amf0Object<String, Object> result = new com.ttProject.container.flv.amf.Amf0Object<String, Object>();
			for(Entry<String, Object> entry : amf0Object.entrySet()) {
				result.put(entry.getKey(), toMyLibObject(entry.getValue()));
			}
		}
		else if(obj instanceof Map) {
			Map<String, Object> map = (Map<String, Object>)obj;
			Map<String, Object> result = new LinkedHashMap<String, Object>();
			for(Entry<String, Object> entry : map.entrySet()) {
				result.put(entry.getKey(), toMyLibObject(entry.getValue()));
			}
			return result;
		}
		else if(obj instanceof List) {
			List<Object> list = (List<Object>)obj;
			List<Object> result = new ArrayList<Object>();
			for(Object element : list) {
				result.add(toMyLibObject(element));
			}
		}
		return obj;
	}
}
