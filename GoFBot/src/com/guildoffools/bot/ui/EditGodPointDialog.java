package com.guildoffools.bot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;

import com.guildoffools.bot.db.GoFDatabase;
import com.guildoffools.bot.model.DefaultGoFUser;
import com.guildoffools.bot.model.GoFUser;
import com.guildoffools.utils.BagHints;

public class EditGodPointDialog extends JDialog
{
	private final JTextField userField = new JTextField();
	private final JSpinner pointsField = new JSpinner(new SpinnerNumberModel());
	private final JSpinner timeInChatField = new JSpinner(new SpinnerNumberModel());

	private final JButton okButton = new JButton("Ok");
	private final JButton cancelButton = new JButton("Cancel");
	private final GoFUser user;

	public EditGodPointDialog(final JFrame owner, final GoFUser user)
	{
		super(owner, true);

		this.user = user;

		setLayout(new GridBagLayout());
		setTitle("Edit God Points");
		final JPanel contentPanel = (JPanel) getContentPane();
		contentPanel.setBorder(new BevelBorder(1));

		final KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(27, 0, false);
		final Action escapeAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				EditGodPointDialog.this.cancelButton.doClick();
			}
		};
		this.rootPane.getInputMap(2).put(escapeKeyStroke, "ESCAPE");
		this.rootPane.getActionMap().put("ESCAPE", escapeAction);

		final KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(10, 0, false);
		final Action enterAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				EditGodPointDialog.this.okButton.doClick();
			}
		};
		this.rootPane.getInputMap(2).put(enterKeyStroke, "ENTER");
		this.rootPane.getActionMap().put("ENTER", enterAction);

		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row++, 1, 1, 1.0D, 1.0D, 1, 10);
		add(createFieldsPanel(), hints);
		hints.set(0, row++, 1, 1, 1.0D, 0.0D, 1, 13);
		add(createButtonsPanel(), hints);
		row++;

		this.okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				EditGodPointDialog.this.apply();
				EditGodPointDialog.this.setVisible(false);
			}
		});
		this.cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				EditGodPointDialog.this.setVisible(false);
			}
		});
		pack();
		this.pointsField.requestFocusInWindow();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private JPanel createFieldsPanel()
	{
		final JPanel panel = new JPanel(new GridBagLayout());

		final BagHints hints = new BagHints();
		int row = 0;
		hints.set(0, row, 1, 1, 0.0D, 1.0D, 1, 11);
		panel.add(new JLabel("User"), hints);
		hints.set(1, row, 1, 1, 1.0D, 0.0D);
		this.userField.setText(this.user.getNick());
		this.userField.setEditable(false);
		this.userField.setPreferredSize(new Dimension(200, this.userField.getPreferredSize().height));
		panel.add(this.userField, hints);
		row++;
		hints.set(0, row, 1, 1, 0.0D, 1.0D);
		panel.add(new JLabel("Points"), hints);
		hints.set(1, row, 1, 1, 1.0D, 1.0D);
		this.pointsField.setValue(Integer.valueOf(this.user.getPoints()));
		panel.add(this.pointsField, hints);
		row++;
		hints.set(0, row, 1, 1, 0.0D, 1.0D);
		panel.add(new JLabel("Time in Chat"), hints);
		hints.set(1, row, 1, 1, 1.0D, 1.0D);
		this.timeInChatField.setValue(Integer.valueOf(this.user.getTimeInChat()));
		panel.add(this.timeInChatField, hints);
		row++;

		return panel;
	}

	private JPanel createButtonsPanel()
	{
		final JPanel panel = new JPanel(new GridBagLayout());

		final BagHints hints = new BagHints();
		hints.set(0, 0, 1, 1, 1.0D, 0.0D, 0, 13);
		panel.add(this.okButton, hints);
		hints.set(1, 0, 1, 1, 0.0D, 0.0D, 0, 13);
		panel.add(this.cancelButton, hints);

		return panel;
	}

	private void apply()
	{
		((DefaultGoFUser) this.user).setPoints(((Integer) this.pointsField.getValue()).intValue());
		((DefaultGoFUser) this.user).setTimeInChat(((Integer) this.timeInChatField.getValue()).intValue());

		GoFDatabase.getInstance().updateUser(this.user);
	}
}