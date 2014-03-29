package com.guildoffools.bot.ui;

import java.awt.Dimension;

import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class UserListTable extends FormattedTable
{
	public UserListTable(final TableModel tableModel)
	{
		super(tableModel, 0);

		int columnIndex = 0;

		TableColumn column = this.columnModel.getColumn(columnIndex++);
		column.setPreferredWidth(100);
		column.setHeaderValue("Nick");

		column = this.columnModel.getColumn(columnIndex++);
		column.setPreferredWidth(50);
		column.setHeaderValue("Points");

		column = this.columnModel.getColumn(columnIndex++);
		column.setPreferredWidth(25);
		column.setHeaderValue("Time");

		this.tableColumnAdjuster.adjustColumns();

		getRowSorter().toggleSortOrder(0);
		setPreferredScrollableViewportSize(new Dimension(this.columnModel.getTotalColumnWidth(), 200));
	}
}