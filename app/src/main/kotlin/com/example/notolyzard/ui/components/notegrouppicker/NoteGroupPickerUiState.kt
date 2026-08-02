package com.example.notolyzard.ui.components.notegrouppicker

import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass

/** What the picker shows: one row per selectable note group, plus the available options. */
data class NoteGroupPickerUiState(
    val rows: List<NoteGroupPickerRow>,
    val patternGroups: Map<String, List<Pattern>>,
    val rootNotes: List<PitchClass>,
) {
    /** Patterns offered in the second column of [row], empty until a group is chosen. */
    fun patternsFor(row: NoteGroupPickerRow): List<Pattern> =
        row.patternGroupName?.let { patternGroups[it] }.orEmpty()
}

/**
 * One row of the picker: pattern group, pattern, root note — the three columns.
 *
 * All three are nullable because a row is filled in left to right, and an untouched row
 * has to be representable.
 */
data class NoteGroupPickerRow(
    val patternGroupName: String? = null,
    val pattern: Pattern? = null,
    val rootNote: PitchClass? = null,
) {
    /** True once the row describes a group that can actually be built. */
    val isComplete: Boolean get() = pattern != null && rootNote != null
}
