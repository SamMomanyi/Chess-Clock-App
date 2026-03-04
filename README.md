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

![timeediting](https://github.com/user-attachments/assets/c328003d-77ea-4d17-af89-459794a5dacb)
![theme](https://github.com/user-attachments/assets/0a6e99ca-24a3-48e8-9d17-b0dc6e86cd8f)
![overalltimer](https://github.com/user-attachments/assets/18d78322-fcde-428f-abf1-c37968aba7df)
![deletion](https://github.com/user-attachments/assets/56ba3185-f91d-4407-8579-3db53e6139a6)

Built with Kotlin & ♟️for the love of the game

