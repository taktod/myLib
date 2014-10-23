/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.extra.AudioMultiFrame;
import com.ttProject.frame.speex.sub.NarrowUnit;
import com.ttProject.frame.speex.sub.SubUnit;
import com.ttProject.frame.speex.sub.WideUnit;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitConnector;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit2;
import com.ttProject.unit.extra.bit.Bit3;
import com.ttProject.unit.extra.bit.Bit4;
import com.ttProject.unit.extra.bit.Bit5;
import com.ttProject.unit.extra.bit.Bit6;
import com.ttProject.unit.extra.bit.Bit7;

/**
 * selector for speex frame.
 * expect to get in order.
 * headerFrame -> commentFrame -> dataFrame.
 * @author taktod
 */
public class SpeexFrameSelector extends AudioSelector {
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SpeexFrameSelector.class);
	/** headerFrame */
	private HeaderFrame headerFrame = null;
	/** commentFrame */
	private CommentFrame commentFrame = null;
	/**
	 * set the header frame.
	 * (set from out side)
	 * ex: flv has only one kind of header.
	 * flv doesn't have specific byte informaton on it.
	 * @param frame
	 */
	public void setHeaderFrame(HeaderFrame frame) {
		this.headerFrame = frame;
	}
	/**
	 * set the comment frame.
	 * same as header frame.
	 * @param frame
	 */
	public void setCommentFrame(CommentFrame frame) {
		this.commentFrame = frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		SpeexFrame frame = null;
		if(headerFrame == null) {
			// try to get header frame.
			frame = new HeaderFrame();
			headerFrame = (HeaderFrame)frame;
		}
		else if(commentFrame == null) {
			// next try to get comment frame.
			frame = new CommentFrame();
			commentFrame = (CommentFrame)frame;
		}
		else {
			/*
			 * speex frame側で処理すると、bitが中途になったときに、次のframeをうまく処理できなくなることがあるみたいです。
			 */
			return getFrameData(channel);
//			frame = new Frame();
//			frame.setHeaderFrame(headerFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
	/**
	 * get the speexFrame(can be audioMultiFrame)
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	private IAudioFrame getFrameData(IReadChannel channel) throws Exception {
		List<SubUnit> unitList = new ArrayList<SubUnit>();
		BitLoader loader = new BitLoader(channel);
		AudioMultiFrame multiFrame = new AudioMultiFrame();
		try {
			while(true) {
				Bit1 firstBit = new Bit1();
				loader.load(firstBit);
				SubUnit unit = null;
				switch(firstBit.get()) {
				case 0:
					// data for frame is ready.
					multiFrame.addFrame(makeFrame(unitList));
					unit = new NarrowUnit();
					break;
				case 1:
				default:
					unit = new WideUnit();
					break;
				}
				unit.load(loader);
				unitList.add(unit);
			}
		}
		catch(Exception e) {
			multiFrame.addFrame(makeFrame(unitList));
		}
		if(multiFrame.getFrameList().size() == 1) {
			return multiFrame.getFrameList().get(0);
		}
		return multiFrame;
	}
	/**
	 * make minimumUnit of speexFrame.
	 * @param unitList
	 * @return
	 * @throws Exception
	 */
	private Frame makeFrame(List<SubUnit> unitList) throws Exception {
		if(unitList.size() == 0) {
			return null;
		}
		int size = 0;
		BitConnector connector = new BitConnector();
		for(SubUnit su : unitList) {
			size += su.getBitCount();
			connector.feed(su.getBitList());
		}
		switch(size % 8) {
		case 1:connector.feed(new Bit7(0x3F));break;
		case 2:connector.feed(new Bit6(0x1F));break;
		case 3:connector.feed(new Bit5(0x0F));break;
		case 4:connector.feed(new Bit4(0x07));break;
		case 5:connector.feed(new Bit3(0x03));break;
		case 6:connector.feed(new Bit2(0x01));break;
		case 7:connector.feed(new Bit1(0x00));break;
		case 0:
		default:
			break;
		}
		Frame frame = new Frame();
		setup(frame);
		frame.setHeaderFrame(headerFrame);
		frame.minimumLoad(new ByteReadChannel(connector.connect()));
		unitList.clear();
		return frame;
	}
}
