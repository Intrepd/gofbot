package com.guildoffools.bot.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;

public class CostListener extends AbstractListenerAdapter
{
	private static final String ACCEPT = "!accept";
	private static final String REJECT = "!reject";
	private static final String GPCOST = "!gpcost";
	private static final int COST_EXPIRATION = 60000;

	private final Map<String, CostTask> costTasks = new HashMap<String, CostTask>();

	public CostListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onMessage(final MessageEvent<PircBotX> event)
	{
		final String message = event.getMessage().trim();
		final String[] words = message.split(" ");

		// !gpcost intrepd 5
		if (settings.isAdmin(event.getUser().getNick()) && words.length > 1 && words[0].equals(GPCOST))
		{
			try
			{
				final String nick = words[1].toLowerCase();
				final int cost = Integer.parseInt(words[2]);
				final GoFUser user = db.getUser(nick, false);
				if (user != null)
				{
					final int points = user.getPoints();
					if (cost > points)
					{
						send(nick + " only has " + points + " god point" + (points == 1 ? "" : "s") + ".");
					}
					else
					{
						synchronized (costTasks)
						{
							final CostTask oldTask = costTasks.remove(nick);
							if (oldTask != null)
							{
								oldTask.cancel();
								timer.purge();
							}
							final CostTask costTask = new CostTask(nick, cost);
							costTasks.put(nick, costTask);
							timer.schedule(costTask, COST_EXPIRATION);
							send(cost + " god point" + (cost == 1 ? "" : "s") + " offered. " + nick + ", you have one minute to !accept.");
						}
					}
				}
				else
				{
					send("I don't see " + nick);
				}
			}
			catch (final Exception e)
			{
			}
		}
		else if (message.startsWith(ACCEPT) || message.startsWith(REJECT))
		{
			final String nick = event.getUser().getNick();
			synchronized (costTasks)
			{
				final CostTask costTask = costTasks.remove(nick);
				if (costTask != null)
				{
					costTask.cancel();
					timer.purge();
					if (message.startsWith(ACCEPT))
					{
						final GoFUser user = db.getUser(nick, false);
						if (user != null)
						{
							final int userPoints = user.getPoints();
							final int cost = costTask.getCost();
							if (userPoints >= cost)
							{
								((DefaultGoFUser) user).setPoints(userPoints - cost);
								db.updateUser(user);
								send(nick + " accepts and now has " + user.getPointsString());
							}
						}
					}
					else
					{
						send(nick + " rejects.");
					}
				}
			}
		}
	}

	private class CostTask extends TimerTask
	{
		private final String nick;
		private final int cost;

		private CostTask(final String nick, final int cost)
		{
			this.nick = nick;
			this.cost = cost;
		}

		private int getCost()
		{
			return cost;
		}

		@Override
		public void run()
		{
			synchronized (costTasks)
			{
				if (costTasks.remove(nick) != null)
				{
					send("The god point offer for " + nick + " has expired.");
				}
			}
		}
	}
}