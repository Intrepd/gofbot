package com.guildoffools.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class IOUtils
{
	private static Logger logger = Logger.getLogger(IOUtils.class.getName());

	public static void safeClose(final Closeable closeable)
	{
		if (closeable != null)
		{
			if (closeable instanceof Flushable)
			{
				try
				{
					((Flushable) closeable).flush();
				}
				catch (final IOException ioe)
				{
				}
			}
			try
			{
				closeable.close();
			}
			catch (final IOException ioe)
			{
				logger.log(Level.WARNING, "Error closing ", ioe);
			}
		}
	}

	public static void zip(final File inFile, final File outFile) throws IOException
	{
		int bytesIn;
		final byte[] readBuffer = new byte[2048];
		BufferedInputStream bis = null;
		ZipOutputStream zos = null;
		try
		{
			bis = new BufferedInputStream(new FileInputStream(inFile));
			zos = new ZipOutputStream(new FileOutputStream(outFile));
			zos.putNextEntry(new ZipEntry(inFile.getName()));
			while ((bytesIn = bis.read(readBuffer)) != -1)
			{
				zos.write(readBuffer, 0, bytesIn);
			}
		}
		finally
		{
			safeClose(bis);
			safeClose(zos);
		}
	}

	public static void deleteAllFiles(final String parentDirectory)
	{
		deleteAllFiles(new File(parentDirectory));
	}

	public static void deleteAllFiles(final File dir)
	{
		if (dir != null && dir.exists() && dir.isDirectory())
		{
			final File[] filesToDelete = dir.listFiles();

			if (filesToDelete != null)
			{
				for (final File file : filesToDelete)
				{
					file.delete();
				}
			}
		}
	}

	public static void deleteDirectory(final File dir)
	{
		final Stack<File> dirStack = new Stack<File>();
		dirStack.push(dir);

		boolean containsSubFolder;
		while (!dirStack.isEmpty())
		{
			final File currDir = dirStack.peek();
			containsSubFolder = false;

			final String[] fileArray = currDir.list();
			if (fileArray == null)
			{
				dirStack.pop();
				continue;
			}

			for (final String element : fileArray)
			{
				final String fileName = currDir.getAbsolutePath() + File.separator + element;
				final File file = new File(fileName);
				if (file.isDirectory())
				{
					dirStack.push(file);
					containsSubFolder = true;
				}
				else
				{
					file.delete(); // delete file
				}
			}

			if (!containsSubFolder)
			{
				dirStack.pop(); // remove curr dir from stack
				currDir.delete(); // delete curr dir
			}
		}
	}

	public static String inputStreamToString(final InputStream in) throws IOException
	{
		final StringBuffer out = new StringBuffer();
		final byte[] buffer = new byte[4096];

		for (int i; (i = in.read(buffer)) != -1;)
		{
			out.append(new String(buffer, 0, i));
		}

		return out.toString();
	}

	public static byte[] inputStreamToByteArray(final InputStream inputStream) throws IOException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final byte[] buffer = new byte[4096];
		int bytesRead = -1;

		while ((bytesRead = inputStream.read(buffer)) > 0)
		{
			baos.write(buffer, 0, bytesRead);
		}

		inputStream.close();
		return baos.toByteArray();
	}

	public static String quickReadLine(final File file) throws IOException
	{
		BufferedReader br = null;
		String returnValue = null;

		try
		{
			br = new BufferedReader(new FileReader(file));
			returnValue = br.readLine();
		}
		finally
		{
			IOUtils.safeClose(br);
		}

		return returnValue;
	}

	public static String quickReadLine(final File file, final String defaultOutput)
	{
		String output;
		try
		{
			output = IOUtils.quickReadLine(file);
		}
		catch (final IOException ioe)
		{
			output = defaultOutput;
		}

		return output;
	}

	public static List<String> quickReadFile(final File file)
	{
		final List<String> output = new ArrayList<String>();

		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
			{
				output.add(line);
			}
		}
		catch (final IOException ioe)
		{
			logger.log(Level.WARNING, "Error reading " + file, ioe);
		}
		finally
		{
			IOUtils.safeClose(br);
		}

		return output;
	}

	public static void quickWriteFile(final File file, final List<String> lines) throws IOException
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(file));
			for (final String line : lines)
			{
				bw.write(line);
				bw.newLine();
			}
		}
		finally
		{
			IOUtils.safeClose(bw);
		}
	}

	public static void guaranteeParentDirExists(final File file)
	{
		final File parentFile = file.getParentFile();
		if (!parentFile.exists())
		{
			parentFile.mkdirs();
		}
	}

	public static boolean moveFile(final String sourceDirectory, final String sourceFileName, final String destinationDirectory,
			final String destinationFileName, final boolean overwrite) throws IOException
	{
		boolean success = false;
		final File destinationDirectoryFile = new File(destinationDirectory);
		if (!destinationDirectoryFile.exists())
		{
			destinationDirectoryFile.mkdirs();
		}

		final File sourceFile = new File(sourceDirectory, sourceFileName);
		final File destinationFile = new File(destinationDirectoryFile, destinationFileName);
		if (destinationFile.exists() && overwrite)
		{
			success = destinationFile.delete();
		}
		if (!destinationFile.exists())
		{
			success = sourceFile.renameTo(destinationFile);
		}

		return success;
	}

	public static void copyFile(final String sourceDirectory, final String sourceFileName, final String destinationDirectory, final String destinationFileName)
			throws IOException
	{
		final File destinationDirectoryFile = new File(destinationDirectory);
		if (!destinationDirectoryFile.exists())
		{
			destinationDirectoryFile.mkdirs();
		}

		final File sourceFile = new File(sourceDirectory, sourceFileName);
		final File destinationFile = new File(destinationDirectoryFile, destinationFileName);
		BufferedInputStream bufferIn = null;
		BufferedOutputStream bufferOut = null;
		try
		{
			bufferOut = new BufferedOutputStream(new FileOutputStream(destinationFile));
			bufferIn = new BufferedInputStream(new FileInputStream(sourceFile));
			int bytesRead = 0;
			final byte[] buffer = new byte[32 * 1024];
			while (bytesRead != -1)
			{
				bufferOut.write(buffer, 0, bytesRead);
				bytesRead = bufferIn.read(buffer);
			}
		}
		finally
		{
			safeClose(bufferIn);
			safeClose(bufferOut);
		}
	}

	public static void channelCopyFile(final String sourceDirectory, final String sourceFileName, final String destinationDirectory,
			final String destinationFileName) throws IOException
	{
		final File destinationDirectoryFile = new File(destinationDirectory);
		if (!destinationDirectoryFile.exists())
		{
			destinationDirectoryFile.mkdirs();
		}

		final File sourceFile = new File(sourceDirectory, sourceFileName);
		final File destinationFile = new File(destinationDirectoryFile, destinationFileName);
		FileChannel source = null;
		FileChannel destination = null;
		try
		{
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destinationFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally
		{
			safeClose(source);
			safeClose(destination);
		}
	}

	public static void linkFile(final String sourceDirectory, final String sourceFileName, final String destinationDirectory, final String destinationFileName)
			throws IOException
	{
		final String thisOSName = System.getProperty("os.name", "");

		if ((thisOSName != null) && thisOSName.toUpperCase().equals("LINUX"))
		{
			try
			{
				final String sourcePath = sourceDirectory + "/" + sourceFileName;
				final String destinationPath = destinationDirectory + "/" + destinationFileName;
				final Process process = Runtime.getRuntime().exec(new String[] { "ln", "-s", sourcePath, destinationPath });
				process.waitFor();
				process.destroy();
			}
			catch (final InterruptedException ie)
			{
				final IOException ioe = new IOException("Error creating link: sourceFileName " + sourceFileName + ", sourceDirectory: " + sourceDirectory
						+ ", destinationDirectory: " + destinationDirectory + ", error: " + ie.getMessage());
				ioe.initCause(ie);
				throw ioe;
			}
		}
		else
		{
			copyFile(sourceDirectory, sourceFileName, destinationDirectory, destinationFileName);
		}
	}

	public static long getLatestTimeStamp(final File rootDirectory)
	{
		return getLatestTimeStamp(rootDirectory, rootDirectory.lastModified(), null);
	}

	public static long getLatestTimeStamp(final File rootDirectory, final String[] ignoreFileNames)
	{
		return getLatestTimeStamp(rootDirectory, rootDirectory.lastModified(), ignoreFileNames);
	}

	private static long getLatestTimeStamp(final File directory, final long timestamp, final String[] ignoreFileNames)
	{
		long latestTimeStamp = timestamp;
		final File[] listFiles = directory != null ? directory.listFiles() : null;

		if (listFiles != null)
		{
			for (final File file : listFiles)
			{
				if (shouldIgnoreFile(file, ignoreFileNames))
				{
					continue;
				}

				final long fileTimeStamp = file.lastModified();
				if (fileTimeStamp > latestTimeStamp)
				{
					latestTimeStamp = fileTimeStamp;
				}

				if (file.isDirectory())
				{
					latestTimeStamp = getLatestTimeStamp(file, latestTimeStamp, ignoreFileNames);
				}
			}
		}

		return latestTimeStamp;
	}

	private static boolean shouldIgnoreFile(final File file, final String[] ignoreFileNames)
	{
		final String fileName = file.getName();
		boolean ignore = file.getName().startsWith(".");

		if ((ignoreFileNames != null) && !ignore)
		{
			for (final String ignoreFileName : ignoreFileNames)
			{
				if (ignoreFileName.equalsIgnoreCase(fileName))
				{
					ignore = true;
					break;
				}
			}
		}

		return ignore;
	}
}
