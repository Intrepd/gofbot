package com.guildoffools.bot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.GoFUser;

public class GodPointListener extends AbstractListenerAdapter
{
	private static final String GP = "!gp";

	public GodPointListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (message.startsWith(GP))
		{
			final String nick = words.length > 1 ? words[1].toLowerCase() : event.getUser().getNick();
			final GoFUser user = db.getUser(nick, false);
			if (user != null)
			{
				final int points = user.getPoints();
				final StringBuilder builder = new StringBuilder();
				builder.append(new StringBuilder().append(nick).append(" has ").append(points).append(" god point").toString());
				if (points > 1)
				{
					builder.append("s");
				}
				builder.append(".");
				send(builder.toString());
			}
		}
	}
}