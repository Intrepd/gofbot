package com.guildoffools.bot.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.text.TextAction;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import com.guildoffools.utils.BagHints;
import com.guildoffools.utils.LimitLinesPlainDocumentListener;

public class ChatPanel extends JPanel
{
	private static final Logger log = Logger.getLogger(ChatPanel.class.getName());

	private final JTextPane textPane = new JTextPane();

	public ChatPanel(final JFrame owner, final PircBotX bot)
	{
		super(new GridBagLayout());

		setBorder(new TitledBorder("Chat"));

		final StyledDocument doc = new DefaultStyledDocument();
		final Font font = new Font("Monospaced", 0, 12);
		this.textPane.setFont(font);
		this.textPane.setBackground(Color.BLACK);
		this.textPane.setForeground(Color.WHITE);
		this.textPane.setEditable(false);
		this.textPane.setDocument(doc);
		this.textPane.getDocument().addDocumentListener(new LimitLinesPlainDocumentListener(1000));
		final ActionMap am = this.textPane.getActionMap();
		am.put("select-word", new TextAction("select-word")
		{
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e)
			{
			}
		});

		bot.getConfiguration().getListenerManager().addListener(new ListenerAdapter<PircBotX>()
		{
			@Override
			public void onAction(final org.pircbotx.hooks.events.ActionEvent<PircBotX> event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					final AttributeSet nickAttribute = ChatColors.getColorFor(event.getUser().getNick());

					@Override
					public void run()
					{
						try
						{
							ChatPanel.this.textPane.getDocument().insertString(textPane.getDocument().getLength(),
									event.getUser().getNick() + " " + event.getMessage() + "\r\n", this.nickAttribute);
						}
						catch (final Exception e)
						{
							ChatPanel.log.log(Level.WARNING, "Error adding chat", e);
						}
					}
				});
			}

			@Override
			public void onMessage(final MessageEvent<PircBotX> event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					final AttributeSet nickAttribute = ChatColors.getColorFor(event.getUser().getNick());

					@Override
					public void run()
					{
						try
						{
							textPane.getDocument().insertString(textPane.getDocument().getLength(), event.getUser().getNick(), nickAttribute);
							textPane.getDocument().insertString(textPane.getDocument().getLength(), ": " + event.getMessage() + "\r\n", ChatColors.getTextColor());
						}
						catch (final Exception e)
						{
							ChatPanel.log.log(Level.WARNING, "Error adding chat", e);
						}
					}
				});
			}
		});
		final JScrollPane scrollPane = new JScrollPane(this.textPane);
		new SmartScroller(scrollPane);
		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row, 1, 1, 1.0D, 1.0D, 1, 11);
		add(scrollPane, hints);
		row++;
	}

	public JTextPane getTextPane()
	{
		return this.textPane;
	}
}