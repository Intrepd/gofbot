package com.guildoffools.bot.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class UserListModel
{
	private static final Logger log = Logger.getLogger(UserListModel.class.getSimpleName());

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
		log.info(user + " added");
		users.add(user);
	}

	public synchronized void removeUser(final String user)
	{
		log.info(user + " removed");
		users.remove(user);
	}

	public synchronized boolean hasUser(final String user)
	{
		return users.contains(user);
	}

	public synchronized void removeAllUsers()
	{
		log.info("cleared");
		users.clear();
	}

	public synchronized List<String> getUsers()
	{
		return new ArrayList<String>(users);
	}
}