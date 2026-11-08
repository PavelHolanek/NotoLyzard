package com.example.notolyzard.ui.components.notegroupspicker

import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.data.notegroups.noteGroupOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

/**
 * The behaviour of [NoteGroupsPicker], separated from its rendering so that every screen
 * showing a picker behaves identically without reimplementing the rules.
 *
 * The rules it owns:
 *  - changing the pattern group (Scales ↔ Chords) clears the rest of that row, because a
 *    scale pattern is not a valid chord pattern, and clearing the group empties the row
 *    altogether;
 *  - changing the pattern keeps the root note, so switching Major → Minor stays on the
 *    same key;
 *  - changing the root note keeps the pattern, for the mirror-image reason;
 *  - a note picked elsewhere on the screen, such as by clicking it in a visualization,
 *    becomes the root note of every row that follows picked notes — see [onNotePicked];
 *  - a hidden row keeps its group but does not show it — see [shownNoteGroups];
 *  - a row that is complete is written into the shared [NoteGroupsModel], and a row that
 *    stops being complete clears its slot there.
 *
 * **A plain class, not a `ViewModel`.** It is created and owned by the screen's ViewModel,
 * so it never enters a `ViewModelStore`, which means `onCleared()` would never be called
 * and a `viewModelScope` would never be cancelled. Extending `ViewModel` here would look
 * right and leak. This mirrors how Compose itself ships `LazyListState` alongside
 * `LazyColumn`.
 */
class NoteGroupsPickerState(
    private val noteGroupsModel: NoteGroupsModel,
    val rowCount: Int = DEFAULT_ROW_COUNT,
) {

    private val _uiState = MutableStateFlow(
        NoteGroupsPickerUiState(
            rows = List(rowCount) { NoteGroupsPickerRow() },
            patternGroups = noteGroupsModel.patternGroups,
            rootNotes = noteGroupsModel.basicNotes,
        ),
    )
    val uiState: StateFlow<NoteGroupsPickerUiState> = _uiState.asStateFlow()

    /**
     * The groups to show: the shared model's selection, with a hidden row's group left out
     * as null so that every group keeps its row's index, and with it its colour.
     *
     * Hiding is this picker's business only. The shared model keeps the hidden group
     * selected, so other screens reading it are not affected.
     */
    val shownNoteGroups: Flow<List<NoteGroup<PitchClass>?>> =
        combine(noteGroupsModel.selectedNoteGroups, _uiState) { groups, state ->
            groups.mapIndexed { index, group ->
                group.takeIf { state.rows.getOrNull(index)?.isShown != false }
            }
        }

    init {
        noteGroupsModel.setRowCount(rowCount)
    }

    /**
     * First column. Clears the pattern and root note unless the group is unchanged.
     *
     * Passing `null` empties the row, which is how a row that was filled in by mistake gets
     * taken out of the selection again. Either way the toggles stay as they were: they say
     * how the row behaves, not what it holds.
     */
    fun onPatternGroupSelected(row: Int, patternGroupName: String?) = updateRow(row) { current ->
        if (patternGroupName == current.patternGroupName) current
        else current.copy(patternGroupName = patternGroupName, pattern = null, rootNote = null)
    }

    /** Second column. Keeps the root note, so Major → Minor stays in the same key. */
    fun onPatternSelected(row: Int, pattern: Pattern) = updateRow(row) { current ->
        current.copy(pattern = pattern)
    }

    /** Third column. Keeps the pattern, so the same scale moves to another root. */
    fun onRootNoteSelected(row: Int, rootNote: PitchClass) = updateRow(row) { current ->
        current.copy(rootNote = rootNote)
    }

    /** The toggle at the start of the row: whether its group is shown. */
    fun onShownToggled(row: Int) = updateRow(row) { current ->
        current.copy(isShown = !current.isShown)
    }

    /** The toggle at the end of the row: whether its root note follows picked notes. */
    fun onFollowPickedNotesToggled(row: Int) = updateRow(row) { current ->
        current.copy(followsPickedNotes = !current.followsPickedNotes)
    }

    /**
     * A note picked outside the picker, such as by clicking it in a visualization, becomes
     * the root note of every row that follows picked notes and has a pattern. A row without
     * a pattern is left alone: a root note on its own would build nothing.
     */
    fun onNotePicked(note: PitchClass) {
        _uiState.value.rows.forEachIndexed { row, current ->
            if (current.followsPickedNotes && current.pattern != null) onRootNoteSelected(row, note)
        }
    }

    /** Empties the row, including its slot in the shared model. The toggles stay. */
    fun onRowCleared(row: Int) = onPatternGroupSelected(row, null)

    /**
     * Applies [transform] to one row and republishes the resulting group, so the picker's
     * own state and the shared model can never disagree.
     */
    private fun updateRow(row: Int, transform: (NoteGroupsPickerRow) -> NoteGroupsPickerRow) {
        require(row in 0 until rowCount) { "Row $row is outside 0..${rowCount - 1}" }

        val previous = _uiState.value.rows[row]
        val updated = transform(previous)
        _uiState.update { current ->
            current.copy(
                rows = current.rows.mapIndexed { index, existing ->
                    if (index == row) updated else existing
                },
            )
        }
        // Only the pattern and the root note make the group. Republishing it for anything
        // else, such as a toggle, would hand every visualization a new but identical group.
        if (updated.pattern != previous.pattern || updated.rootNote != previous.rootNote) {
            noteGroupsModel.select(row, noteGroupOf(updated.pattern, updated.rootNote))
        }
    }

    companion object {
        /** Matches the four group colours inherited from the old ColorPalette. */
        const val DEFAULT_ROW_COUNT = 4
    }
}
