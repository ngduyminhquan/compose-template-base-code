# AGENTS.md

Compact handbook for OpenCode sessions in this repo. Only repo-specific gotchas
are listed; standard Android/Kotlin conventions are omitted.

## Project shape

- Single Gradle module: `:app` only. `settings.gradle.kts` declares the project
  name `Compose Project` (with a space). No multi-module/feature split.
- App package: `com.project.composeproject`. Debug builds append
  `.dev` to `applicationId` and `-dev` to `versionName` (`app/build.gradle.kts`).
- Toolchain: AGP 8.12.2, Kotlin 2.1.0, KSP 2.1.0-1.0.29, JVM target 11,
  `compileSdk = 36`, `minSdk = 24`. Compose BOM `2026.03.01` and
  `androidx.lifecycle 2.10.0` are pinned ahead of typical stable; do not
  "fix" them.
- `dependencyResolutionManagement` is set to `FAIL_ON_PROJECT_REPOS`. New
  repositories must be added in `settings.gradle.kts`, never in
  `app/build.gradle.kts`.

## Build / run

- Windows shell here is PowerShell. Use `./gradlew.bat <task>` (or
  `& .\gradlew.bat`). The wrapper scripts at the repo root are the source of
  truth.
- Two build types matter: `debug` (suffixed `.dev`, AdMob test app id,
  unminified) and `release` (R8 + shrink resources, real AdMob id, Crashlytics
  mapping upload enabled). Do not enable minify on debug.
- `app/google-services.json` is gitignored but present locally and required at
  build time. Don't delete it; don't commit it.
- No test sources exist yet (`app/src/test/java`, `app/src/androidTest/java`
  are empty) and `build.gradle.kts` declares no test dependencies. Adding tests
  means adding the test dependencies first.
- Stale `build_error*.log` and `build_output.log` files at the repo root are
  scratch outputs from earlier runs. Ignore unless debugging.

## Architecture conventions (enforced)

These rules are codified in `.opencode/skills/architecture-designer/rules/` and
`.agent/rules/code-style-guide.md`. Follow them strictly when adding screens
or repositories - the existing code does.

- **MVI ViewModel** (`viewmodel-mvi-rule.md`):
  - `XxxUiState` is a `data class` holding all screen state.
  - `XxxIntent` is a `sealed class`/`sealed interface`. ViewModel exposes
    `fun onIntent(intent: XxxIntent)` and dispatches with `when`. Never expose
    a `Flow`/`StateFlow`/`SharedFlow` to receive intents.
  - `XxxSideEffect` is a `sealed class` emitted via `SharedFlow` for one-shot
    events (navigation, toast, open url).
- **Repository** (`repository-rule.md`):
  - Every return is wrapped in `domain/model/DataResult.kt`
    (`Success` / `Error(Exception)` / `Loading`).
  - Realtime/list reads return `Flow<DataResult<T>>`; one-shot actions are
    `suspend fun ... : DataResult<T>`.
  - Implementations go in `data/repository/`, interfaces in
    `domain/repository/`. Bind via Hilt `@Binds` in
    `di/RepositoryModule.kt` (see `bindChannelRepository` for the pattern).
  - Reuse helpers in `data/utils/ResultExtension.kt`: `Result<T>.toDataResult()`,
    `Result<Unit>.toUnitDataResult()`, `Throwable.toException()`, `now()`.
    Match `ChannelRepositoryImpl.kt` for the canonical
    `onStart { emit(Loading) } + map(Success) + catch(Error)` pattern.
- **Code style** (`.agent/rules/code-style-guide.md`):
  - Do not leave comments (`//`, `/* */`) in production Kotlin. The rule is
    enforced repo-wide; the few existing files have none. Self-explanatory
    naming only.
  - YAGNI: don't add abstractions or fields beyond what the current SRS/task
    requires. Don't refactor unrelated code while implementing a feature.
  - No deprecated APIs.

## Navigation (Navigation 3, not 2)

- `ui/navigation/AppNavigation.kt` uses `androidx.navigation3` with
  `rememberNavBackStack`, `NavDisplay`, `entryProvider { entry<Route.X> { ... } }`.
  There is no `NavController`/`NavHost`/`composable("route")`.
- Routes are `@Serializable` Kotlin types implementing `Route : NavKey`
  (`ui/navigation/Route.kt`). Add a new screen by adding a `Route` subtype and
  an `entry<Route.X>` block.
