package com.example.notolyzard.navigation

enum class TopLevelDestination(val route: Any, val label: String) {
    ScalesAndChords(
        route = ScalesAndChordsVisualizationRoute,
        label = "Scales & Chords",
    ),
	IntervalGuessingGame(
        route = IntervalGuessingGameRoute,
        label = "Intervals",
    ),
}
