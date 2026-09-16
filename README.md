# Word Mask

A RuneLite plugin that replaces words in **NPC dialog, books, quest journals, chat, overhead speech, and menus**.

Example: `Gielinor=Runescape`

## Config

Open **Configuration → Word Mask**.

| Setting | Default | Meaning |
| --- | --- | --- |
| Replacements | `Gielinor=Runescape` | One `find=replace` pair per line |
| Case sensitive | off | Off matches any casing |
| Whole word | on | On only replaces complete words |

```
Gielinor=Runescape
# comments start with #
Lumbridge=Tutorial Town
```

## What it touches

- **Widgets** — dialog, books, interfaces, journals (reapplied each frame so client scripts cannot overwrite it)
- **Chat** — displayed message text
- **Overhead speech** — NPC/player speech bubbles
- **Menus** — right-click option and target text

It does **not** rewrite the chat input while you type, and it does not change 3D-world nametags that the engine draws (those are not text widgets). NPC names in menus and dialog **are** replaced.

## Run locally

Requires JDK 11+.

```
./gradlew run
```

Log in, enable **Word Mask**, talk to an NPC or open a book.

## Plugin Hub

1. This repository is public.
2. Fork [runelite/plugin-hub](https://github.com/runelite/plugin-hub).
3. Add `plugins/word-mask`:

```
repository=https://github.com/austin43/word-mask.git
commit=<full 40-character commit hash>
```

4. Open a pull request.
