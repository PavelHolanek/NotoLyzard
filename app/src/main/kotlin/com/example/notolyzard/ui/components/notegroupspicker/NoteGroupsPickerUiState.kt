package com.example.notolyzard.ui.components.notegroupspicker

import com.example.notolyzard.core.theory.ChordPattern
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass

/** What the picker shows: one row per selectable note group, plus the available options. */
data class NoteGroupsPickerUiState(
    val rows: List<NoteGroupsPickerRow>,
    val patternGroups: Map<String, List<Pattern>>,
    val rootNotes: List<PitchClass>,
) {
    /** Patterns offered in the second column of [row], empty until a group is chosen. */
    fun patternsFor(row: NoteGroupsPickerRow): List<Pattern> =
        row.patternGroupName?.let { patternGroups[it] }.orEmpty()
}

/**
 * One row of the picker: pattern group, pattern, root note — the three columns — and a
 * toggle either side of them.
 *
 * The three columns are nullable because a row is filled in left to right, and an
 * untouched row has to be representable.
 *
 * [isShown] is the toggle before the columns: switched off, the row keeps its group but
 * the group is hidden — see [NoteGroupsPickerState.shownNoteGroups]. It starts on.
 *
 * [followsPickedNotes] is the toggle after them: while it is on, a note picked outside the
 * picker, such as by clicking it in a visualization, becomes this row's root note. It
 * starts off, so a picked note moves nothing until a row is switched on.
 */
data class NoteGroupsPickerRow(
    val patternGroupName: String? = null,
    val pattern: Pattern? = null,
    val rootNote: PitchClass? = null,
    val isShown: Boolean = true,
    val followsPickedNotes: Boolean = false,
) {
    /** True once the row describes a group that can actually be built. */
    val isComplete: Boolean get() = pattern != null && rootNote != null

    /**
     * What the closed pattern cell shows: a chord's symbol, a scale's full name. The open
     * menu still lists full names, so the symbols are learnt from it.
     */
    val patternLabel: String? get() = pattern?.let(::collapsedLabel)
}

/**
 * A chord is shown by its [ChordPattern.symbol], except a major triad: its symbol is empty,
 * as `C` alone means C major, but an empty cell next to the root would read as nothing
 * chosen. Other patterns have no symbol and keep their name.
 */
internal fun collapsedLabel(pattern: Pattern): String = when (pattern) {
    is ChordPattern -> pattern.symbol.ifEmpty { MAJOR_TRIAD_LABEL }
    else -> pattern.name
}

private const val MAJOR_TRIAD_LABEL = "maj"
