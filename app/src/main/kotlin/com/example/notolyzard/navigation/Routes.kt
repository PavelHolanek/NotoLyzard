package com.example.notolyzard.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes.
 *
 * Each destination is a `@Serializable` object (no arguments) or data class (with
 * arguments), so the compiler checks navigation calls instead of you hand-writing route
 * strings.
 */
@Serializable
data object ScalesAndChordsVisualizationRoute

@Serializable
data object ScaleViewerRoute

// TODO: add further destinations here as screens are built, for example:
// @Serializable data class ChordQuizRoute(val rootNoteOrdinal: Int)
