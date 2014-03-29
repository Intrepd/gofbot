package com.guildoffools.bot.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ChatColors
{
	private static final StyleContext sc = StyleContext.getDefaultStyleContext();
	private static final AttributeSet TEXT_COLOR;
	private static final List<AttributeSet> attributeSetList = new ArrayList<AttributeSet>();

	static
	{
		final List<Color> colors = new ArrayList<Color>();
		colors.add(new Color(170, 0, 0));
		colors.add(new Color(0, 170, 0));
		colors.add(new Color(170, 85, 0));
		colors.add(new Color(0, 0, 170));
		colors.add(new Color(170, 0, 170));
		colors.add(new Color(0, 170, 170));
		colors.add(new Color(170, 170, 170));
		colors.add(new Color(255, 85, 85));
		colors.add(new Color(85, 255, 85));
		colors.add(new Color(255, 255, 85));
		colors.add(new Color(85, 85, 255));
		colors.add(new Color(85, 255, 255));
		colors.add(new Color(255, 255, 255));

		final SimpleAttributeSet fontSet = new SimpleAttributeSet();

		StyleConstants.setFontSize(fontSet, 18);

		TEXT_COLOR = sc.addAttribute(fontSet, StyleConstants.Foreground, Color.WHITE);

		for (final Color color : colors)
		{
			attributeSetList.add(sc.addAttribute(fontSet, StyleConstants.Foreground, color));
		}
	}

	public static AttributeSet getTextColor()
	{
		return TEXT_COLOR;
	}

	public static AttributeSet getColorFor(final String nick)
	{
		return attributeSetList.get(Math.abs(nick.hashCode()) % attributeSetList.size());
	}
}