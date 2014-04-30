package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioAnalyzer;

public class AdpcmImaWavAnalyzer extends AudioAnalyzer  {
	public AdpcmImaWavAnalyzer() {
		super(new AdpcmImaWavSelector());
	}
}
