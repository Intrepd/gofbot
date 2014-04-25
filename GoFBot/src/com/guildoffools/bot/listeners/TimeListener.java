package com.guildoffools.bot.listeners;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.bot.model.UserListModel;

public class TimeListener extends AbstractListenerAdapter
{
	private static final Logger log = Logger.getLogger(TimeListener.class.getSimpleName());
	private static final String TIME = "!time";
	private static final int EARNING_INTERVAL = 120;

	public TimeListener(final PircBotX bot)
	{
		super(bot);

		timer.schedule(new TimeInChatTask(), 60000L, 60000L);
	}

	@Override
	public void onMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		if (message.startsWith(TIME))
		{
			final String nick = words.length > 1 ? words[1].toLowerCase() : event.getUser().getNick();
			final GoFUser user = db.getUser(nick, false);
			if (user != null)
			{
				send(nick + " has " + user.getTimeInChat() + " minutes in chat.");
			}
		}
	}

	private class TimeInChatTask extends TimerTask
	{
		private TimeInChatTask()
		{
		}

		@Override
		public void run()
		{
			try
			{
				final long now = System.currentTimeMillis();
				final String[] users = UserListModel.getInstance().getUsers();
				for (final String nick : users)
				{
					final GoFUser gofUser = TimeListener.this.db.getUser(nick, false);
					if (gofUser != null)
					{
						final long lastJoined = gofUser.getLastJoined().getTime();
						if (now - lastJoined > 60000L)
						{
							final int timeInChat = gofUser.getTimeInChat() + 1;
							((DefaultGoFUser) gofUser).setTimeInChat(timeInChat);
							if (timeInChat % EARNING_INTERVAL == 0)
							{
								final int points = gofUser.getPoints();
								((DefaultGoFUser) gofUser).setPoints(points + 1);
								log.info(nick + " awarded a loyalty point, time in chat " + timeInChat + ", " + gofUser.getPointsString() + ".");
							}
							db.updateUser(gofUser);
						}
					}
				}
			}
			catch (final Exception e)
			{
				log.log(Level.WARNING, "TimerTask Exception", e);
			}
		}
	}
}