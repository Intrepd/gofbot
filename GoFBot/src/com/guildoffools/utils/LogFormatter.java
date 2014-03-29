package com.guildoffools.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter
{
	private final SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss");
	private static final int MESSAGE_COLUMN = 14;

	@Override
	public String format(final LogRecord record)
	{
		final StringBuffer result = new StringBuffer();
		final String timestamp = timestampFormat.format(new Date(record.getMillis()));
		final Throwable anyException = record.getThrown();

		result.append(timestamp); // Java 1.5 String.format( "%1$-2tH:%1$-2tM:%1$-2tS ", record.getMillis() ) );
		result.append(" ").append(record.getLevel().toString());
		result.append(" ").append(record.getMessage()).append("\n");

		while (result.length() < MESSAGE_COLUMN)
		{
			result.append(' ');
		}

		if (anyException != null)
		{
			result.append("Exception: ").append(anyException.toString()).append("\n");
			final StackTraceElement[] stackTrace = anyException.getStackTrace();
			for (int i = 0; i < stackTrace.length; i++)
			{
				final StackTraceElement traceLine = stackTrace[i];
				result.append("   ").append(traceLine).append("\n");
			}
			result.append("\n");
		}

		return result.toString();
	}
}
