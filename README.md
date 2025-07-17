# Criticaltech

A Kotlin Android app that displays BBC News top headlines using the NewsAPI.

This project was developed as a code challenge to demonstrate clean architecture, testability, responsiveness, and modern Android development practices.

- Fetches top headlines from BBC News using NewsAPI
- Displays headlines in a scrollable list, sorted by date
- Displays headline title and image
- Supports portrait and landscape modes seamlessly
- Allows tapping on a headline to view detailed information, including image, title, description, and content
- Biometric authentication (fingerprint or face if it's set) on launch if available and configured
- Supports build flavors to change the news source easily
- Written in Kotlin, targeting the latest Android API level
- Built using latest stable Android Studio
- Includes unit tests for core logic

# Tech Stack

- Kotlin
- Retrofit for HTTP requests
- Gson for JSON parsing
- Coil for image downloading and caching
- Jetpack ViewModel and Flow for state management
- Material Design components with Jetpack Compose
- Biometric authentication for fingerprint/face (when device is set for it)
- JUnit/Mockito for unit testing
- UI tests

# Build Flavors

The project supports build flavors to fetch headlines from different sources: BBC News, Blasting News (BR) and ESPN
