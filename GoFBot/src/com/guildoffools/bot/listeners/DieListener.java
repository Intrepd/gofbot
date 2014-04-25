package com.guildoffools.bot.listeners;

import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.db.GoFDatabase;

public class DieListener extends AbstractAdminListenerAdapter
{
	private static final Logger log = Logger.getLogger(DieListener.class.getName());
	private static final String DIE = "!die";

	private boolean dieing;

	public DieListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onAdminMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();

		if (message.startsWith(DIE))
		{
			log.info(event.getUser().getNick() + " requested that the bot die.");
			dieing = true;
			send(settings.getNick() + " is departing.");
			GoFDatabase.getInstance().mailDatabase();
			bot.stopBotReconnect();
			bot.sendIRC().quitServer();
		}
	}

	@Override
	public void onDisconnect(final DisconnectEvent<PircBotX> event)
	{
		if (dieing)
		{
			log.info("Disconnected while dieing, terminating.");
			System.exit(0);
		}
	}
}