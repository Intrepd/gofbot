package com.guildoffools.bot.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.guildoffools.utils.IOUtils;

public class GoFSettings
{
	private static final Logger log = Logger.getLogger(GoFSettings.class.getName());

	public static final int HIGH_GOD_THRESHOLD = 500;

	public static final String ADMINS = "admins";
	public static final String ADMINS_DEFAULT = "guild_of_fools, soilin89, itsjustchelsea, xxxunlivingxxx";
	public static final String CHANNEL = "channel";
	public static final String CHANNEL_DEFAULT = "#guild_of_fools";
	public static final String NICK = "nick";
	public static final String NICK_DEFAULT = "Gof_bot";
	public static final String OAUTH = "oauth";
	public static final String OAUTH_DEFAULT = "oauth:o04wh4pu4m55vqajjoey73zg2p9fl3p";
	public static final String SERVER = "server";
	public static final String SERVER_DEFAULT = "irc.twitch.tv";
	private static final String PROPS_FILE = "GoFSettings.txt";

	public static final String MAIL_HOST = "mailHost";
	public static final String MAIL_HOST_DEFAULT = "smtp.googlemail.com";
	public static final String MAIL_PORT = "mailPort";
	public static final String MAIL_PORT_DEFAULT = "587";
	public static final String MAIL_SSL = "mailSSL";
	public static final String MAIL_SSL_DEFAULT = "true";
	public static final String MAIL_USER = "mailUser";
	public static final String MAIL_USER_DEFAULT = "GoFBot@DevineEmail.com";
	public static final String MAIL_PASSWORD = "mailPassword";
	public static final String MAIL_PASSWORD_DEFAULT = "93&eA=>q!";
	public static final String MAIL_FROM = "mailFrom";
	public static final String MAIL_FROM_DEFAULT = "GoFBot@DevineEmail.com";
	public static final String MAIL_TO = "mailTo";
	public static final String MAIL_TO_DEFAULT = "Sean@DevineEmail.com";

	private final Set<String> adminSet = new HashSet<String>();
	private final Properties props;
	private static GoFSettings settings;

	public static GoFSettings getInstance()
	{
		if (settings == null)
		{
			synchronized (GoFDatabase.class)
			{
				if (settings == null)
				{
					settings = new GoFSettings();
				}
			}
		}
		return settings;
	}

	private GoFSettings()
	{
		props = new Properties();
		BufferedReader reader = null;
		try
		{
			final File propsFile = new File(PROPS_FILE);
			if (propsFile.exists())
			{
				reader = new BufferedReader(new FileReader("GoFSettings.txt"));
				props.load(reader);
			}
			else
			{
				props.setProperty(ADMINS, ADMINS_DEFAULT);
				props.setProperty(CHANNEL, CHANNEL_DEFAULT);
				props.setProperty(NICK, NICK_DEFAULT);
				props.setProperty(OAUTH, OAUTH_DEFAULT);
				props.setProperty(SERVER, SERVER_DEFAULT);
				props.setProperty(MAIL_HOST, MAIL_HOST_DEFAULT);
				props.setProperty(MAIL_PORT, MAIL_PORT_DEFAULT);
				props.setProperty(MAIL_SSL, MAIL_SSL_DEFAULT);
				props.setProperty(MAIL_USER, MAIL_USER_DEFAULT);
				props.setProperty(MAIL_PASSWORD, MAIL_PASSWORD_DEFAULT);
				props.setProperty(MAIL_FROM, MAIL_FROM_DEFAULT);
				props.setProperty(MAIL_TO, MAIL_TO_DEFAULT);

				save();
			}

			final String[] admins = props.getProperty(ADMINS).split(",");
			for (final String admin : admins)
			{
				adminSet.add(admin.trim());
			}
		}
		catch (final IOException ioe)
		{
			log.log(Level.WARNING, "Error loading settings", ioe);
		}
		finally
		{
			IOUtils.safeClose(reader);
		}
	}

	public boolean isAdmin(final String nick)
	{
		return adminSet.contains(nick);
	}

	public String getChannel()
	{
		return get(CHANNEL, CHANNEL_DEFAULT);
	}

	public String getNick()
	{
		return get(NICK, NICK_DEFAULT);
	}

	public String getOAuth()
	{
		return get(OAUTH, OAUTH_DEFAULT);
	}

	public String getServer()
	{
		return get(SERVER, SERVER_DEFAULT);
	}

	public String get(final String key, final String defaultValue)
	{
		return props.getProperty(key, defaultValue);
	}

	public void set(final String key, final String value)
	{
		props.setProperty(key, value);
	}

	public void save()
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter("GoFSettings.txt"));
			props.store(writer, null);
		}
		catch (final IOException ioe)
		{
			log.log(Level.WARNING, "Error saving settings", ioe);
		}
		finally
		{
			IOUtils.safeClose(writer);
		}
	}
}