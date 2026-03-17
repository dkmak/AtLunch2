# **At Lunch** - Darryl Mak

**Required Features**
- [ ] The app will use the Google Places API for its data source
- [ ] The app will prompt the user for permission to access their current location
- [ ] Upon launch, the app will execute a search that displays nearby restaurants 
- [ ] A search feature will be included that allows the user to search for restaurants 
- [ ] The user can choose to display the search results as a list, or as pins on a map
- [ ] The user can select a search result to present a restaurant detail page with basic information about
the restaurant

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
