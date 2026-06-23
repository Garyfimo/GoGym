This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

### Mocking the backend with Mockoon

The mock API configuration file is located at `../go-gym-mocks/go-gym.json` (sibling directory of the project).

You can run this mock server using the Mockoon Desktop app or the Mockoon CLI:

1. **Mockoon Desktop**: Open Mockoon, click "Open environment" (Ctrl+O / Cmd+O), and select the `go-gym.json` file.
2. **Mockoon CLI**: Run the following command:
   ```bash
   mockoon-cli start --data ../go-gym-mocks/go-gym.json
   ```

The mock API runs on `http://localhost:3000` (base path `/api`) with the following endpoints:
- `GET /api/exercises` - Lists available exercises
- `GET /api/exercises/:id` - Retrieves a specific exercise by ID
- `GET /api/routines` - Lists routines

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…