package com.guildoffools.bot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.bot.model.UserListTableModel;
import com.guildoffools.utils.BagHints;

public class UserListPanel extends JPanel
{
	private final JButton editButton = new JButton("Edit");
	private final UserListTable userListTable;

	public UserListPanel(final JFrame owner, final UserListTableModel userTableModel)
	{
		super(new GridBagLayout());

		setBorder(new TitledBorder("User List"));
		setPreferredSize(new Dimension(300, 0));
		setMinimumSize(new Dimension(300, 0));

		userListTable = new UserListTable(userTableModel);
		userListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(final ListSelectionEvent e)
			{
				updateButtons();
			}
		});

		editButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final int selectedRow = userListTable.getSelectedRow();
				if (selectedRow >= 0)
				{
					final GoFUser user = userTableModel.getGoFUser(userListTable.convertRowIndexToModel(selectedRow));
					new EditGodPointDialog(owner, user);
				}
			}
		});
		final JScrollPane scrollPane = new JScrollPane(userListTable);

		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row++, 1, 1, 1.0D, 1.0D);
		add(scrollPane, hints);
		hints.set(0, row++, 1, 1, 0.0D, 0.0D, 0, 13);
		add(editButton, hints);

		updateButtons();
	}

	public UserListTable getTable()
	{
		return userListTable;
	}

	private void updateButtons()
	{
		editButton.setEnabled(userListTable.getSelectedRow() != -1);
	}
}