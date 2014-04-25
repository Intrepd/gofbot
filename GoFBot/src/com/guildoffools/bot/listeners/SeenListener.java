package com.guildoffools.bot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.db.GoFDatabase;
import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.bot.model.UserListModel;

public class SeenListener extends AbstractAdminListenerAdapter
{
	private static final String SEEN = "!seen";

	public SeenListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (message.startsWith(SEEN) && words.length > 1)
		{
			final String nick = words[1];
			if (UserListModel.getInstance().hasUser(nick))
			{
				send(nick + " is here");
			}
			else
			{
				final GoFUser gofUser = GoFDatabase.getInstance().getUser(nick, false);
				if (gofUser != null)
				{
					send(nick + " last joined " + gofUser.getLastJoined());
				}
				else
				{
					send("I have never seen " + nick);
				}
			}
		}
	}
}