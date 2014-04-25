package com.guildoffools.bot.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
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
							addUser(user);
						}
					}
				}
			}

			@Override
			public void onJoin(final JoinEvent<PircBotX> event)
			{
				addUser(event.getUser().getNick());
			}

			@Override
			public void onPart(final PartEvent<PircBotX> event)
			{
				removeUser(event.getUser().getNick());
			}

			@Override
			public void onDisconnect(final DisconnectEvent<PircBotX> event)
			{
				removeAllUsers();
			}
		});

		GoFDatabase.getInstance().addListener(new GoFDatabaseListener()
		{
			@Override
			public void userUpdated(final GoFUser user)
			{
				updateUser(user.getNick());
			}

			@Override
			public void userAdded(final GoFUser user)
			{
			}
		});
	}

	private void addUser(final String nick)
	{
		synchronized (userList)
		{
			final int existingUserIndex = getUserIndex(nick);
			if (existingUserIndex == -1)
			{
				final GoFUser user = db.getUser(nick, true);
				userList.add(user);
				fireTableRowsInserted(userList.size() - 1, userList.size() - 1);
			}
		}
	}

	private void removeUser(final String nick)
	{
		synchronized (userList)
		{
			final int indexToRemove = getUserIndex(nick);
			if (indexToRemove != -1)
			{
				userList.remove(indexToRemove);
				fireTableRowsDeleted(indexToRemove, indexToRemove);
			}
		}
	}

	private void removeAllUsers()
	{
		synchronized (userList)
		{
			userList.clear();
			fireTableDataChanged();
		}
	}

	private void updateUser(final String nick)
	{
		synchronized (userList)
		{
			final int indexToUpdate = getUserIndex(nick);
			if (indexToUpdate != -1)
			{
				final GoFUser updatedUser = db.getUser(nick, false);
				userList.set(indexToUpdate, updatedUser);
				fireTableRowsUpdated(indexToUpdate, indexToUpdate);
			}
		}
	}

	public int getUserIndex(final String nick)
	{
		int index = -1;
		synchronized (userList)
		{
			for (int i = 0; (i < userList.size()) && (index == -1); i++)
			{
				final GoFUser user = userList.get(i);
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
		synchronized (userList)
		{
			user = userList.get(index);
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
		synchronized (userList)
		{
			rowCount = userList.size();
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