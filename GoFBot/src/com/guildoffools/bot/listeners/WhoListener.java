package com.guildoffools.bot.listeners;

import java.util.Iterator;
import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.UserListModel;

public class WhoListener extends AbstractAdminListenerAdapter
{
	private static final String WHO = "!who";

	public WhoListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (message.startsWith(WHO) && words.length > 1)
		{
			final String nick = words[1];
			final boolean present = UserListModel.getInstance().hasUser(nick);
			send(present ? nick + " is here" : "I don't see " + nick);
		}
		else if (message.startsWith(WHO))
		{
			final List<String> users = UserListModel.getInstance().getUsers();
			final StringBuilder builder = new StringBuilder();
			builder.append("I see ");
			final Iterator<String> iter = users.iterator();
			while (iter.hasNext())
			{
				builder.append(iter.next());
				if (iter.hasNext())
				{
					builder.append(", ");
				}
			}

			send(builder.toString());
		}
	}
}