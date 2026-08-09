package com.example.notolyzard.feature.scalesandchords

import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.PitchClass

data class ScalesAndChordsVisualizationUiState(
    val selectedNoteGroups: List<NoteGroup<PitchClass>?>,
    val visualization: Visualization = Visualization.Default,
)
