package com.guildoffools.bot.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.guildoffools.utils.IOUtils;

public class GoFDatabaseUpgrader
{
	private static final Logger log = Logger.getLogger(GoFDatabaseUpgrader.class.getName());

	public static void upgrade()
	{
		final File csvFile = new File(GoFDatabase.CSV_FILE);
		final File tempFile = new File(GoFDatabase.CSV_FILE_TEMP);

		if (csvFile.canRead())
		{
			try
			{
				final String headerLine = IOUtils.quickReadLine(csvFile);
				final String[] headers = headerLine.split(",");

				boolean modified = false;
				if (headers.length == 4)
				{
					addFields(csvFile, tempFile, "highGod,castsJoined", "false,0");
					modified = true;
				}

				if (modified)
				{
					if (!csvFile.delete())
					{
						log.log(Level.WARNING, "Upgrader could not delete old database.");
					}
					if (!tempFile.renameTo(csvFile))
					{
						log.log(Level.WARNING, "Upgrader could not rename temp database.");
					}
				}
			}
			catch (final IOException ioe)
			{
				log.log(Level.WARNING, "Error upgrading database", ioe);
			}
		}
	}

	private static void addFields(final File csvFile, final File tempFile, final String columnNames, final String defaultValues)
	{
		BufferedReader br = null;
		BufferedWriter bw = null;
		try
		{
			br = new BufferedReader(new FileReader(csvFile));
			bw = new BufferedWriter(new FileWriter(tempFile));
			final String headerLine = br.readLine();
			bw.write(headerLine);
			bw.write("," + columnNames);
			bw.newLine();

			String line = br.readLine();
			while (line != null)
			{
				bw.write(line);
				bw.write("," + defaultValues);
				bw.newLine();
				line = br.readLine();
			}
		}
		catch (final IOException ioe)
		{
			log.log(Level.WARNING, "Error upgrading database", ioe);
		}
		finally
		{
			IOUtils.safeClose(br);
			IOUtils.safeClose(bw);
		}
	}
}
