package com.wordmask;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class WordMaskPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(WordMaskPlugin.class);
		RuneLite.main(args);
	}
}
