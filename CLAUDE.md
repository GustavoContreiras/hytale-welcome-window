# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build and deploy to server mods folder
mvn package

# Clean build
mvn clean package
```

The build automatically copies the JAR to `C:/dev/hytale/hytale-server/Server/mods`.

## Architecture

This is a Hytale server plugin that displays a configurable welcome window to players.

### Core Components

- **WelcomeWindowPlugin** - Entry point extending `JavaPlugin`. Registers the `/welcome` command and `PlayerReadyEvent` listener.
- **WelcomeWindowEvent** - Builds and displays the multi-page UI using HyUI's HTML-like syntax (HYUIML). Handles page navigation between menu buttons, back/next buttons, and done button.
- **WelcomeWindowCommand** - Async command handler for `/welcome` that opens the window on demand.
- **ConfigLoader** - Loads/creates JSON config from `mods/WelcomeWindow/config.json`. Creates a `.bak` file with defaults for reference.
- **WelcomeConfig/PageConfig** - POJOs for JSON deserialization with Gson.

### UI Building Pattern

The UI is built dynamically from config using HyUI's `PageBuilder`:
1. HTML string is constructed with config values (dimensions, text, page content)
2. `PageBuilder.detachedPage().fromHtml(html)` creates the page
3. Button event listeners are attached via `getById()` and `addEventListener()`
4. Pages are linked together for navigation

### Dependencies

- **HyUI** (`lib/HyUI-0.5.2-all.jar`) - UI library for building interfaces with HTML-like syntax. See `docs/HyUI - Hytale Mods - CurseForge.html` for documentation.
- **HytaleServer-parent** - Hytale server API (provided scope)
- **Gson** - JSON parsing for config

### Config Location

Runtime config: `{server_root}/mods/WelcomeWindow/config.json`
