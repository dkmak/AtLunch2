# **Android App Template** - Darryl Mak

(Description):
1. **Action 1** - Display a list of available sports leagues
2. **Action 2** - Browse teams within a selected league
3. **Action 3** - View detailed information about a team

(insert visuals, video)


### Setup Instructions
#### Prerequisites
- Android Studio:
- Java/JDK Version:
- •Android SDK
    - Compile SDK:  
    - Minimum SDK:  
- Gradle Version: 

#### Open & Run Project
1. Unzip the project
2. Open Android Studio
3. Select "Open" and choose the root project directory
4. Let Gradle sync complete
5. run unit tests with `./gradlew clean testDebugUnitTest`

## Technical Details

### Architecture
**MVVM** architecture
- Clear separation of concerns between UI, domain, and data layers

### UI
- **Jetpack Compose**
- Proper loading, error, and empty states

### Networking
- Integrate with [API](**insert api here**)
- Handle network errors gracefully.
- Proper error messages for the user.

### Modularization
- Implement features and other architectural components in separate modules.
- Maintain proper dependency direction
- Keep modules focused and single-purpose.

### Testing
- Includes unit tests for ViewModels and Repositories

### Build Configuration
- Debug and release build types are configured
- ProGuard/R8 is enabled for release builds

---

## API Reference
**Base URL:** `insert base url`

| Endpoint                     | Description | Example |
|------------------------------|---------|--------|
| `ex1`                        | | N/A |
| `ex2`                        | |  |
