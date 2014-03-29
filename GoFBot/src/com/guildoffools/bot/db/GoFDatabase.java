package com.guildoffools.bot.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.utils.IOUtils;
import com.guildoffools.utils.MailUtils;

public class GoFDatabase
{
	private static final Logger log = Logger.getLogger(GoFDatabase.class.getName());
	private static final int SAVE_INTERVAL = 300000;
	private static final String CSV_FILE = "GoFBot.csv";
	private static final String CSV_FILE_TEMP = "GoFBot.csv.temp";
	private static GoFDatabase database;
	private final List<GoFDatabaseListener> listeners = new ArrayList<GoFDatabaseListener>();
	private final Map<String, GoFUser> userMap = new HashMap<String, GoFUser>();

	public static GoFDatabase getInstance()
	{
		if (database == null)
		{
			synchronized (GoFDatabase.class)
			{
				if (database == null)
				{
					database = new GoFDatabase();
				}
			}
		}
		return database;
	}

	private GoFDatabase()
	{
		CsvBeanReader beanReader = null;
		try
		{
			final File csvFile = new File(CSV_FILE);
			if (csvFile.canRead())
			{
				final BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), Charset.forName("UTF-8")));
				beanReader = new CsvBeanReader(reader, CsvPreference.EXCEL_PREFERENCE);
				beanReader.getHeader(true);
				DefaultGoFUser user = beanReader.read(DefaultGoFUser.class, DefaultGoFUser.HEADER, DefaultGoFUser.PROCESSORS);
				while (user != null)
				{
					userMap.put(user.getNick(), user);
					user = beanReader.read(DefaultGoFUser.class, DefaultGoFUser.HEADER, DefaultGoFUser.PROCESSORS);
				}
			}

			final Timer saveTimer = new Timer(true);
			saveTimer.schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					try
					{
						save();
					}
					catch (final IOException e)
					{
						GoFDatabase.log.log(Level.WARNING, "Error during periodic save", e);
					}
				}
			}, SAVE_INTERVAL, SAVE_INTERVAL);

			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				@Override
				public void run()
				{
					try
					{
						GoFDatabase.log.info("Shutdown hook save started");
						save();
						System.out.println("Save complete");
						GoFDatabase.log.info("Save complete");
					}
					catch (final IOException e)
					{
						GoFDatabase.log.log(Level.WARNING, "Error saving", e);
					}
				}
			});
		}
		catch (final IOException ioe)
		{
			log.log(Level.WARNING, "Error loading database", ioe);
		}
		finally
		{
			IOUtils.safeClose(beanReader);
		}
	}

	public GoFUser getUser(final String nick, final boolean createIfNeeded)
	{
		DefaultGoFUser user;
		boolean userAdded;
		synchronized (userMap)
		{
			user = (DefaultGoFUser) userMap.get(nick);
			userAdded = true;
			if ((user == null) && (createIfNeeded))
			{
				user = new DefaultGoFUser(nick);
				log.info(nick + " joined the cast for the first time.");
				userMap.put(user.getNick(), user);
			}
		}

		if (userAdded)
		{
			fireUserAdded(user);
		}

		return (GoFUser) (user != null ? user.clone() : null);
	}

	public void updateUser(final GoFUser user)
	{
		synchronized (userMap)
		{
			userMap.put(user.getNick(), user);
		}
		fireUserUpdated(user);
	}

	public void addListener(final GoFDatabaseListener listener)
	{
		synchronized (listeners)
		{
			listeners.add(listener);
		}
	}

	public File serializeDatabase(final boolean zipDB) throws IOException
	{
		final File tempFile = File.createTempFile("GoFBot", zipDB ? ".csv.zip" : ".csv");
		tempFile.deleteOnExit();

		CsvBeanWriter beanWriter = null;
		Writer writer = null;
		ZipOutputStream zos = null;
		try
		{
			if (zipDB)
			{
				zos = new ZipOutputStream(new FileOutputStream(tempFile));
				zos.putNextEntry(new ZipEntry("GoFBot.csv"));
				writer = new BufferedWriter(new OutputStreamWriter(zos));
			}
			else
			{
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile)));
			}
			beanWriter = new CsvBeanWriter(writer, CsvPreference.EXCEL_PREFERENCE);
			beanWriter.writeHeader(DefaultGoFUser.HEADER);
			synchronized (userMap)
			{
				for (final GoFUser user : userMap.values())
				{
					beanWriter.write(user, DefaultGoFUser.HEADER);
				}
			}
		}
		finally
		{
			IOUtils.safeClose(beanWriter);
			IOUtils.safeClose(writer);
			IOUtils.safeClose(zos);
		}

		return tempFile;
	}

	public void mailDatabase()
	{
		File dbZipFile = null;
		try
		{
			final boolean zipDb = false;
			dbZipFile = serializeDatabase(zipDb);

			final GoFSettings settings = GoFSettings.getInstance();
			final String host = settings.get(GoFSettings.MAIL_HOST, GoFSettings.MAIL_HOST_DEFAULT);
			final int port = Integer.parseInt(settings.get(GoFSettings.MAIL_PORT, GoFSettings.MAIL_PORT_DEFAULT));
			final String username = settings.get(GoFSettings.MAIL_USER, GoFSettings.MAIL_USER_DEFAULT);
			final String password = settings.get(GoFSettings.MAIL_PASSWORD, GoFSettings.MAIL_PASSWORD_DEFAULT);
			final boolean useSSL = Boolean.parseBoolean(settings.get(GoFSettings.MAIL_SSL, GoFSettings.MAIL_SSL_DEFAULT));
			final String fromAddress = settings.get(GoFSettings.MAIL_FROM, GoFSettings.MAIL_FROM_DEFAULT);
			final String[] toAddressList = settings.get(GoFSettings.MAIL_TO, GoFSettings.MAIL_TO_DEFAULT).split(",");

			final EmailAttachment attachment = MailUtils.createAttachment(dbZipFile.getAbsolutePath(), zipDb ? "GoFBotDB.zip" : "GoFBot.csv",
					"GoFBot GodPoint Database");
			MailUtils.sendMail(host, port, username, password, useSSL, fromAddress, toAddressList, "GoFBot Database", "See attached", attachment);
		}
		catch (final IOException ioe)
		{
			log.log(Level.WARNING, "Error serializing database", ioe);
		}
		catch (final EmailException ee)
		{
			log.log(Level.WARNING, "Error mailing database", ee);
		}
		catch (final Exception e)
		{
			log.log(Level.WARNING, "Error", e);
		}
		finally
		{
			if (dbZipFile != null)
			{
				dbZipFile.delete();
			}
		}
	}

	private GoFDatabaseListener[] getListenersCopy()
	{
		GoFDatabaseListener[] listenersCopy;
		synchronized (listeners)
		{
			listenersCopy = listeners.toArray(new GoFDatabaseListener[0]);
		}

		return listenersCopy;
	}

	private void fireUserAdded(final GoFUser user)
	{
		for (final GoFDatabaseListener listener : getListenersCopy())
		{
			listener.userAdded(user);
		}
	}

	private void fireUserUpdated(final GoFUser user)
	{
		for (final GoFDatabaseListener listener : getListenersCopy())
		{
			listener.userUpdated(user);
		}
	}

	private synchronized void save() throws IOException
	{
		CsvBeanWriter beanWriter = null;
		boolean writeComplete = false;

		final File tempFile = new File(CSV_FILE_TEMP);
		final File file = new File(CSV_FILE);
		try
		{
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), Charset.forName("UTF-8")));
			beanWriter = new CsvBeanWriter(writer, CsvPreference.EXCEL_PREFERENCE);
			beanWriter.writeHeader(DefaultGoFUser.HEADER);
			synchronized (userMap)
			{
				for (final GoFUser user : userMap.values())
				{
					beanWriter.write(user, DefaultGoFUser.HEADER);
				}
			}
			writeComplete = true;
		}
		finally
		{
			IOUtils.safeClose(beanWriter);
		}

		if (writeComplete)
		{
			if (!file.delete())
			{
				log.log(Level.WARNING, "Could not delete old database.");
			}
			if (!tempFile.renameTo(file))
			{
				log.log(Level.WARNING, "Could not rename temp database.");
			}
		}
	}
}