package com.ttProject.swing.component;

import java.awt.Container;

import javax.swing.GroupLayout;
import javax.swing.JComponent;

/**
 * groupLayoutの作成補助
 */
public class GroupLayoutEx extends GroupLayout {
	/**
	 * コンストラクタ
	 * @param comp
	 */
	public GroupLayoutEx(Container comp) {
		super(comp);
	}
	/**
	 * オブジェクトの設定
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