package com.guildoffools.bot.model;

import java.util.Date;

import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class DefaultGoFUser implements GoFUser, Cloneable
{
	public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
	public static final String[] HEADER = { "nick", "points", "lastJoined", "timeInChat" };
	public static final CellProcessor[] PROCESSORS = { null, new ParseInt(), new ParseDate("EEE MMM dd HH:mm:ss zzz yyyy"), new ParseInt() };

	private String nick;
	private int points;
	private Date lastJoined;
	private int timeInChat;

	public DefaultGoFUser()
	{
	}

	public DefaultGoFUser(final String nick)
	{
		this.nick = nick;
		lastJoined = new Date();
		points = 1;
	}

	@Override
	public String getNick()
	{
		return this.nick;
	}

	public void setNick(final String nick)
	{
		this.nick = nick;
	}

	@Override
	public Date getLastJoined()
	{
		return this.lastJoined;
	}

	public void setLastJoined(final Date lastJoined)
	{
		this.lastJoined = lastJoined;
	}

	@Override
	public int getPoints()
	{
		return this.points;
	}

	public void setPoints(final int points)
	{
		this.points = points;
	}

	@Override
	public String getPointsString()
	{
		return new StringBuilder().append(this.points).append(" god point").append(Math.abs(this.points) == 1 ? "" : "s").toString();
	}

	@Override
	public int getTimeInChat()
	{
		return this.timeInChat;
	}

	public void setTimeInChat(final int timeInChat)
	{
		this.timeInChat = timeInChat;
	}

	@Override
	public Object clone()
	{
		DefaultGoFUser clonedObject = null;
		try
		{
			clonedObject = (DefaultGoFUser) super.clone();

			if (this.nick != null)
			{
				clonedObject.nick = new String(this.nick);
			}
			if (this.lastJoined != null)
			{
				clonedObject.lastJoined = new Date(this.lastJoined.getTime());
			}
		}
		catch (final CloneNotSupportedException e)
		{
		}

		return clonedObject;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastJoined == null) ? 0 : lastJoined.hashCode());
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		result = prime * result + points;
		result = prime * result + timeInChat;
		return result;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (this == object)
		{
			return true;
		}

		if ((object == null) || (object.getClass() != getClass()))
		{
			return false;
		}

		final DefaultGoFUser other = (DefaultGoFUser) object;

		boolean equivalent = this.points == other.points;
		equivalent &= this.timeInChat == other.timeInChat;

		if (equivalent)
		{
			equivalent &= (((this.nick == null) && (other.nick == null)) || ((this.nick != null) && (this.nick.equals(other.nick))));
			equivalent &= (((this.lastJoined == null) && (other.lastJoined == null)) || ((this.lastJoined != null) && (this.lastJoined.equals(other.lastJoined))));
		}

		return equivalent;
	}
}