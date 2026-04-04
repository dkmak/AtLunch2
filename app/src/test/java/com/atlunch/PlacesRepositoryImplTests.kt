
        val result = repository.searchNearby(
            lat = 40.0,
            long = -73.0
        ).first()

        assertThat(result).isEqualTo(
            PlacesResult.PlacesSuccess(
                listOf(
                    PlacePreview(
                        restaurantName = "Cafe 123",
                        id = "place-1",
                        rating = 4.5,
                        userRatingCount = 120,
                        shortFormattedAddress = "123 Main St",
                        location = Location(37.0, -122.0),
                        iconBaseUri = "https://example.com/icon.png"
                    )
                )
            )
        )
    }
}