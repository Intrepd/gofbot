package com.guildoffools.bot.listeners;

import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class RestartListener extends AbstractAdminListenerAdapter
{
	private static final Logger log = Logger.getLogger(RestartListener.class.getName());
	private static final String RESTART = "!restart";

	public RestartListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();

		if (message.startsWith(RESTART))
		{
			log.info(event.getUser().getNick() + " requested that the bot restart.");
			send(settings.getNick() + " is restarting.");
			bot.stopBotReconnect();
			bot.sendIRC().quitServer();
			System.exit(1);
		}
	}
}