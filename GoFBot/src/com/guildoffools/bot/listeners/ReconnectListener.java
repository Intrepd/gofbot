package com.guildoffools.bot.listeners;

import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class ReconnectListener extends AbstractAdminListenerAdapter
{
	private static final Logger log = Logger.getLogger(ReconnectListener.class.getName());
	private static final String RECONNECT = "!reconnect";

	public ReconnectListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();

		if (message.startsWith(RECONNECT))
		{
			log.info(event.getUser().getNick() + " requested that the bot reconnect.");
			bot.sendIRC().message(settings.getChannel(), settings.getNick() + " is reconnecting.");
			bot.sendIRC().quitServer("Reconnecting...");
		}
	}
}