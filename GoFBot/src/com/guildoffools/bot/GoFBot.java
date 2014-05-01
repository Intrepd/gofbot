package com.guildoffools.bot;

import java.awt.GraphicsEnvironment;
import java.io.File;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

import com.guildoffools.bot.db.GoFSettings;
import com.guildoffools.bot.listeners.AddRemoveGodPointListener;
import com.guildoffools.bot.listeners.DieListener;
import com.guildoffools.bot.listeners.GodPointListener;
import com.guildoffools.bot.listeners.IRCListener;
import com.guildoffools.bot.listeners.JoinListener;
import com.guildoffools.bot.listeners.ReconnectListener;
import com.guildoffools.bot.listeners.RestartListener;
import com.guildoffools.bot.listeners.RollListener;
import com.guildoffools.bot.listeners.SeenListener;
import com.guildoffools.bot.listeners.TimeListener;
import com.guildoffools.bot.listeners.UserModelListener;
import com.guildoffools.bot.listeners.WhoListener;
import com.guildoffools.bot.ui.MainFrame;

public class GoFBot
{
	private static final String LOG_DIR = "logs/";

	private final PircBotX bot;

	public GoFBot() throws Exception
	{
		new File(LOG_DIR).mkdirs();

		final GoFSettings settings = GoFSettings.getInstance();
		final Configuration.Builder<PircBotX> builder = new Configuration.Builder<PircBotX>();
		builder.setName(settings.getNick());
		builder.setServerHostname(settings.getServer());
		builder.setServerPassword(settings.getOAuth());
		builder.addAutoJoinChannel(settings.getChannel());
		builder.setAutoReconnect(true);
		builder.setMessageDelay(2000);

		final Configuration<PircBotX> config = builder.buildConfiguration();
		bot = new PircBotX(config);
		bot.getConfiguration().getListenerManager().addListener(new AddRemoveGodPointListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new IRCListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new UserModelListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new JoinListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new TimeListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new GodPointListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new ReconnectListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new RestartListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new RollListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new SeenListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new DieListener(bot));
		bot.getConfiguration().getListenerManager().addListener(new WhoListener(bot));

		if (!GraphicsEnvironment.isHeadless())
		{
			new MainFrame(bot);
		}
		bot.startBot();
	}

	public static void main(final String args[]) throws Exception
	{
		new GoFBot();
	}
}
