package com.ttProject.container.adts;

import com.ttProject.container.Reader;

/**
 * unitのselector
 * @author taktod
 */
public class AdtsUnitReader extends Reader {
	public AdtsUnitReader() {
		super(new AdtsUnitSelector());
	}
}
