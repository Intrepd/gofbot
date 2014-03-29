package com.guildoffools.bot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.guildoffools.utils.BagHints;
import com.guildoffools.utils.LimitLinesPlainDocumentListener;

public class LogPanel extends JPanel
{
	private final JTextArea textArea = new JTextArea();

	public LogPanel()
	{
		super(new GridBagLayout());
		setBorder(new TitledBorder("Log"));
		setPreferredSize(new Dimension(0, 100));

		final Font font = new Font("Monospaced", 0, 12);
		this.textArea.setFont(font);
		this.textArea.setBackground(Color.BLACK);
		this.textArea.setForeground(Color.WHITE);
		this.textArea.setEditable(false);
		this.textArea.setLineWrap(true);
		this.textArea.setWrapStyleWord(true);
		this.textArea.getDocument().addDocumentListener(new LimitLinesPlainDocumentListener(1000));

		Logger.getLogger("").addHandler(new Handler()
		{
			@Override
			public void publish(final LogRecord record)
			{
				LogPanel.this.textArea.append(record.getMessage() + "\r\n");
			}

			@Override
			public void flush()
			{
			}

			@Override
			public void close() throws SecurityException
			{
			}
		});
		final JScrollPane scrollPane = new JScrollPane(this.textArea);
		new SmartScroller(scrollPane);
		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row, 1, 1, 1.0D, 1.0D, 1, 11);
		add(scrollPane, hints);
		row++;
	}
}