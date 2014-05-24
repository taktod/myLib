/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.test.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * 映像データを書き込む適当なコンポーネント
 * @author taktod
 */
public class VideoComponent extends JComponent {
	private static final long serialVersionUID = 7890183008132195859L;
	private Image image;
	private Dimension size;

	public void setImage(Image image) {
		SwingUtilities.invokeLater(new ImageRunnable(image));
	}
	public void setImageSize(Dimension newSize) {
		
	}
	private class ImageRunnable implements Runnable {
		private final Image newImage;
		public ImageRunnable(Image newImage) {
			super();
			this.newImage = newImage;
		}
		public void run() {
			VideoComponent.this.image = newImage;
			Dimension newSize = new Dimension(image.getWidth(null), image.getHeight(null));
			if(!newSize.equals(size)) {
				VideoComponent.this.size = newSize;
				// サイズがかわったことを通知してやったほうがよさそう。
			}
			repaint();
		}
	}
	public VideoComponent() {
		size = new Dimension(0, 0);
		setSize(size);
	}
	@Override
	public synchronized void paint(Graphics g) {
		if(image != null) {
			g.drawImage(image, 0, 0, this);
		}
	}
}
