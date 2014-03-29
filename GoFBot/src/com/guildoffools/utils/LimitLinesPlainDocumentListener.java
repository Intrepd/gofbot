package com.guildoffools.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

public class LimitLinesPlainDocumentListener implements DocumentListener
{
	private static final Logger log = Logger.getLogger(LimitLinesPlainDocumentListener.class.getName());

	private final int maximumElements;
	private final boolean removeFromStart;

	public LimitLinesPlainDocumentListener(final int maximumLines)
	{
		this(maximumLines, 1, true);
	}

	public LimitLinesPlainDocumentListener(final int maximumLines, final int elementSize, final boolean removeFromStart)
	{
		maximumElements = (maximumLines + 1) * elementSize;
		this.removeFromStart = removeFromStart;
	}

	@Override
	public void insertUpdate(final DocumentEvent e)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				removeLines(e);
			}
		});
	}

	@Override
	public void removeUpdate(final DocumentEvent documentevent)
	{
	}

	@Override
	public void changedUpdate(final DocumentEvent documentevent)
	{
	}

	private void removeLines(final DocumentEvent e)
	{
		final Document document = e.getDocument();
		final Element root = document.getDefaultRootElement();
		for (int elementCount = root.getElementCount(); elementCount > maximumElements; elementCount = root.getElementCount())
		{
			if (removeFromStart)
			{
				removeFromStart(document, root);
			}
			else
			{
				removeFromEnd(document, root);
			}
		}

	}

	private void removeFromStart(final Document document, final Element root)
	{
		final Element line = root.getElement(0);
		final int end = line.getEndOffset();
		try
		{
			document.remove(0, end);
		}
		catch (final BadLocationException ble)
		{
			log.log(Level.WARNING, "Error removing line", ble);
		}
	}

	private void removeFromEnd(final Document document, final Element root)
	{
		final Element line = root.getElement(root.getElementCount() - 1);
		final int start = line.getStartOffset();
		final int end = line.getEndOffset();
		try
		{
			document.remove(start - 1, end - start);
		}
		catch (final BadLocationException ble)
		{
			log.log(Level.WARNING, "Error removing line", ble);
		}
	}
}
