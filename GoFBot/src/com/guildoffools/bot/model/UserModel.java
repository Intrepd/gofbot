package com.guildoffools.bot.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class UserModel
{
	private static final Logger log = Logger.getLogger(UserModel.class.getSimpleName());

	private static UserModel instance;

	private final List<UserModelListener> listeners = new ArrayList<>();
	private final Set<String> users = new HashSet<String>();

	public static UserModel getInstance()
	{
		if (instance == null)
		{
			synchronized (UserModel.class)
			{
				if (instance == null)
				{
					instance = new UserModel();
				}
			}
		}
		return instance;
	}

	public void addListener(final UserModelListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public void removeListener(final UserModelListener listener)
	{
		synchronized (listeners)
		{
			listeners.remove(listener);
		}
	}

	public void addUser(final String user)
	{
		synchronized (users)
		{
			users.add(user);
		}
		fireUserAdded(user);
		log.info(user + " added");
	}

	public void removeUser(final String user)
	{
		synchronized (users)
		{
			users.remove(user);
		}
		fireUserRemoved(user);
		log.info(user + " removed");
	}

	public synchronized boolean hasUser(final String user)
	{
		synchronized (users)
		{
			return users.contains(user);
		}
	}

	public void removeAllUsers()
	{
		final Set<String> usersCopy = new HashSet<>();
		synchronized (users)
		{
			usersCopy.addAll(users);
			users.clear();
		}
		log.info("All users removed");
		fireUsersRemoved();
	}

	public synchronized List<String> getUsers()
	{
		return new ArrayList<String>(users);
	}

	private void fireUserAdded(final String user)
	{
		final List<UserModelListener> listenersCopy = new ArrayList<>();
		synchronized (listeners)
		{
			listenersCopy.addAll(listeners);
		}
		for (final UserModelListener listener : listenersCopy)
		{
			listener.userAdded(user);
		}
	}

	private void fireUserRemoved(final String user)
	{
		final List<UserModelListener> listenersCopy = new ArrayList<>();
		synchronized (listeners)
		{
			listenersCopy.addAll(listeners);
		}
		for (final UserModelListener listener : listenersCopy)
		{
			listener.userRemoved(user);
		}
	}

	private void fireUsersRemoved()
	{
		final List<UserModelListener> listenersCopy = new ArrayList<>();
		synchronized (listeners)
		{
			listenersCopy.addAll(listeners);
		}
		for (final UserModelListener listener : listenersCopy)
		{
			listener.usersRemoved();
		}
	}
}