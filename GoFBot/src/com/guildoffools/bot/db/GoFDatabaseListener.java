package com.guildoffools.bot.db;

import com.guildoffools.bot.model.GoFUser;

public abstract interface GoFDatabaseListener
{
	public abstract void userAdded(GoFUser paramGoFUser);

	public abstract void userUpdated(GoFUser paramGoFUser);
}