package com.wordmask;

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuEntry;
import net.runelite.api.MessageNode;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Word Mask",
	description = "Replace words anywhere in game text: NPC dialog, books, chat, overhead speech, and menus",
	tags = {"replace", "text", "dialog", "book", "chat", "word", "mask"}
)
public class WordMaskPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private WordMaskConfig config;

	private final WordReplacer replacer = new WordReplacer();

	@Override
	protected void startUp()
	{
		rebuild();
		log.debug("Word Mask started");
	}

	@Override
	protected void shutDown()
	{
		replacer.clear();
		log.debug("Word Mask stopped");
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if ("wordmask".equals(event.getGroup()))
		{
			rebuild();
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		if (replacer.isEmpty() || client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		Widget[] roots = client.getWidgetRoots();
		if (roots == null)
		{
			return;
		}

		for (Widget root : roots)
		{
			walk(root);
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (replacer.isEmpty())
		{
			return;
		}

		MessageNode node = event.getMessageNode();
		String value = node.getValue();
		if (value == null || value.isEmpty())
		{
			return;
		}

		String replaced = replacer.replace(value);
		if (!replaced.equals(value))
		{
			node.setValue(replaced);
			client.refreshChat();
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		if (replacer.isEmpty() || event.getActor() == null)
		{
			return;
		}

		String original = event.getOverheadText();
		if (original == null || original.isEmpty())
		{
			return;
		}

		// Overhead bubbles do not parse <col> tags.
		String replaced = replacer.replace(original, false);
		if (!replaced.equals(original))
		{
			event.getActor().setOverheadText(replaced);
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (replacer.isEmpty())
		{
			return;
		}

		MenuEntry entry = event.getMenuEntry();
		String option = entry.getOption();
		String target = entry.getTarget();
		String newOption = replacer.replace(option);
		String newTarget = replacer.replace(target);

		if (!option.equals(newOption))
		{
			entry.setOption(newOption);
		}
		if (!target.equals(newTarget))
		{
			entry.setTarget(newTarget);
		}
	}

	@Provides
	WordMaskConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(WordMaskConfig.class);
	}

	private void rebuild()
	{
		String hex = null;
		if (config.highlight())
		{
			Color color = config.highlightColor();
			if (color != null)
			{
				hex = String.format("%06x", color.getRGB() & 0xFFFFFF);
			}
		}
		replacer.parse(config.replacements(), config.caseSensitive(), config.wholeWord(), hex);
	}

	private void walk(Widget widget)
	{
		if (widget == null || widget.isHidden())
		{
			return;
		}

		// Skip input fields so typed chat is never rewritten.
		if (widget.getOnKeyListener() == null)
		{
			apply(widget);
		}

		walkChildren(widget.getStaticChildren());
		walkChildren(widget.getDynamicChildren());
		walkChildren(widget.getNestedChildren());
	}

	private void walkChildren(Widget[] children)
	{
		if (children == null)
		{
			return;
		}

		for (Widget child : children)
		{
			walk(child);
		}
	}

	private void apply(Widget widget)
	{
		String text = widget.getText();
		if (text != null && !text.isEmpty())
		{
			String replaced = replacer.replace(text);
			if (!replaced.equals(text))
			{
				widget.setText(replaced);
			}
		}

		String name = widget.getName();
		if (name != null && !name.isEmpty())
		{
			String replaced = replacer.replace(name);
			if (!replaced.equals(name))
			{
				widget.setName(replaced);
			}
		}
	}
}
