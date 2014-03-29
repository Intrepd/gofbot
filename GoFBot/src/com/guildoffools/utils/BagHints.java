/*******************************************************************************
 * Copyright (c) 2012 Triveni Digital, Inc. All rights reserved.
 *******************************************************************************/

package com.guildoffools.utils;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class BagHints extends GridBagConstraints
{
	private static final long serialVersionUID = 1L;

	public BagHints()
	{
		fill = GridBagConstraints.BOTH;
		anchor = GridBagConstraints.CENTER;
		insets = new Insets(5, 5, 5, 5);
	}

	public void set(final int x, final int y, final int w, final int h, final double wx, final double wy)
	{
		gridx = x;
		gridy = y;
		gridwidth = w;
		gridheight = h;
		weightx = wx;
		weighty = wy;
	}

	public void set(final int x, final int y, final int w, final int h, final double wx, final double wy, final Insets i)
	{
		set(x, y, w, h, wx, wy);
		insets = i;
	}

	public void set(final int x, final int y, final int w, final int h, final double wx, final double wy, final int f, final int a)
	{
		set(x, y, w, h, wx, wy);
		fill = f;
		anchor = a;
	}

	public void set(final int x, final int y, final int w, final int h, final double wx, final double wy, final int f, final int a, final Insets i)
	{
		set(x, y, w, h, wx, wy);
		fill = f;
		anchor = a;
		insets = i;
	}

	public void reset()
	{
		gridx = 0;
		gridy = 0;
		gridwidth = 1;
		gridheight = 1;
		weightx = 0.0;
		weighty = 0.0;
		fill = BOTH;
		anchor = CENTER;
		insets = new Insets(5, 5, 5, 5);
	}
}
