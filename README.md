# **At Lunch** - Darryl Mak

**Required Features**
- [x] The app will use the Google Places API for its data source
- [x] The app will prompt the user for permission to access their current location
- [x] Upon launch, the app will execute a search that displays nearby restaurants 
- [x] A search feature will be included that allows the user to search for restaurants 
- [ ] The user can choose to display the search results as a list, or as pins on a map
- [x] The user can select a search result to present a restaurant detail page with basic information about
the restaurant

(insert visuals, video)


### Setup Instructions
#### Prerequisites
- Android Studio: Latest stable version recommended
- Java/JDK Version: JDK 11
- Android SDK
    - Compile SDK: 36
    - Target SDK: 36
    - Minimum SDK: 24
- Gradle Version: 8.13
- Android Gradle Plugin (AGP): 8.13.1
- Kotlin: 2.0.21

## Technical Details

### Architecture
**MVVM** architecture
- Clear separation of concerns between UI, domain, and data layers

### UI
- **Jetpack Compose**

### Networking
- Integrate with [Google Places API](https://developers.google.com/maps/documentation/places/web-service/overview)
- Handle network errors gracefully.
- Proper error messages for the user.

### Testing
- run unit tests with `./gradlew clean testDebugUnitTest`
---

## API Reference
**Base URL:** `https://places.googleapis.com/`

**Common headers**
- `X-Goog-Api-Key: <API_KEY>`
- `X-Goog-FieldMask: <field mask for the endpoint>`

| Endpoint | Description |
|------------------------------|---------|
| `POST /v1/places:searchNearby` | Returns nearby restaurant results for the list screen. | 
| `POST /v1/places:searchText` | Searches restaurants by free-text query. | 
| `GET /v1/places/{id}` | Fetches the selected restaurant's detail page data. |
| `GET /v1/{photoName}/media` | Fetches photo media metadata for a place photo.|

