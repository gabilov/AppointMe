# AppointMe 📅

**A native Android app for scheduling and managing appointments, with reminders, a calendar view and a clean Material design.**

Built in Kotlin with XML layouts as a mobile programming course project at Baku Engineering University.

**⬇️ [Download the APK](../../releases/latest)** · [Presentation (PPTX)](docs/PointMate%20App%20Presentation.pptx)

---

## Features
- **Account flow:** onboarding, register, login and a simulated password reset
- **Dashboard:** personal greeting and your next upcoming appointment
- **Appointments:** create, edit, view and delete appointments with date and time pickers
- **Filtering:** All / Upcoming / Past chips on the appointment list
- **Calendar:** tap a date to see that day's appointments and tasks
- **Reminders:** notifications before an appointment, scheduled with `AlarmManager`
- **Sharing and maps:** share an appointment through any app, or open its location in a maps app
- **Profile and settings:** edit your name and phone number, notification preferences, default reminder time

## Screens
Onboarding → Login / Register → Dashboard → Appointment list → Appointment detail / edit → Calendar → Profile → Settings
*(11 activities in total)*

## Architecture

```
app/src/main/java/com/beu/pointmate/
├── model/      Schedulable (interface), Appointment, Task, AppointmentStatus (enum)
├── data/       AppointmentRepository, AppointmentDatabaseHelper (SQLite),
│               AppointmentStorage (JSON), UserSession, SettingsManager
├── adapter/    AppointmentAdapter, SchedulableAdapter (mixed item types)
├── notify/     Notifier interface + Push / Email / SMS notifier implementations
└── *Activity   11 screens, plus ReminderScheduler / ReminderReceiver
```

**Key design decisions**
- **Polymorphism:** `Appointment` and `Task` both implement `Schedulable`, so the calendar shows one `List<Schedulable>` through a single adapter.
- **Repository pattern:** all screens go through `AppointmentRepository`, which hides whether data comes from SQLite or file storage.
- **Singletons** for app-wide state: the session, the settings and the repository.
- **Strategy-style notifiers:** one `Notifier` interface with interchangeable push, email and SMS implementations.
- **Intents:** explicit intents for moving between screens, implicit intents for `ACTION_SEND` (share) and `geo:` links (maps).

## Design system
| Token | Value |
|---|---|
| Primary | Calm Blue `#6366F1` |
| Accents | Teal `#22D3EE` · Purple `#A855F7` · Pink `#EC4899` |
| Status colors | Green = upcoming · Blue = completed · Red = cancelled |
| Shapes | 16dp rounded cards · 24dp pill buttons |
| Components | Material outlined text fields, filter chips, gradient onboarding |

## Tech stack
Kotlin · Android SDK (min 24, target 36) · XML layouts · Material Components · RecyclerView · SQLite · Gson · AlarmManager

## Run it locally
1. Clone the repo:
   ```bash
   git clone https://github.com/gabilov/AppointMe.git
   ```
2. Open the folder in **Android Studio**.
3. Run it on an emulator or a phone with Android 7.0 or newer.

You can also install the APK from [Releases](../../releases/latest) directly on an Android phone.

---
Designed and built by **Kanan Gabilov**.
