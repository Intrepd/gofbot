package com.guildoffools.bot.model;

import java.util.Date;

public abstract interface GoFUser
{
	public abstract String getNick();

	public abstract int getPoints();

	public abstract String getPointsString();

	public abstract Date getLastJoined();

	public abstract int getTimeInChat();
}