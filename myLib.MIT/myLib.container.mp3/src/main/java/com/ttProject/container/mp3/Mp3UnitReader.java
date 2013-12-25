package com.ttProject.container.mp3;

import com.ttProject.container.Reader;

/**
 * unitのselector
 * @author taktod
 */
public class Mp3UnitReader extends Reader {
	public Mp3UnitReader() {
		super(new Mp3UnitSelector());
	}
}
