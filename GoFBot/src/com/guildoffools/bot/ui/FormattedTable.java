package com.guildoffools.bot.ui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

public class FormattedTable extends JXTable
{
	protected final TableColumnAdjuster tableColumnAdjuster = new TableColumnAdjuster(this);

	protected FormattedTable(final TableModel tableModel)
	{
		this(tableModel, 0);
	}

	protected FormattedTable(final TableModel tableModel, final int selectionMode)
	{
		super(tableModel);

		this.tableHeader.setFont(this.tableHeader.getFont().deriveFont(1));
		this.tableHeader.setReorderingAllowed(false);

		setAutoscrolls(true);
		setEditable(false);
		setAutoCreateRowSorter(true);
		setAutoResizeMode(4);
		setHighlighters(new Highlighter[] { HighlighterFactory.createSimpleStriping(HighlighterFactory.GENERIC_GRAY) });
		setFillsViewportHeight(true);
		setHorizontalScrollEnabled(true);
		setRowSelectionAllowed(true);
		setSelectionMode(selectionMode);
		setRowMargin(0);
		setRowHeight(30);
		setShowGrid(false);
		setShowHorizontalLines(false);
		setShowVerticalLines(false);
		setIntercellSpacing(new Dimension(0, 0));
	}

	@Override
	public void scrollRectToVisible(final Rectangle aRect)
	{
		if (getAutoscrolls())
		{
			super.scrollRectToVisible(aRect);
		}
	}
}