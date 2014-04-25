package com.guildoffools.bot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;

public class AddRemoveGodPointListener extends AbstractAdminListenerAdapter
{
	private static final String ADD_GP = "!addgp";
	private static final String REMOVE_GP = "!removegp";

	public AddRemoveGodPointListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (((message.startsWith(ADD_GP)) || (message.startsWith(REMOVE_GP))) && (words.length > 1))
		{
			final String nick = words[1].toLowerCase();
			int pointsModifier = message.startsWith(ADD_GP) ? 1 : -1;
			try
			{
				pointsModifier *= Integer.parseInt(words[2]);
			}
			catch (final Exception e)
			{
			}
			final GoFUser user = db.getUser(nick, false);
			if (user != null)
			{
				((DefaultGoFUser) user).setPoints(user.getPoints() + pointsModifier);
				db.updateUser(user);
				send(nick + " now has " + user.getPointsString() + ".");
			}
			else
			{
				send("I don't know " + nick + ".");
			}
		}
	}
}