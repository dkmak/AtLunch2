# At Lunch - Darryl Mak

At Lunch is an Android app for finding nearby restaurants using the Google Places API. On launch,
the app requests location access, fetches nearby restaurant results, and lets the user explore them
as either a scrollable list or on a map. Tapping a restaurant opens a detail screen with additional
information such as address, phone number, rating, photos, favorites, and an AI-generated summary
when available.

**Features**
-  The app uses the Google Places API for its data source
- The app will prompt the user for permission to access their current location
- Upon launch, the app will execute a search that displays nearby restaurants
- A search feature will be included that allows the user to search for restaurants
- The user can choose to display the search results as a list, or as pins on a map
- The user can select a search result to present a restaurant detail page with basic information
  about the restaurant
- The user can favorite or unfavorite a restaurant from the details screen
- The user can open a custom share sheet from the details screen to share a restaurant address or
  open the restaurant in Google Maps
- The user can request a short AI-generated "Why Come Here?" summary on the details screen

**Non-Functional Features**
- Jetpack Compose for UI, comprising two distinct screens (e.g. list/detail)
- Kotlin Flows and Coroutines - Modern android development practices (Jetpack, Clean
  Architecture)
- Local database and/or Network call to retrieve data that is populated to some portion of the
  UI (See *Data Persistence* for details)
- Dependency Injection using Hilt
- Unit Tests
- OpenAI Responses API integration for lightweight on-device AI enrichment

| Video | List | Map | Details |
|-------|------|---- |---------|
| <video src="https://github.com/user-attachments/assets/946cea35-c7b6-4828-9bc0-1ec9d00dae66" width="200"></video>      | <img width="200" alt="Screenshot 2026-03-21 at 9 13 52 PM" src="https://github.com/user-attachments/assets/24e04ed4-d15b-4ba6-a181-96c3c28b2425" />  | <img width="200" alt="Screenshot 2026-03-21 at 9 14 18 PM" src="https://github.com/user-attachments/assets/e16ef256-20ec-4d99-81cd-7fdfaf9e5d4a" /> | <img width="200" alt="Screenshot 2026-03-21 at 9 14 41 PM" src="https://github.com/user-attachments/assets/265b05f7-88cb-40d5-bc56-7e3badbc0ded" /> |

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
4. Add your API keys to your local, untracked `local.properties` file:

```properties
GOOGLE_PLACES_API_KEY=your_places_api_key
GOOGLE_MAPS_API_KEY=your_maps_sdk_api_key
OPENAI_API_KEY=your_openai_api_key
```

5. Build and run the `app` configuration on an emulator or physical Android device.
6. Accept location permission when prompted so the app can perform nearby restaurant searches.


## Technical Details

### Client Architecture
<img width="800" alt="AtLunch_Diagram" src="https://github.com/user-attachments/assets/f1c9c0a5-bcc4-4832-b573-abb7c42f0387" />

- The app uses a clean architecture with separate UI, domain, data
  responsibilities.
- Hilt is used for dependency injection.

### Data Layer: Networking

- The app integrates with
  the [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview).
- The app also integrates with the [OpenAI Responses API](https://platform.openai.com/docs/api-reference/responses/create)
  to generate a short "Why Come Here?" explanation from structured restaurant details.
- Retrofit and Kotlinx Serialization are used for HTTP and JSON parsing.
- Google field masks are explicitly defined for search and detail requests so the app only requests
  the fields it needs.
- Network, backend, and unknown failures are mapped into user-facing error messages in the UI layer.

#### API Reference

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

#### AI Summary API Reference

**Base URL:** `https://api.openai.com/`

**Common headers**
- `Authorization: Bearer <OPENAI_API_KEY>`
- `Content-Type: application/json`

| Endpoint             | Description                                                             |
|----------------------|-------------------------------------------------------------------------|
| `POST /v1/responses` | Generates a short AI summary for why a restaurant may be a good choice. |

### Data Layer: Data Persistence

- Room is used to cache nearby place preview results locally.
- If the connection is the lost, the user will still see locally cached nearby restaurant data.
- Successful results from network are cached by the network and emitted by the UI.
- Room is also used to persist favorited restaurants locally.

### UI
- The presentation layer follows an **MVVM** structure.
- The UI is built with **Jetpack Compose** and Material 3.
- Navigation is handled with Navigation 3 and a simple back stack between the places screen and the
  details screen.
- The list screen supports loading, error, empty, list, and map states.
- The details screen renders place metadata and photo content returned by the Places API.
- The details screen also supports toggling favorites and requesting a short AI-generated summary.
- Map overlays are provided using the Google Maps Compose SDK

### Testing
- You can run the current test suite with `./gradlew clean testDebugUnitTest`.
