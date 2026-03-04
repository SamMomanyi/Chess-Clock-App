# ♟️ Chess Clock

A beautiful, fully-featured chess clock for Android built with modern Jetpack Compose and clean architecture.

> Screenshots & demo coming soon.

---

## Tech Stack & Skills Demonstrated

**Architecture & State Management**
- MVI pattern — unidirectional data flow via sealed `Command` / `Event` / `State` classes
- `StateFlow` + `combine` for reactive, composable UI state
- Hilt dependency injection across ViewModels and repositories

**Kotlin & Coroutines**
- Coroutine-based countdown timer running on `Dispatchers.Default`
- `Job` management for pause, resume, and cancellation without leaks
- `Channel` for one-shot UI events (navigation, toasts)

**UI / UX**
- 100% Jetpack Compose with Material 3
- 6 switchable themes (Midnight Steel, Royal Gold, Neon Cyber, Sunset Blaze + more) that persist across restarts
- Pulsing glow animation on the active player's card using `InfiniteTransition`
- Swipe-to-delete time controls, live theme preview swatches in Settings

**Data & Persistence**
- Room database with a pre-populated `.db` asset for default time controls
- Full CRUD — add, edit, delete custom time controls with form validation
- SharedPreferences for lightweight theme persistence

---

## Features at a Glance
- ⏱ Two-player countdown with increment support
- 🎨 6 built-in themes, switchable live from Settings
- ➕ Add / edit / delete custom time controls
- ⏸ Pause & resume — only the correct player can resume
- 🔢 Move counter + centisecond display

---

Built with Kotlin & ♟️for the love of the game
