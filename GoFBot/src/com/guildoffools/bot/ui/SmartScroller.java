package com.guildoffools.bot.ui;

import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class SmartScroller implements AdjustmentListener
{
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	public static final int START = 0;
	public static final int END = 1;
	private final int viewportPosition;
	private JScrollBar scrollBar;
	private boolean adjustScrollBar = true;

	private int previousValue = -1;
	private int previousMaximum = -1;

	public SmartScroller(final JScrollPane scrollPane)
	{
		this(scrollPane, 1, 1);
	}

	public SmartScroller(final JScrollPane scrollPane, final int viewportPosition)
	{
		this(scrollPane, 1, viewportPosition);
	}

	public SmartScroller(final JScrollPane scrollPane, final int scrollDirection, final int viewportPosition)
	{
		if ((scrollDirection != 0) && (scrollDirection != 1))
		{
			throw new IllegalArgumentException("invalid scroll direction specified");
		}

		if ((viewportPosition != 0) && (viewportPosition != 1))
		{
			throw new IllegalArgumentException("invalid viewport position specified");
		}

		this.viewportPosition = viewportPosition;

		if (scrollDirection == 0)
		{
			this.scrollBar = scrollPane.getHorizontalScrollBar();
		}
		else
		{
			this.scrollBar = scrollPane.getVerticalScrollBar();
		}

		this.scrollBar.addAdjustmentListener(this);

		final Component view = scrollPane.getViewport().getView();

		if ((view instanceof JTextComponent))
		{
			final JTextComponent textComponent = (JTextComponent) view;
			final DefaultCaret caret = (DefaultCaret) textComponent.getCaret();
			caret.setUpdatePolicy(1);
		}
	}

	@Override
	public void adjustmentValueChanged(final AdjustmentEvent e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SmartScroller.this.checkScrollBar(e);
			}
		});
	}

	private void checkScrollBar(final AdjustmentEvent e)
	{
		final JScrollBar scrollBar = (JScrollBar) e.getSource();
		final BoundedRangeModel listModel = scrollBar.getModel();
		int value = listModel.getValue();
		final int extent = listModel.getExtent();
		final int maximum = listModel.getMaximum();

		final boolean valueChanged = this.previousValue != value;
		final boolean maximumChanged = this.previousMaximum != maximum;

		if ((valueChanged) && (!maximumChanged))
		{
			if (this.viewportPosition == 0)
			{
				this.adjustScrollBar = (value != 0);
			}
			else
			{
				this.adjustScrollBar = (value + extent >= maximum);
			}

		}

		if ((this.adjustScrollBar) && (this.viewportPosition == 1))
		{
			scrollBar.removeAdjustmentListener(this);
			value = maximum - extent;
			scrollBar.setValue(value);
			scrollBar.addAdjustmentListener(this);
		}

		if ((this.adjustScrollBar) && (this.viewportPosition == 0))
		{
			scrollBar.removeAdjustmentListener(this);
			value = value + maximum - this.previousMaximum;
			scrollBar.setValue(value);
			scrollBar.addAdjustmentListener(this);
		}

		this.previousValue = value;
		this.previousMaximum = maximum;
	}
}