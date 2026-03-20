# At Lunch - Darryl Mak

At Lunch is an Android app for finding nearby restaurants using the Google Places API. On launch, the app requests location access, fetches nearby restaurant results, and lets the user explore them as either a scrollable list or on a map. Tapping a restaurant opens a detail screen with additional information such as address, phone number, rating, and photos when available.

**Required Features**
- [x] The app uses the Google Places API as its primary data source
- [x] The app prompts the user for permission to access their current location
- [x] Upon launch, the app executes a nearby search for restaurants
- [x] A search feature is included for free-text restaurant lookups
- [x] The user can switch between list and map result presentations
- [x] The user can select a restaurant to open a detail screen with basic information

**Non-Functional Requirements**
- [x] Jetpack Compose UI with distinct list and detail screens
- [x] Kotlin Coroutines and Flow
- [x] Local database and/or Network call to retrieve data that is populated to some portion of the UI
- [x] (Optional) Dependency Injection
- [ ] (Optional) Unit Tests

(insert visuals, video)

### Setup Instructions

#### Prerequisites
- Android Studio: Latest stable version recommended
- Java/JDK Version: JDK 17
- Android SDK
  - Compile SDK: 36
  - Target SDK: 36
  - Minimum SDK: 24
- Gradle Version: 8.13
- Android Gradle Plugin (AGP): 8.13.1
- Kotlin: 2.0.21

#### Open & Run Project
1. Open the project root in Android Studio.
2. Let Gradle sync complete.
3. Confirm Android Studio is using JDK 17 for Gradle.
4. Build and run the `app` configuration on an emulator or physical Android device.
5. Accept location permission when prompted so the app can perform nearby restaurant searches.

## Technical Details

### Architecture
- The app follows an **MVVM** structure with separate UI, domain, data, and networking responsibilities.
- Hilt is used to wire repositories, networking, and Android entry points.

### UI
- The UI is built with **Jetpack Compose** and Material 3.
- Navigation is handled with Navigation 3 and a simple back stack between the places screen and the details screen.
- The list screen supports loading, error, empty, list, and map states.
- The details screen renders place metadata and photo content returned by the Places API.

### Networking
- The app integrates with the [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview).
- Retrofit and Kotlinx Serialization are used for HTTP and JSON parsing.
- Google field masks are explicitly defined for search and detail requests so the app only requests the fields it needs.
- Network, backend, and unknown failures are mapped into user-facing error messages in the UI layer.

### Testing
- [ ] You can run the current test suite with `./gradlew clean testDebugUnitTest`.

---

## API Reference

**Base URL:** `https://places.googleapis.com/`

**Common headers**
- `X-Goog-Api-Key: <API_KEY>`
- `X-Goog-FieldMask: <field mask for the endpoint>`

| Endpoint | Description |
|------------------------------|---------|
| `POST /v1/places:searchNearby` | Returns nearby restaurant results for the main discovery screen. |
| `POST /v1/places:searchText` | Searches restaurants by a user-entered text query. |
| `GET /v1/places/{id}` | Fetches details for the selected restaurant. |
| `GET /v1/{photoName}/media` | Fetches photo media metadata for a place photo resource. |
