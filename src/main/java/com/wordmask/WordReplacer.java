package com.wordmask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses {@code find=replace} lines and applies them to game text.
 * Longer find-strings are applied first so phrases win over words.
 */
public final class WordReplacer
{
	private List<Rule> rules = new ArrayList<>();
	private String[] needles = new String[0];
	private boolean caseSensitive;

	public void parse(String configText, boolean caseSensitive, boolean wholeWord)
	{
		parse(configText, caseSensitive, wholeWord, null);
	}

	public void parse(String configText, boolean caseSensitive, boolean wholeWord, String highlightHex)
	{
		this.caseSensitive = caseSensitive;

		Map<String, String> map = new LinkedHashMap<>();
		if (configText != null && !configText.isEmpty())
		{
			for (String line : configText.split("\\R"))
			{
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#"))
				{
					continue;
				}

				int eq = line.indexOf('=');
				if (eq <= 0)
				{
					continue;
				}

				String find = line.substring(0, eq).trim();
				String replace = line.substring(eq + 1).trim();
				if (!find.isEmpty())
				{
					map.put(find, replace);
				}
			}
		}

		List<Rule> parsed = new ArrayList<>(map.size());
		List<String> needleList = new ArrayList<>(map.size());
		int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
		boolean colorize = highlightHex != null && !highlightHex.isEmpty();

		for (Map.Entry<String, String> entry : map.entrySet())
		{
			String find = entry.getKey();
			String quoted = Pattern.quote(find);
			Pattern pattern = Pattern.compile(wholeWord ? "\\b" + quoted + "\\b" : quoted, flags);
			String plain = Matcher.quoteReplacement(entry.getValue());
			String colored = colorize
				? "<col=" + highlightHex + ">" + plain + "</col>"
				: plain;
			parsed.add(new Rule(pattern, plain, colored, find.length()));
			needleList.add(caseSensitive ? find : find.toLowerCase(Locale.ROOT));
		}

		parsed.sort(Comparator.comparingInt((Rule r) -> r.findLength).reversed());
		this.rules = parsed;
		this.needles = needleList.toArray(new String[0]);
	}

	public void clear()
	{
		rules = new ArrayList<>();
		needles = new String[0];
	}

	public boolean isEmpty()
	{
		return rules.isEmpty();
	}

	public String replace(String text)
	{
		return replace(text, true);
	}

	public String replace(String text, boolean color)
	{
		if (text == null || text.isEmpty() || rules.isEmpty() || !mightMatch(text))
		{
			return text;
		}

		String result = text;
		for (Rule rule : rules)
		{
			result = rule.pattern.matcher(result).replaceAll(color ? rule.colored : rule.plain);
		}
		return result;
	}

	private boolean mightMatch(String text)
	{
		String haystack = caseSensitive ? text : text.toLowerCase(Locale.ROOT);
		for (String needle : needles)
		{
			if (haystack.contains(needle))
			{
				return true;
			}
		}
		return false;
	}

	private static final class Rule
	{
		private final Pattern pattern;
		private final String plain;
		private final String colored;
		private final int findLength;

		private Rule(Pattern pattern, String plain, String colored, int findLength)
		{
			this.pattern = pattern;
			this.plain = plain;
			this.colored = colored;
			this.findLength = findLength;
		}
	}
}
