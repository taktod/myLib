/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.swing.component;

import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JComponent;

/**
 * groupLayout helper
 */
public class GroupLayoutEx extends GroupLayout {
	/**
	 * constructor
	 * @param comp
	 */
	public GroupLayoutEx(Container comp) {
		super(comp);
	}
	/**
	 * set components
	 * @param components
	 */
	public void addComponents(Object[][] components) {
		int yCount = components.length;
		int xCount = components[0].length;
		{
			SequentialGroup hg = createSequentialGroup();
			for(int x = 0;x < xCount;x ++) {
				ParallelGroup pg = createParallelGroup();
				for(int y = 0;y< yCount;y ++) {
					if(components[y][x] instanceof JComponent) {
						pg.addComponent((JComponent)components[y][x]);
					}
					else if(components[y][x] instanceof JComponent[]) {
						JComponent[] comps = (JComponent[]) components[y][x];
						SequentialGroup g = createSequentialGroup();
						for(JComponent comp : comps) {
							g.addComponent(comp);
							g.addGap(0);
						}
						pg.addGroup(g);
					}
				}
				hg.addGroup(pg);
			}
			setHorizontalGroup(hg);
		}
		{
			SequentialGroup vg = createSequentialGroup();
			for(int y = 0;y < yCount;y ++) {
				ParallelGroup pg = createParallelGroup(Alignment.BASELINE);
				for(int x = 0;x < xCount;x ++) {
					if(components[y][x] instanceof JComponent) {
						pg.addComponent((JComponent)components[y][x]);
					}
					else if(components[y][x] instanceof JComponent[]) {
						JComponent[] comps = (JComponent[]) components[y][x];
						for(JComponent comp : comps) {
							pg.addComponent(comp);
						}
					}
				}
				vg.addGroup(pg);
			}
			setVerticalGroup(vg);
		}
	}
}
