package com.example.notolyzard.feature.scalesandchords

import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.PitchClass

/**
 * What the page shows: the currently selected note groups, one slot per picker row.
 *
 * Deliberately thin. The picker's own state is not duplicated here — it lives in
 * [com.example.notolyzard.ui.components.notegrouppicker.NoteGroupPickerState], which the
 * ViewModel owns and the picker reads directly. And nothing here describes rings, keys or
 * frets, so replacing the circle with a keyboard or a fretboard needs no change to this
 * type.
 */
data class ScalesAndChordsVisualizationUiState(
    val selectedNoteGroups: List<NoteGroup<PitchClass>?>,
)
