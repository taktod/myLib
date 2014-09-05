/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.type;

import com.ttProject.container.mkv.MkvMasterTag;
import com.ttProject.container.mkv.Type;
import com.ttProject.unit.extra.EbmlValue;

/**
 * EBMLタグ
 * @author taktod
 */
public class EBML extends MkvMasterTag {
	/**
	 * コンストラクタ
	 * @param size
	 */
	public EBML(EbmlValue size) {
		super(Type.EBML, size);
	}
	/**
	 * コンストラクタ
	 */
	public EBML() {
		super(Type.EBML, new EbmlValue());
	}
	/**
	 * 書き込み用のセットアップ処理
	 * @param version
	 * @param readVersion
	 * @param docType
	 * @param docTypeVersion
	 * @param docTypeReadVersion
	 */
	public void setup(int version, int readVersion, String dType , int dTypeVersion, int dTypeReadVersion) throws Exception {
		EBMLVersion ebmlVersion = new EBMLVersion();
		ebmlVersion.setValue(version);
		addChild(ebmlVersion);
		
		EBMLReadVersion ebmlReadVersion = new EBMLReadVersion();
		ebmlReadVersion.setValue(readVersion);
		addChild(ebmlReadVersion);
		
		EBMLMaxIDLength ebmlMaxIDLength = new EBMLMaxIDLength();
		ebmlMaxIDLength.setValue(4);
		addChild(ebmlMaxIDLength);
		
		EBMLMaxSizeLength ebmlMaxSizeLength = new EBMLMaxSizeLength();
		ebmlMaxSizeLength.setValue(8);
		addChild(ebmlMaxSizeLength);
		
		DocType docType = new DocType();
		docType.setValue(dType);
		addChild(docType);
		
		DocTypeVersion docTypeVersion = new DocTypeVersion();
		docTypeVersion.setValue(dTypeVersion);
		addChild(docTypeVersion);
		
		DocTypeReadVersion docTypeReadVersion = new DocTypeReadVersion();
		docTypeReadVersion.setValue(dTypeReadVersion);
		addChild(docTypeReadVersion);
	}
}
