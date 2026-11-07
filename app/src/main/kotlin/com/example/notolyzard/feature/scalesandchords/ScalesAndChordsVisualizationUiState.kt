package com.example.notolyzard.feature.scalesandchords

import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.PitchClass

/**
 * [shownNoteGroups] has one entry per picker row, null where the row is empty or hidden,
 * so that each group's index is its row's and picks the row's colour.
 */
data class ScalesAndChordsVisualizationUiState(
    val shownNoteGroups: List<NoteGroup<PitchClass>?>,
    val visualization: Visualization = Visualization.Default,
)
