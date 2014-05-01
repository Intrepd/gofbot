package com.guildoffools.bot.listeners;

import java.util.Date;
import java.util.logging.Logger;

import org.pircbotx.PircBotX;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.UserModel;
import com.guildoffools.bot.model.UserModelListener;

public class JoinListener extends AbstractListenerAdapter
{
	private static final Logger log = Logger.getLogger(JoinListener.class.getSimpleName());
	private static final int TWELVE_HOURS = 43200000;

	public JoinListener(final PircBotX bot)
	{
		super(bot);

		UserModel.getInstance().addListener(new UserModelListener()
		{
			@Override
			public void usersRemoved()
			{
			}

			@Override
			public void userRemoved(final String nick)
			{
			}

			@Override
			public void userAdded(final String nick)
			{
				handleJoin(nick);
			}
		});
	}

	private void handleJoin(final String nick)
	{
		final long timestamp = System.currentTimeMillis();
		final DefaultGoFUser gofUser = (DefaultGoFUser) db.getUser(nick, true);
		final Date lastJoined = gofUser.getLastJoined();
		if (timestamp - lastJoined.getTime() > TWELVE_HOURS)
		{
			gofUser.setPoints(gofUser.getPoints() + 1);
			gofUser.setCastsJoined(gofUser.getCastsJoined() + 1);
			log.info(nick + " earned a point for joining the cast, last seen " + lastJoined + ", " + gofUser.getPointsString() + ".");
			if (gofUser.isHighGod())
			{
				send("High God " + nick + " has arrived.");
			}
		}
		gofUser.setLastJoined(new Date(timestamp));
		db.updateUser(gofUser);
	}
}