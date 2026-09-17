package com.wordmask;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("wordmask")
public interface WordMaskConfig extends Config
{
	@ConfigItem(
		keyName = "replacements",
		name = "Replacements",
		description = "One per line: find=replace. Lines starting with # are comments.",
		position = 0
	)
	default String replacements()
	{
		return "Gielinor=Runescape";
	}

	@ConfigItem(
		keyName = "caseSensitive",
		name = "Case sensitive",
		description = "Match the find-text exactly as typed. Off matches Gielinor, gielinor, GIELINOR.",
		position = 1
	)
	default boolean caseSensitive()
	{
		return false;
	}

	@ConfigItem(
		keyName = "wholeWord",
		name = "Whole word",
		description = "Only replace complete words. Off also replaces the find-text inside longer words.",
		position = 2
	)
	default boolean wholeWord()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlight",
		name = "Highlight replacements",
		description = "Color the replaced text in dialog, books, chat, and menus. Overhead speech stays uncolored (no tag support).",
		position = 3
	)
	default boolean highlight()
	{
		return true;
	}

	@ConfigItem(
		keyName = "highlightColor",
		name = "Highlight color",
		description = "Color of replaced words when highlight is on. Default is dark magenta.",
		position = 4
	)
	default Color highlightColor()
	{
		return new Color(0x8B008B);
	}
}
