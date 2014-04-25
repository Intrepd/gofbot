package com.guildoffools.bot.listeners;

import java.util.List;

import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.ServerResponseEvent;

import com.guildoffools.bot.model.UserListModel;

public class UserListListener extends AbstractListenerAdapter
{
	private static final UserListModel userList = UserListModel.getInstance();

	public UserListListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onServerResponse(final ServerResponseEvent<PircBotX> event)
	{
		if (event.getCode() == ReplyConstants.RPL_NAMREPLY)
		{
			final List<String> fields = event.getParsedResponse();
			if (fields.size() > 3)
			{
				final String userList = fields.get(3);
				final String[] nicks = userList.split(" ");
				for (final String nick : nicks)
				{
					UserListModel.getInstance().addUser(nick);
				}
			}
		}
	}

	@Override
	public void onJoin(final JoinEvent<PircBotX> event)
	{
		userList.addUser(event.getUser().getNick());
	}

	@Override
	public void onPart(final PartEvent<PircBotX> event)
	{
		userList.removeUser(event.getUser().getNick());
	}

	@Override
	public void onDisconnect(final DisconnectEvent<PircBotX> event)
	{
		userList.removeAllUsers();
	}
}