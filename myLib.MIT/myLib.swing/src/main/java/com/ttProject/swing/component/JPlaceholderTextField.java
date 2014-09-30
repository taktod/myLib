/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.swing.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * textField with placeHolder
 * @see http://stackoverflow.com/questions/16213836/java-swing-jtextfield-set-placeholder
 * @author taktod
 */
public class JPlaceholderTextField extends JTextField {
	private static final long serialVersionUID = 8838799910774222615L;
	private String placeholder = null;
	public JPlaceholderTextField() {
	}
	public JPlaceholderTextField(
			final Document pDoc,
			final String pText,
			final int pColumns) {
		super(pDoc, pText, pColumns);
	}
	public JPlaceholderTextField(final String pText) {
		super(pText);
	}
	public JPlaceholderTextField(final int pColumns) {
		super(pColumns);
	}
	public JPlaceholderTextField(final String pText, final int pColumns) {
		super(pText, pColumns);
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	@Override
	protected void paintComponent(Graphics pG) {
		super.paintComponent(pG);
		if(placeholder.length() == 0 || getText().length() > 0) {
			return;
		}
		final Graphics2D g = (Graphics2D)pG;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getDisabledTextColor());
		g.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
	}
}
