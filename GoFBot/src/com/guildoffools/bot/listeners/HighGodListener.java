package com.guildoffools.bot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;

public class HighGodListener extends AbstractAdminListenerAdapter
{
	private static final String HIGH_GOD = "!highgod";

	public HighGodListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (message.startsWith(HIGH_GOD) && words.length > 1)
		{
			final String nick = words[1];
			final GoFUser gofUser = db.getUser(nick, false);
			if (gofUser != null)
			{
				((DefaultGoFUser) gofUser).setHighGod(!gofUser.isHighGod());
				db.updateUser(gofUser);
				send(gofUser.isHighGod() ? nick + " deified." : nick + " is now a peasant.");
			}
			else
			{
				send("I don't know " + nick);
			}
		}
	}
}