package com.guildoffools.bot.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;

import com.guildoffools.bot.db.GoFDatabase;
import com.guildoffools.bot.db.GoFDatabaseListener;

public class UserListTableModel extends AbstractTableModel
{
	private final GoFDatabase db = GoFDatabase.getInstance();
	private final List<GoFUser> userList = new ArrayList<GoFUser>();

	public UserListTableModel(final PircBotX bot)
	{
		bot.getConfiguration().getListenerManager().addListener(new ListenerAdapter<PircBotX>()
		{
			@Override
			public void onServerResponse(final ServerResponseEvent<PircBotX> event)
			{
				if (event.getCode() == 353)
				{
					final List<String> fields = event.getParsedResponse();
					if (fields.size() > 3)
					{
						final String userList = fields.get(3);
						final String[] users = userList.split(" ");
						for (final String user : users)
						{
							UserListTableModel.this.addUser(user);
						}
					}
				}
			}

			@Override
			public void onJoin(final JoinEvent<PircBotX> event)
			{
				UserListTableModel.this.addUser(event.getUser().getNick());
			}

			@Override
			public void onPart(final PartEvent<PircBotX> event)
			{
				UserListTableModel.this.removeUser(event.getUser().getNick());
			}
		});
		GoFDatabase.getInstance().addListener(new GoFDatabaseListener()
		{
			@Override
			public void userUpdated(final GoFUser user)
			{
				UserListTableModel.this.updateUser(user.getNick());
			}

			@Override
			public void userAdded(final GoFUser user)
			{
			}
		});
	}

	public void addUser(final String nick)
	{
		final int existingUserIndex = getUserIndex(nick);
		if (existingUserIndex == -1)
		{
			final GoFUser user = this.db.getUser(nick, true);

			synchronized (this.userList)
			{
				this.userList.add(user);
				final int userListSize = this.userList.size();
				fireTableRowsInserted(userListSize - 1, userListSize - 1);
			}
		}
	}

	public void removeUser(final String nick)
	{
		final int indexToRemove = getUserIndex(nick);
		if (indexToRemove != -1)
		{
			synchronized (this.userList)
			{
				this.userList.remove(indexToRemove);
				fireTableRowsDeleted(indexToRemove, indexToRemove);
			}
		}
	}

	public void updateUser(final String nick)
	{
		final int indexToUpdate = getUserIndex(nick);
		if (indexToUpdate != -1)
		{
			final GoFUser updatedUser = this.db.getUser(nick, false);
			synchronized (this.userList)
			{
				this.userList.set(indexToUpdate, updatedUser);
				fireTableRowsUpdated(indexToUpdate, indexToUpdate);
			}
		}
	}

	public int getUserIndex(final String nick)
	{
		int index = -1;
		synchronized (this.userList)
		{
			for (int i = 0; (i < this.userList.size()) && (index == -1); i++)
			{
				final GoFUser user = this.userList.get(i);
				if (user.getNick().equals(nick))
				{
					index = i;
				}
			}
		}

		return index;
	}

	public GoFUser getGoFUser(final int index)
	{
		GoFUser user;
		synchronized (this.userList)
		{
			user = this.userList.get(index);
		}

		return user;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		Class<?> value;

		switch (columnIndex)
		{
			case 0:
				value = String.class;
				break;
			case 1:
			case 2:
				value = Integer.class;
				break;
			default:
				value = Object.class;
		}

		return value;
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public int getRowCount()
	{
		int rowCount;
		synchronized (this.userList)
		{
			rowCount = this.userList.size();
		}

		return rowCount;
	}

	@Override
	public Object getValueAt(final int row, final int col)
	{
		final GoFUser userRecord = getGoFUser(row);
		Object value;
		switch (col)
		{
			case 0:
				value = userRecord.getNick();
				break;
			case 1:
				value = Integer.valueOf(userRecord.getPoints());
				break;
			case 2:
				value = Integer.valueOf(userRecord.getTimeInChat());
				break;
			default:
				value = "?";
		}

		return value;
	}
}