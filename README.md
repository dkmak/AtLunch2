# At Lunch - Darryl Mak

At Lunch is an Android app for finding nearby restaurants using the Google Places API. On launch,
the app requests location access, fetches nearby restaurant results, and lets the user explore them
as either a scrollable list or on a map. Tapping a restaurant opens a detail screen with additional
information such as address, phone number, rating, and photos when available.

**Features**
-  The app uses the Google Places API for its data source
- The app will prompt the user for permission to access their current location
- Upon launch, the app will execute a search that displays nearby restaurants
- A search feature will be included that allows the user to search for restaurants
- The user can choose to display the search results as a list, or as pins on a map
- The user can select a search result to present a restaurant detail page with basic information
  about the restaurant

**Non-Functional Features**
- Jetpack Compose for UI, comprising two distinct screens (e.g. list/detail)
- Kotlin Flows and Coroutines - Modern android development practices (Jetpack, Clean
  Architecture)
- Local database and/or Network call to retrieve data that is populated to some portion of the
  UI (See *Data Persistence* for details)
- Dependency Injection using Hilt
- Unit Tests

| Video | List | Map | Details |
|-------|------|---- |---------|
| <video src="https://github.com/user-attachments/assets/347421c5-f23f-4ae6-b89c-8d63930acb01" width="200"></video>      | <img width="200" alt="Screenshot 2026-03-21 at 9 13 52 PM" src="https://github.com/user-attachments/assets/4ff439ea-e10f-47a8-a82b-9365e48abfc7" />  | <img width="200" alt="Screenshot 2026-03-21 at 9 14 18 PM" src="https://github.com/user-attachments/assets/b27d377b-db86-484d-b19e-22feb82d24a0" /> | <img width="200" alt="Screenshot 2026-03-21 at 9 14 41 PM" src="https://github.com/user-attachments/assets/cf6eed8a-c3a8-4539-8b82-653a58dccb44" /> |


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
4. Add your Google API keys to your local, untracked `local.properties` file:

```properties
GOOGLE_PLACES_API_KEY=your_places_api_key
GOOGLE_MAPS_API_KEY=your_maps_sdk_api_key
```

5. Build and run the `app` configuration on an emulator or physical Android device.
6. Accept location permission when prompted so the app can perform nearby restaurant searches.


## Technical Details

### Architecture

- The app follows an **MVVM** structure with separate UI, domain, data, and networking
  responsibilities.
- Hilt is used to wire repositories, networking, and Android entry points.

### UI

- The UI is built with **Jetpack Compose** and Material 3.
- Navigation is handled with Navigation 3 and a simple back stack between the places screen and the
  details screen.
- The list screen supports loading, error, empty, list, and map states.
- The details screen renders place metadata and photo content returned by the Places API.

### Networking

- The app integrates with
  the [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview).
- Retrofit and Kotlinx Serialization are used for HTTP and JSON parsing.
- Google field masks are explicitly defined for search and detail requests so the app only requests
  the fields it needs.
- Network, backend, and unknown failures are mapped into user-facing error messages in the UI layer.

### Data Persistence

- Room is used to cache nearby place preview results locally.
- The repository clears and refreshes cached nearby results on successful nearby searches, then
  emits the database-backed models to the UI layer.
- Separate mapper extensions translate between API DTOs, Room entities, and domain models to keep
  data-layer responsibilities explicit.

### Testing

- You can run the current test suite with `./gradlew clean testDebugUnitTest`.

---

## API Reference

**Base URL:** `https://places.googleapis.com/`

**Common headers**

- `X-Goog-Api-Key: <API_KEY>`
- `X-Goog-FieldMask: <field mask for the endpoint>`

| Endpoint                       | Description                                                      |
|--------------------------------|------------------------------------------------------------------|
| `POST /v1/places:searchNearby` | Returns nearby restaurant results for the main discovery screen. |
| `POST /v1/places:searchText`   | Searches restaurants by a user-entered text query.               |
| `GET /v1/places/{id}`          | Fetches details for the selected restaurant.                     |
| `GET /v1/{photoName}/media`    | Fetches photo media metadata for a place photo resource.         |
