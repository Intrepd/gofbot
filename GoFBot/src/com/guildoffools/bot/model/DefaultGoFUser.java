package com.guildoffools.bot.model;

import java.util.Date;

import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

public class DefaultGoFUser implements GoFUser, Cloneable
{
	public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
	public static final String[] HEADER = { "nick", "points", "lastJoined", "timeInChat", "highGod", "castsJoined" };
	public static final CellProcessor[] PROCESSORS = { null, new ParseInt(), new ParseDate("EEE MMM dd HH:mm:ss zzz yyyy"), new ParseInt(), new ParseBool(),
			new ParseInt() };

	private String nick;
	private int points;
	private Date lastJoined;
	private int timeInChat;
	private boolean highGod;
	private int castsJoined;

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
		return nick;
	}

	public void setNick(final String nick)
	{
		this.nick = nick;
	}

	@Override
	public Date getLastJoined()
	{
		return lastJoined;
	}

	public void setLastJoined(final Date lastJoined)
	{
		this.lastJoined = lastJoined;
	}

	@Override
	public int getPoints()
	{
		return points;
	}

	public void setPoints(final int points)
	{
		this.points = points;
	}

	@Override
	public String getPointsString()
	{
		return new StringBuilder().append(points).append(" god point").append(Math.abs(points) == 1 ? "" : "s").toString();
	}

	@Override
	public int getTimeInChat()
	{
		return timeInChat;
	}

	public void setTimeInChat(final int timeInChat)
	{
		this.timeInChat = timeInChat;
	}

	@Override
	public boolean isHighGod()
	{
		return highGod;
	}

	public void setHighGod(final boolean highGod)
	{
		this.highGod = highGod;
	}

	@Override
	public int getCastsJoined()
	{
		return castsJoined;
	}

	public void setCastsJoined(final int castsJoined)
	{
		this.castsJoined = castsJoined;
	}

	@Override
	public Object clone()
	{
		DefaultGoFUser clonedObject = null;
		try
		{
			clonedObject = (DefaultGoFUser) super.clone();

			if (nick != null)
			{
				clonedObject.nick = new String(nick);
			}
			if (lastJoined != null)
			{
				clonedObject.lastJoined = new Date(lastJoined.getTime());
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
		result = prime * result + castsJoined;
		result = prime * result + (highGod ? 1231 : 1237);
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

		boolean equivalent = points == other.points;
		equivalent &= timeInChat == other.timeInChat;
		equivalent &= highGod == other.highGod;
		equivalent &= castsJoined == other.castsJoined;

		if (equivalent)
		{
			equivalent &= (((nick == null) && (other.nick == null)) || ((nick != null) && (nick.equals(other.nick))));
			equivalent &= (((lastJoined == null) && (other.lastJoined == null)) || ((lastJoined != null) && (lastJoined.equals(other.lastJoined))));
		}

		return equivalent;
	}
}