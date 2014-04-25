package com.guildoffools.bot.model;

import java.util.HashSet;
import java.util.Set;

public class UserListModel
{
	private static UserListModel instance;

	private final Set<String> users = new HashSet<String>();

	public static UserListModel getInstance()
	{
		if (instance == null)
		{
			synchronized (UserListModel.class)
			{
				if (instance == null)
				{
					instance = new UserListModel();
				}
			}
		}
		return instance;
	}

	public synchronized void addUser(final String user)
	{
		users.add(user);
	}

	public synchronized void removeUser(final String user)
	{
		users.remove(user);
	}

	public synchronized boolean hasUser(final String user)
	{
		return users.contains(user);
	}

	public synchronized void removeAllUsers()
	{
		users.clear();
	}

	public synchronized String[] getUsers()
	{
		return users.toArray(new String[0]);
	}
}