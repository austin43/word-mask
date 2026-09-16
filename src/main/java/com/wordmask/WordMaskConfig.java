package com.wordmask;

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
}
