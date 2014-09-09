/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test.swing;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class TestFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private VideoComponent video = null;
	/**
	 * This is the default constructor
	 */
	public TestFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(320, 240);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
//			jContentPane.add(getJTextPane(), BorderLayout.CENTER);
			jContentPane.add(getVideoComponent(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
//	private JTextPane getJTextPane() {
//		if (jTextPane == null) {
//			jTextPane = new JTextPane();
//		}
//		return jTextPane;
//	}
	public VideoComponent getVideoComponent() {
		if(video == null) {
			video = new VideoComponent();
		}
		return video;
	}

}
