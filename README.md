# VectorScan

An Xposed/LSPosed (Vector) module that scans apps in its scope and dumps their classes, fields and methods to a text file. Step 1 of a larger tool for inspecting and editing app values at runtime.

- Legacy Xposed API 82, Kotlin
- Target: Android 10 (SDK 29), Vector LSPosed

## Build

The GitHub Actions workflow (`.github/workflows/android.yml`) builds a debug APK on every push.

1. Push this project to a GitHub repo.
2. Open the **Actions** tab and run **Android CI** (it also runs on push).
3. Download the `VectorScan-debug` artifact from the finished run.

Pushing a tag like `v0.1` also attaches the APK to a GitHub release.

Local build: `gradle assembleDebug` (Gradle 8.2+, JDK 17).

## Use

1. Install the APK and enable the module in Vector.
2. Add the apps you want to scan to its scope.
3. Force-stop and reopen the target app.
4. Read the dump at `/sdcard/Android/data/<target.package>/files/vectorscan_dump.txt`.

Set `ONLY_APP_PACKAGE = false` in `Scanner.kt` to dump every class, not just the app's own package.
