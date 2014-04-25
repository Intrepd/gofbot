package com.guildoffools.bot.listeners;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

public class RollListener extends AbstractListenerAdapter
{
	private static final Random random = new Random();
	private static final String ROLL = "!roll";
	private static final Pattern ROLL_PATTERN = Pattern.compile("^(\\d)d(\\d*)((?:[\\+-]\\d*?)+)?$");
	private static final String SPLIT_PATTERN = "(?=(?!^)[\\+-])|(?<=[\\+-])";

	public RollListener(final PircBotX bot)
	{
		super(bot);
	}

	@Override
	public void onMessage(final MessageEvent<PircBotX> event)
	{
		String message = event.getMessage().trim();
		if (message.startsWith(ROLL))
		{
			message = message.substring(ROLL.length()).replace(" ", "");
			final Matcher m = ROLL_PATTERN.matcher(message);
			if (m.matches())
			{
				final int count = Integer.parseInt(m.group(1));
				final int sides = Integer.parseInt(m.group(2));
				int modifier = 0;
				if (m.groupCount() == 3 && m.group(3) != null)
				{
					final String[] modifierValues = m.group(3).split(SPLIT_PATTERN);
					for (int i = 0; i < modifierValues.length; i = i + 2)
					{
						final int negate = modifierValues[i].equals("-") ? -1 : 1;
						modifier += Integer.parseInt(modifierValues[i + 1]) * negate;
					}
				}

				if (count > 0 && sides > 0)
				{
					final StringBuilder builder = new StringBuilder();
					send(event.getUser().getNick() + " rolling " + message);
					int total = 0;
					for (int i = 0; i < count; i++)
					{
						final int roll = random.nextInt(sides) + 1;
						total += roll;
						builder.append("(" + roll + ") + ");
					}
					builder.setLength(builder.length() - 3);

					if (modifier > 0)
					{
						total += modifier;
						builder.append(modifier > 0 ? " + " + modifier : " - " + modifier);
					}
					builder.append(" = " + total);
					send(builder.toString());
				}
			}
		}
	}
}