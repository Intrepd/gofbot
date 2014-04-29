package com.guildoffools.bot.model;

import java.util.Date;

public interface GoFUser
{
	public String getNick();

	public int getPoints();

	public String getPointsString();

	public Date getLastJoined();

	public int getTimeInChat();

	public boolean isHighGod();

	public int getCastsJoined();
}