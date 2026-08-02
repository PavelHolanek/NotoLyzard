package com.example.notolyzard.ui.components.notegrouppicker

import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.data.notegroups.noteGroupOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * The behaviour of [NoteGroupPicker], separated from its rendering so that every screen
 * showing a picker behaves identically without reimplementing the rules.
 *
 * The rules it owns:
 *  - changing the pattern group (Scales ↔ Chords) clears the rest of that row, because a
 *    scale pattern is not a valid chord pattern;
 *  - changing the pattern keeps the root note, so switching Major → Minor stays on the
 *    same key;
 *  - changing the root note keeps the pattern, for the mirror-image reason;
 *  - a row that is complete is written into the shared [NoteGroupsModel], and a row that
 *    stops being complete clears its slot there.
 *
 * **A plain class, not a `ViewModel`.** It is created and owned by the screen's ViewModel,
 * so it never enters a `ViewModelStore`, which means `onCleared()` would never be called
 * and a `viewModelScope` would never be cancelled. Extending `ViewModel` here would look
 * right and leak. This mirrors how Compose itself ships `LazyListState` alongside
 * `LazyColumn`.
 */
class NoteGroupPickerState(
    private val noteGroupsModel: NoteGroupsModel,
    val rowCount: Int = DEFAULT_ROW_COUNT,
) {

    private val _uiState = MutableStateFlow(
        NoteGroupPickerUiState(
            rows = List(rowCount) { NoteGroupPickerRow() },
            patternGroups = noteGroupsModel.patternGroups,
            rootNotes = noteGroupsModel.basicNotes,
        ),
    )
    val uiState: StateFlow<NoteGroupPickerUiState> = _uiState.asStateFlow()

    init {
        noteGroupsModel.setRowCount(rowCount)
    }

    /** First column. Clears the pattern and root note unless the group is unchanged. */
    fun onPatternGroupSelected(row: Int, patternGroupName: String) = updateRow(row) { current ->
        if (current.patternGroupName == patternGroupName) {
            current
        } else {
            NoteGroupPickerRow(patternGroupName = patternGroupName)
        }
    }

    /** Second column. Keeps the root note, so Major → Minor stays in the same key. */
    fun onPatternSelected(row: Int, pattern: Pattern) = updateRow(row) { current ->
        current.copy(pattern = pattern)
    }

    /** Third column. Keeps the pattern, so the same scale moves to another root. */
    fun onRootNoteSelected(row: Int, rootNote: PitchClass) = updateRow(row) { current ->
        current.copy(rootNote = rootNote)
    }

    /** Empties the row completely, including its slot in the shared model. */
    fun onRowCleared(row: Int) = updateRow(row) { NoteGroupPickerRow() }

    /**
     * Applies [transform] to one row and republishes the resulting group, so the picker's
     * own state and the shared model can never disagree.
     */
    private fun updateRow(row: Int, transform: (NoteGroupPickerRow) -> NoteGroupPickerRow) {
        require(row in 0 until rowCount) { "Row $row is outside 0..${rowCount - 1}" }

        val updated = transform(_uiState.value.rows[row])
        _uiState.update { current ->
            current.copy(
                rows = current.rows.mapIndexed { index, existing ->
                    if (index == row) updated else existing
                },
            )
        }
        noteGroupsModel.select(row, noteGroupOf(updated.pattern, updated.rootNote))
    }

    companion object {
        /** Matches the four group colours inherited from the old ColorPalette. */
        const val DEFAULT_ROW_COUNT = 4
    }
}
