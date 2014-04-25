package com.guildoffools.bot.listeners;

import java.util.Timer;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;

import com.guildoffools.bot.db.GoFDatabase;
import com.guildoffools.bot.db.GoFSettings;

public abstract class AbstractListenerAdapter extends ListenerAdapter<PircBotX>
{
	protected static final Timer timer = new Timer("Listener Timer", true);

	protected final GoFDatabase db = GoFDatabase.getInstance();
	protected final GoFSettings settings = GoFSettings.getInstance();
	protected final PircBotX bot;

	public AbstractListenerAdapter(final PircBotX bot)
	{
		this.bot = bot;
	}

	protected void send(final String message)
	{
		bot.sendIRC().message(settings.getChannel(), message);
	}
}