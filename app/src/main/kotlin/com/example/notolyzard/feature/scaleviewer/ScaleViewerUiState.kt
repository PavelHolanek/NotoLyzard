package com.example.notolyzard.feature.scaleviewer

import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType

/**
 * Everything the Scale Viewer screen needs to draw itself, and nothing else.
 *
 * One immutable snapshot per state of the screen: the UI never reads from anywhere else,
 * so what you see is always exactly what is in here.
 */
data class ScaleViewerUiState(
    val rootNote: PitchClass,
    val scaleType: ScaleType,
    val availableScaleTypes: List<ScaleType>,
    val scale: Scale?,
) {
    /** Notes of the current scale, ready to hand to a circle-of-fifths style component. */
    val scaleNotes: Set<PitchClass> = scale?.notes?.toSet().orEmpty()
}
