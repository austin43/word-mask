package com.wordmask;

import org.junit.Assert;
import org.junit.Test;

public class WordReplacerTest
{
	@Test
	public void replacesGielinor()
	{
		WordReplacer replacer = replacer("Gielinor=Runescape", false, true);
		Assert.assertEquals("Welcome to Runescape!", replacer.replace("Welcome to Gielinor!"));
	}

	@Test
	public void isCaseInsensitiveByDefault()
	{
		WordReplacer replacer = replacer("Gielinor=Runescape", false, true);
		Assert.assertEquals("Welcome to Runescape!", replacer.replace("Welcome to gielinor!"));
		Assert.assertEquals("Welcome to Runescape!", replacer.replace("Welcome to GIELINOR!"));
	}

	@Test
	public void caseSensitiveHonoursExactCasing()
	{
		WordReplacer replacer = replacer("Gielinor=Runescape", true, true);
		Assert.assertEquals("Welcome to Runescape!", replacer.replace("Welcome to Gielinor!"));
		Assert.assertEquals("Welcome to gielinor!", replacer.replace("Welcome to gielinor!"));
	}

	@Test
	public void wholeWordDoesNotReplaceInsideLongerWords()
	{
		WordReplacer replacer = replacer("cat=dog", false, true);
		Assert.assertEquals("catalog", replacer.replace("catalog"));
		Assert.assertEquals("the dog sat", replacer.replace("the cat sat"));
	}

	@Test
	public void substringModeReplacesInsideLongerWords()
	{
		WordReplacer replacer = replacer("cat=dog", false, false);
		Assert.assertEquals("dogalog", replacer.replace("catalog"));
	}

	@Test
	public void preservesColourTags()
	{
		WordReplacer replacer = replacer("Gielinor=Runescape", false, true);
		Assert.assertEquals(
			"Welcome to <col=ff9040>Runescape</col>!",
			replacer.replace("Welcome to <col=ff9040>Gielinor</col>!"));
	}

	@Test
	public void longerPhrasesWin()
	{
		WordReplacer replacer = replacer("Gielinor=Runescape\nGielinor Games=OSRS", false, true);
		Assert.assertEquals("Watch OSRS tonight", replacer.replace("Watch Gielinor Games tonight"));
		Assert.assertEquals("Welcome to Runescape", replacer.replace("Welcome to Gielinor"));
	}

	@Test
	public void ignoresCommentsAndMalformedLines()
	{
		WordReplacer replacer = replacer("# ignore me\n\n=nope\nGielinor=Runescape\nbadline", false, true);
		Assert.assertEquals("Runescape", replacer.replace("Gielinor"));
	}

	@Test
	public void emptyReplacementMasksTheWord()
	{
		WordReplacer replacer = replacer("Gielinor=", false, true);
		Assert.assertEquals("Welcome to !", replacer.replace("Welcome to Gielinor!"));
	}

	@Test
	public void dollarInReplacementIsLiteral()
	{
		WordReplacer replacer = replacer("gold=$gp", false, true);
		Assert.assertEquals("100 $gp", replacer.replace("100 gold"));
	}

	@Test
	public void lastDuplicateFindWins()
	{
		WordReplacer replacer = replacer("Gielinor=World\nGielinor=Runescape", false, true);
		Assert.assertEquals("Runescape", replacer.replace("Gielinor"));
	}

	@Test
	public void emptyConfigIsNoop()
	{
		WordReplacer replacer = replacer("", false, true);
		Assert.assertTrue(replacer.isEmpty());
		Assert.assertEquals("Gielinor", replacer.replace("Gielinor"));
	}

	@Test
	public void highlightWrapsReplacementInColourTag()
	{
		WordReplacer replacer = new WordReplacer();
		replacer.parse("Gielinor=Runescape", false, true, "8b008b");
		Assert.assertEquals(
			"Welcome to <col=8b008b>Runescape</col>!",
			replacer.replace("Welcome to Gielinor!"));
	}

	@Test
	public void highlightCanBeSkippedForOverhead()
	{
		WordReplacer replacer = new WordReplacer();
		replacer.parse("Gielinor=Runescape", false, true, "8b008b");
		Assert.assertEquals("Welcome to Runescape!", replacer.replace("Welcome to Gielinor!", false));
	}

	@Test
	public void highlightNestsInsideExistingColourTags()
	{
		WordReplacer replacer = new WordReplacer();
		replacer.parse("Gielinor=Runescape", false, true, "8b008b");
		Assert.assertEquals(
			"Welcome to <col=ff9040><col=8b008b>Runescape</col></col>!",
			replacer.replace("Welcome to <col=ff9040>Gielinor</col>!"));
	}

	private static WordReplacer replacer(String config, boolean caseSensitive, boolean wholeWord)
	{
		WordReplacer replacer = new WordReplacer();
		replacer.parse(config, caseSensitive, wholeWord);
		return replacer;
	}
}
