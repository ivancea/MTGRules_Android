# MTGRules Android

Android app for browsing, searching, and comparing historical Magic: The Gathering Comprehensive Rules. Rules are bundled locally; adding a file alone does not register it in the app.

## Working approach

- Investigate first: check `git status`, read the relevant implementation and tests, and follow nearby conventions before editing.
- Keep changes focused. Preserve existing user work and historical rules; avoid unrelated dependency upgrades or refactors.
- Report what changed, which checks ran, and any checks blocked by missing configuration or devices.
- Keep credentials and private operational details out of commits, logs, and documentation.

## Tooling and layout

- Single `app` module, primarily Kotlin with some Java; Jetpack Compose/Material 3, ViewModel/StateFlow, Hilt with KSP, and JUnit 5.
- Use the Gradle wrapper (`./gradlew`, or `.\gradlew.bat` in PowerShell). Check `build.gradle`, `app/build.gradle`, and `gradle/wrapper/gradle-wrapper.properties` for current versions; Java source/target compatibility is 17. Use a JDK supported by the current Gradle/Android plugin and install the configured Android SDK.
- Code lives under `app/src/main/java/com/ivancea/MTGRules/`:
  - `MainActivity.kt` and `presentation/`: Android integration, Compose UI, and `MainViewModel` state.
  - `services/`: rules registry/loading, comparison, storage, and platform integrations.
  - `model/`: rule tree, sources, and diffs; `utils/`: parser, search, and navigation helpers.
  - `constants/Symbols.kt`: symbol mappings, with artwork in `app/src/main/res/drawable/`.
- `app/src/main/res/raw/rules_YYYYMMDD.txt`: bundled source documents.
- `app/src/test/`: JVM unit tests; `app/src/androidTest/`: device/emulator tests. `.github/workflows/`: build/test CI and new-rules notifications.

## Adding a ruleset

1. Obtain the official Wizards text and verify its effective date and encoding. Save it as `app/src/main/res/raw/rules_YYYYMMDD.txt`, preserving the published content.
2. Append a `RulesSource` in `services/RulesService.kt` with the exact source URI, effective date, and charset. Keep chronological order: the last entry is the default/latest edition. The date determines the resource filename; do not assume every download is UTF-8.
3. Bump `versionCode` in `app/build.gradle`, following previous rules updates. Routine rules updates do not require a `versionName` change.
4. Run unit and instrumentation tests below. `RulesServiceInstrumentationTest` automatically checks every registered edition for resource loading, rule structure/order, duplicate IDs, glossary completion, and symbol mappings.
5. Investigate failures against the original text. Whitespace around examples/glossary entries has broken parsing before. Apply narrowly date-scoped fixes in `utils/RulesParser.kt` when needed; preserve historical fixes and do not extend them to a new edition without checking. Add genuinely new symbol mappings/artwork when required.
6. Review unusually large diffs against the source documents. `RulesComparisonService` ignores rule-reference numbers within text, but matches entries by exact rule ID; moved/renumbered sections still produce large diffs.

The new-rules notification workflow uses the GitHub repository variable `LAST_RULES_URL`; remind the maintainer to update it after integrating a new source.

## Build and test

Run from the repository root (substitute `.\gradlew.bat` on Windows):

```sh
./gradlew assembleDebug                # Build debug APK
./gradlew testDebugUnitTest            # JVM tests; no emulator needed
./gradlew connectedDebugAndroidTest    # Instrumentation tests; boot an AVD or connect a device first
```

- Use `adb devices` to confirm a test device is available. Unit tests alone do not validate Android packaging/resource loading.
- Prefer the existing instrumentation coverage for rules updates; do not add test resource source sets or duplicate that suite just to load bundled files in JVM tests.
- Reports are under `app/build/reports/`; instrumentation results are under `app/build/outputs/androidTest-results/`.
