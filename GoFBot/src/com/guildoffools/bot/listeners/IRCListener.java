package com.guildoffools.bot.listeners;

import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.hooks.events.SocketConnectEvent;

public class IRCListener extends AbstractListenerAdapter
{
	private static final Logger log = Logger.getLogger(IRCListener.class.getName());

	public IRCListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onSocketConnect(final SocketConnectEvent<PircBotX> event)
	{
		log.info("Socket Connected.");
	}

	@Override
	public void onConnect(final ConnectEvent<PircBotX> event)
	{
		log.info("Connected.");
	}

	@Override
	public void onDisconnect(final DisconnectEvent<PircBotX> event)
	{
		log.info("Disconnected.");
		final Exception e = event.getDisconnectException();
		if (e != null)
		{
			log.info("Exception " + e);
		}
	}

	@Override
	public void onServerResponse(final ServerResponseEvent<PircBotX> event)
	{
		log.info("Server Message: " + event.getRawLine());
		if (event.getCode() == ReplyConstants.RPL_ENDOFNAMES)
		{
			bot.sendIRC().message(settings.getChannel(), settings.getNick() + " is online.");
		}
	}
}