- Pop the back stack with `backStack.removeAt(backStack.size - 1)`; push with
  `backStack.add(Route.X)`. Do not introduce Nav2 helpers.
- Nav3 ViewModels are scoped via `rememberViewModelStoreNavEntryDecorator()`
  registered in `entryDecorators`. Use `hiltViewModel()` inside an `entry { }`.

## Activities and theming

- `SplashActivity` (XML + ViewBinding + AppCompat, theme `Theme.XmlScreen`)
  is the LAUNCHER. It plays `assets/video_splash.mp4` via Media3 ExoPlayer
  for a hard-coded 5s, then starts `LauncherActivity` and calls
  `finishAffinity()`.
- `LauncherActivity` is the Compose host (`@AndroidEntryPoint`, theme
  `Theme.ComposeScreen`). It calls `setContent { AppNavigation() }`.
- Both override `attachBaseContext` / `applySystemBarsSetting()` for locale
  and edge-to-edge - call these helpers when adding new Activities.
- `buildFeatures { compose = true; viewBinding = true; buildConfig = true }`
  are all on; ViewBinding is in active use by `SplashActivity` only.

## Localization

- Runtime locale switching lives in `utils/LanguageUtils.kt` with
  `LANGUAGE_PREFS` / `KEY_LANGUAGE`. Activities must override
  `attachBaseContext` and call `LanguageUtils.createLocalizedContext(...)`,
  matching `LauncherActivity.kt`. Compose-only language changes will not
  persist across activity recreation without it.
- Supported language codes are hard-coded in `LanguageUtils.supportedLanguages`.
  AAB language splits are disabled (`bundle.language.enableSplit = false`)
  - all locales ship in the base APK.

## Ads / Firebase

- Ads SDK: `com.github.trongluan99:AdsNextGen` from JitPack
  (declared in `settings.gradle.kts`). `AdsManager.initialize()` is called
  from `GlobalApplication.onCreate()`.
- `AdsManager.kt` uses placeholder tokens (`"thuongok"`) for Adjust/Facebook -
  swap them for real values before release; debug builds use the AdMob test
  app id `ca-app-pub-3940256099942544~3347511713`.
- `BuildConfig.APP_ID` is generated per build type and passed to the manifest
  via `manifestPlaceholders["app_id"]`. Don't hard-code it in code.
- Firebase Crashlytics, Analytics, Messaging, Config are wired through the
  Firebase BOM. `google-services.json` must be in place for assembly.

## Documentation workflow

- Per-screen docs live under `docs/<screen-name>/`:
  - `00-plan.md` - workflow plan/tracking file owned by the
    `feature-workflow-orchestrator` skill (see below).
  - `01-srs.md` - functional spec only (no tech).
  - `02-architecture.md` (legacy) or `02-ui-layer.md` + `03-data-layer.md`
    (current pattern - see `docs/iap-screen/`).
- The `.opencode/skills/` directory contains the generators
  (`srs-generator`, `architecture-designer`, `ui-android-compose`,
  `ui-android-xml`, `android-resource-policy`, `android-code-indexer`,
  `feature-workflow-orchestrator`). Use the `skill` tool when a task matches
  their description; do not duplicate their workflow ad hoc.
- For end-to-end feature work (idea -> code), prefer the
  `feature-workflow-orchestrator` skill. It chains
  `srs-generator` -> `architecture-designer` -> Data Layer impl ->
  `ui-android-compose` -> verify, manages `docs/<feature>/00-plan.md`, and
  enforces a user-confirmation gate between phases. Don't run the sub-skills
  ad hoc when a full feature is requested - the orchestrator owns the order
  and the plan file.
- `.agent/skills/` mirrors `.opencode/skills/` for non-OpenCode tools.
  Treat `.opencode/skills/` as the source of truth for OpenCode sessions.

## Things to avoid

- Don't add a module-level repository - it will fail because of
  `FAIL_ON_PROJECT_REPOS`.
- Don't introduce Navigation 2 (`NavHost`, `composable("route")`,
  `NavController`) - it will conflict with Nav3 already in use.
- Don't add Kotlin comments to new files (style rule above).
- Don't downgrade Compose BOM / Lifecycle versions to "match" stable - the
  pinned versions are intentional.
- Don't rename the Gradle root project (`Compose Project`) - the archive
  base name in `app/build.gradle.kts` depends on it.
