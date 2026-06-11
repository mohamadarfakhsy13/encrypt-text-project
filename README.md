# Pi-Xcels Encryption Utility

An advanced, production-grade Android application demonstrating secure UTF-8 string encryption using a native C++ core and modern Clean Architecture. Built to showcase senior engineering standards in security, scalability, and UI/UX design.

---

## 🚀 Project Highlights

### 🏛️ Clean Architecture & Scalability
The project is architected with a strict separation of concerns, ensuring high maintainability and testability:
- **Domain Layer**: Pure business logic containing `EncryptUseCase`.
- **Data Layer**: Abstracted `EncryptionRepository` with a dedicated `NativeEncryptionDataSource` for JNI orchestration.
- **UI Layer**: Reactive ViewModels utilizing `StateFlow` to drive Jetpack Compose screens.
- **Dependency Injection**: Fully integrated with **Dagger Hilt** for modularity and seamless lifecycle management.

### 🔐 Hardened Native Core (JNI)
The encryption engine is implemented in C++ for maximum logic obfuscation and performance:
- **Native Orchestration**: Key download and RSA-OAEP-256 encryption are orchestrated from the native side, as per the primary project requirement.
- **Exception Safety**: Implemented robust JNI exception checking (`env->ExceptionCheck()`) to prevent crashes and provide graceful error recovery.
- **Memory Management**: Manual management of JNI local references (`env->DeleteLocalRef()`) ensures zero memory leaks during orchestration.
- **Standard UTF-8 Compliance**: Bypassed "Modified UTF-8" limitations by utilizing `String.getBytes("UTF-8")` via JNI, ensuring 100% support for all character ranges (e.g., complex emojis and technical symbols).

### 📊 Production-Grade Observability
- **Analytics Abstraction**: Implemented a scalable `AnalyticsTracker` interface to decouple the app from specific providers.
- **Event Tracking**: Automatically logs `ENCRYPTION_ATTEMPT` and `ENCRYPTION_RESULT` (including success, error types, and metadata) for operational monitoring.

---

## ✨ Advanced UI/UX

- **Jetpack Navigation 3**: Utilizes the latest state-driven navigation architecture for robust backstack management.
- **Material 3 Adaptive**: Implements a responsive **List-Detail pattern** using `ListDetailPaneScaffold`, ensuring an optimized experience across phones, tablets, and foldables.
- **Edge-to-Edge**: Full edge-to-edge immersive display handling with proper `WindowInsets` integration.
- **Corporate Branding**:
    - **Palette**: Custom "Pi-Xcels Green" high-visibility tech scheme.
    - **Adaptive Icon**: A pixel-inspired "π" (Pi) shield symbol, reflecting the corporate identity.

---

## 🧪 Testing Strategy

The application is backed by a comprehensive automated testing suite:
- **Unit Tests**:
    - **Turbine**: Used for high-fidelity testing of Kotlin `StateFlow` transitions.
    - **MockK**: Facilitates robust mocking of repositories, use cases, and analytics.
    - **Test Coverage**: Covers state machines, logic branching, and error handling across all layers.
- **Instrumented UI Tests**:
    - **Hilt Testing**: End-to-end verification of the full encryption flow using `@HiltAndroidTest`.
    - **Compose Test**: Validates UI integrity, accessibility, and navigation consistency.

---

## 🛠️ Technical Stack

- **Language**: Kotlin 2.1+ / C++ (C++17)
- **Framework**: Jetpack Compose (Material 3)
- **Dependency Injection**: Dagger Hilt
- **Concurrency**: Kotlin Coroutines & Flow
- **Native Build**: CMake 3.22.1 / NDK 23+
- **Minimum SDK**: 30 (Android 11)

---

## 📐 Architectural Decision Records (ADR)

### Why Native Orchestration?
**Decision**: Orchestrate key retrieval and encryption logic within the C++ layer via JNI callbacks.
**Reasoning**: This provides an additional layer of obfuscation for sensitive logic while allowing the use of safe, system-provided networking and cryptographic providers.

### Why Clean Architecture?
**Decision**: Implement a strict Domain-Data-UI separation.
**Reasoning**: Decoupling ensures that business rules (Encryption) are independent of both the UI (Compose) and the specific implementation of data sources (JNI). This maximizes testability and allows for future scalability.

---

## 🔨 Compilation Instructions

To compile and run this application:

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd pixcels_assessment
   ```
2. **Setup NDK**:
   Ensure the Android NDK and CMake are installed via the SDK Manager in Android Studio.
3. **Build**:
   - Via Studio: `Build > Make Project`
   - Via CLI: `./gradlew assembleDebug`
4. **Run**:
   Ensure a device/emulator (API 30+) is connected and run via the Play button or `./gradlew installDebug`.
5. **Run Tests**:
   - Unit Tests: `./gradlew testDebugUnitTest`
   - UI Tests: `./gradlew connectedDebugAndroidTest`
