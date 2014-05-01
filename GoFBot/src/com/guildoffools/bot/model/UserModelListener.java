package com.guildoffools.bot.model;

public interface UserModelListener
{
	public void userAdded(String nick);

	public void userRemoved(String nick);

	public void usersRemoved();
}
