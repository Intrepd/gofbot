package com.guildoffools.bot.listeners;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;

import com.guildoffools.bot.db.GoFSettings;
import com.guildoffools.bot.model.DefaultGoFUser;

public class JoinListener extends AbstractListenerAdapter
{
	private static final Logger log = Logger.getLogger(JoinListener.class.getSimpleName());
	private static final int TWELVE_HOURS = 43200000;

	public JoinListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onServerResponse(final ServerResponseEvent<PircBotX> event)
	{
		if (event.getCode() == ReplyConstants.RPL_NAMREPLY)
		{
			final List<String> fields = event.getParsedResponse();
			if (fields.size() > 3)
			{
				final String userList = fields.get(3);
				final String[] nicks = userList.split(" ");
				for (final String nick : nicks)
				{
					handleJoin(nick, event.getTimestamp());
				}
			}
		}
	}

	@Override
	public void onJoin(final JoinEvent<PircBotX> event)
	{
		handleJoin(event.getUser().getNick(), event.getTimestamp());
	}

	private void handleJoin(final String nick, final long timestamp)
	{
		final DefaultGoFUser gofUser = (DefaultGoFUser) db.getUser(nick, true);
		final Date lastJoined = gofUser.getLastJoined();
		if (timestamp - lastJoined.getTime() > TWELVE_HOURS)
		{
			gofUser.setPoints(gofUser.getPoints() + 1);
			log.info(nick + " earned a point for joining the cast, last seen " + lastJoined + ", " + gofUser.getPointsString() + ".");
			if (gofUser.getPoints() >= GoFSettings.HIGH_GOD_THRESHOLD)
			{
				bot.sendIRC().message(settings.getChannel(), "High God " + nick + " has arrived.");
			}
		}
		gofUser.setLastJoined(new Date(timestamp));
		db.updateUser(gofUser);
	}
}