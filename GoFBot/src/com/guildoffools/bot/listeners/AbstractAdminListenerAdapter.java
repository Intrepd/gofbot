package com.guildoffools.bot.listeners;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public abstract class AbstractAdminListenerAdapter extends AbstractListenerAdapter
{
	public AbstractAdminListenerAdapter(final PircBotX bot)
	{
		super(bot);
	}

	protected abstract void onAdminMessage(final MessageEvent<PircBotX> event);

	@Override
	public void onMessage(final MessageEvent<PircBotX> event)
	{
		if (settings.isAdmin(event.getUser().getNick()))
		{
			onAdminMessage(event);
		}
	}
}
