package com.guildoffools.bot.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener
{
	private final JTable table;
	private final int spacing;
	private boolean isColumnHeaderIncluded;
	private boolean isColumnDataIncluded;
	private boolean isOnlyAdjustLarger;
	private boolean isDynamicAdjustment;
	private final Map<TableColumn, Integer> columnSizes = new HashMap<TableColumn, Integer>();

	public TableColumnAdjuster(final JTable table)
	{
		this(table, 6);
	}

	public TableColumnAdjuster(final JTable table, final int spacing)
	{
		this.table = table;
		this.spacing = spacing;
		setColumnHeaderIncluded(true);
		setColumnDataIncluded(true);
		setOnlyAdjustLarger(true);
		setDynamicAdjustment(false);
	}

	public void adjustColumns()
	{
		final TableColumnModel tcm = this.table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			adjustColumn(i);
		}
	}

	public void adjustColumn(final int column)
	{
		final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
		{
			return;
		}

		final int columnHeaderWidth = getColumnHeaderWidth(column);
		final int columnDataWidth = getColumnDataWidth(column);
		final int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

		updateTableColumn(column, preferredWidth);
	}

	private int getColumnHeaderWidth(final int column)
	{
		if (!this.isColumnHeaderIncluded)
		{
			return 0;
		}

		final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
		final Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null)
		{
			renderer = this.table.getTableHeader().getDefaultRenderer();
		}

		final Component c = renderer.getTableCellRendererComponent(this.table, value, false, false, -1, column);
		return c.getPreferredSize().width;
	}

	private int getColumnDataWidth(final int column)
	{
		if (!this.isColumnDataIncluded)
		{
			return 0;
		}

		int preferredWidth = 0;
		final int maxWidth = this.table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < this.table.getRowCount(); row++)
		{
			preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));

			if (preferredWidth >= maxWidth)
			{
				break;
			}
		}

		return preferredWidth;
	}

	private int getCellDataWidth(final int row, final int column)
	{
		final TableCellRenderer cellRenderer = this.table.getCellRenderer(row, column);
		final Component c = this.table.prepareRenderer(cellRenderer, row, column);
		final int width = c.getPreferredSize().width + this.table.getIntercellSpacing().width;

		return width;
	}

	private void updateTableColumn(final int column, int width)
	{
		final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
		{
			return;
		}

		width += this.spacing;

		if (this.isOnlyAdjustLarger)
		{
			width = Math.max(width, tableColumn.getPreferredWidth());
		}

		this.columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));
		this.table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}

	public void restoreColumns()
	{
		final TableColumnModel tcm = this.table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++)
		{
			restoreColumn(i);
		}
	}

	private void restoreColumn(final int column)
	{
		final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
		final Integer width = this.columnSizes.get(tableColumn);

		if (width != null)
		{
			this.table.getTableHeader().setResizingColumn(tableColumn);
			tableColumn.setWidth(width.intValue());
		}
	}

	public void setColumnHeaderIncluded(final boolean isColumnHeaderIncluded)
	{
		this.isColumnHeaderIncluded = isColumnHeaderIncluded;
	}

	public void setColumnDataIncluded(final boolean isColumnDataIncluded)
	{
		this.isColumnDataIncluded = isColumnDataIncluded;
	}

	public void setOnlyAdjustLarger(final boolean isOnlyAdjustLarger)
	{
		this.isOnlyAdjustLarger = isOnlyAdjustLarger;
	}

	public void setDynamicAdjustment(final boolean isDynamicAdjustment)
	{
		if (this.isDynamicAdjustment != isDynamicAdjustment)
		{
			if (isDynamicAdjustment)
			{
				this.table.addPropertyChangeListener(this);
				this.table.getModel().addTableModelListener(this);
			}
			else
			{
				this.table.removePropertyChangeListener(this);
				this.table.getModel().removeTableModelListener(this);
			}
		}

		this.isDynamicAdjustment = isDynamicAdjustment;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e)
	{
		if ("model".equals(e.getPropertyName()))
		{
			TableModel model = (TableModel) e.getOldValue();
			model.removeTableModelListener(this);

			model = (TableModel) e.getNewValue();
			model.addTableModelListener(this);
			adjustColumns();
		}
	}

	@Override
	public void tableChanged(final TableModelEvent e)
	{
		if (!this.isColumnDataIncluded)
		{
			return;
		}

		if (e.getType() == 0)
		{
			final int column = this.table.convertColumnIndexToView(e.getColumn());

			if (this.isOnlyAdjustLarger)
			{
				final int row = e.getFirstRow();
				final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

				if (tableColumn.getResizable())
				{
					final int width = getCellDataWidth(row, column);
					updateTableColumn(column, width);
				}

			}
			else
			{
				adjustColumn(column);
			}

		}
		else
		{
			adjustColumns();
		}
	}

	class ToggleAction extends AbstractAction
	{
		private final boolean isToggleDynamic;
		private final boolean isToggleLarger;

		public ToggleAction(final boolean isToggleDynamic, final boolean isToggleLarger)
		{
			this.isToggleDynamic = isToggleDynamic;
			this.isToggleLarger = isToggleLarger;
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			if (this.isToggleDynamic)
			{
				TableColumnAdjuster.this.setDynamicAdjustment(!TableColumnAdjuster.this.isDynamicAdjustment);
				return;
			}

			if (this.isToggleLarger)
			{
				TableColumnAdjuster.this.setOnlyAdjustLarger(!TableColumnAdjuster.this.isOnlyAdjustLarger);
				return;
			}
		}
	}

	class ColumnAction extends AbstractAction
	{
		private final boolean isSelectedColumn;
		private final boolean isAdjust;

		public ColumnAction(final boolean isSelectedColumn, final boolean isAdjust)
		{
			this.isSelectedColumn = isSelectedColumn;
			this.isAdjust = isAdjust;
		}

		@Override
		public void actionPerformed(final ActionEvent e)
		{
			if (this.isSelectedColumn)
			{
				final int[] columns = TableColumnAdjuster.this.table.getSelectedColumns();

				for (int i = 0; i < columns.length; i++)
				{
					if (this.isAdjust)
					{
						TableColumnAdjuster.this.adjustColumn(columns[i]);
					}
					else
					{
						TableColumnAdjuster.this.restoreColumn(columns[i]);
					}

				}

			}
			else if (this.isAdjust)
			{
				TableColumnAdjuster.this.adjustColumns();
			}
			else
			{
				TableColumnAdjuster.this.restoreColumns();
			}
		}
	}
}