package com.guildoffools.bot.ui;

import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;

import org.pircbotx.PircBotX;

import com.guildoffools.bot.db.GoFDatabase;
import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.bot.model.UserListTableModel;
import com.guildoffools.utils.BagHints;

public class MainFrame extends JFrame
{
	private static final Logger log = Logger.getLogger(MainFrame.class.getName());
	private final LogPanel consolePanel;
	private final ChatPanel chatPanel;
	private final UserListPanel userListPanel;

	public MainFrame(final PircBotX bot)
	{
		super("GoFBot 1.0");

		setDefaultCloseOperation(3);
		setLayout(new GridBagLayout());

		this.consolePanel = new LogPanel();

		final UserListTableModel userTableModel = new UserListTableModel(bot);
		this.userListPanel = new UserListPanel(this, userTableModel);
		this.userListPanel.getTable().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				if ((e.getButton() == 1) && (e.getClickCount() == 2))
				{
					final int selectedRow = MainFrame.this.userListPanel.getTable().getSelectedRow();
					if (selectedRow >= 0)
					{
						final GoFUser user = userTableModel.getGoFUser(MainFrame.this.userListPanel.getTable().convertRowIndexToModel(selectedRow));
						new EditGodPointDialog(MainFrame.this, user);
					}
				}
			}
		});
		this.chatPanel = new ChatPanel(this, bot);
		this.chatPanel.getTextPane().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(final MouseEvent e)
			{
				final int cursorPoint = MainFrame.this.chatPanel.getTextPane().viewToModel(e.getPoint());
				try
				{
					final Element paragraph = Utilities.getParagraphElement(MainFrame.this.chatPanel.getTextPane(), cursorPoint);
					final int paragraphStart = paragraph.getStartOffset();
					final int paragraphEnd = paragraph.getEndOffset();
					final String paragraphText = MainFrame.this.chatPanel.getTextPane().getText(paragraphStart, paragraphEnd - paragraphStart);
					final int colonIndex = paragraphText.indexOf(':');
					if (colonIndex > -1)
					{
						final String nick = paragraphText.substring(0, colonIndex);
						final GoFUser user = GoFDatabase.getInstance().getUser(nick, false);
						if (user != null)
						{
							final int index = userTableModel.getUserIndex(nick);
							if (index > -1)
							{
								final int convertedIndex = MainFrame.this.userListPanel.getTable().convertRowIndexToView(index);
								MainFrame.this.userListPanel.getTable().getSelectionModel().setSelectionInterval(convertedIndex, convertedIndex);
								MainFrame.this.userListPanel.getTable().scrollRowToVisible(convertedIndex);
							}
							if (e.getClickCount() == 2)
							{
								new EditGodPointDialog(MainFrame.this, user);
							}
						}
					}
				}
				catch (final BadLocationException ble)
				{
				}
			}
		});
		final JSplitPane splitPane = new JSplitPane(0, this.chatPanel, this.consolePanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(450);

		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row, 1, 1, 1.0D, 1.0D);
		add(splitPane, hints);
		hints.set(1, row, 1, 1, 0.0D, 1.0D);
		add(this.userListPanel, hints);
		row++;

		setSize(1100, 600);
		setVisible(true);
		setLocationRelativeTo(null);
	}

	@SuppressWarnings("unused")
	private void setNativeLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (final Exception e)
		{
			log.log(Level.WARNING, "Could not set Look and Feel", e);
		}
	}
}