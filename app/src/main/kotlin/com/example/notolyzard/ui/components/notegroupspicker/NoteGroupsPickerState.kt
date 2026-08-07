package com.example.notolyzard.ui.components.notegroupspicker

import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.data.notegroups.noteGroupOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    init {
        noteGroupsModel.setRowCount(rowCount)
    }

    /**
     * First column. Clears the pattern and root note unless the group is unchanged.
     *
     * Passing `null` puts the row back to its untouched state, which is how a row that was
     * filled in by mistake gets taken out of the selection again.
     */
    fun onPatternGroupSelected(row: Int, patternGroupName: String?) = updateRow(row) { current ->
        when (patternGroupName) {
            current.patternGroupName -> current
            null -> NoteGroupsPickerRow()
            else -> NoteGroupsPickerRow(patternGroupName = patternGroupName)
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
    fun onRowCleared(row: Int) = updateRow(row) { NoteGroupsPickerRow() }

    /**
     * Applies [transform] to one row and republishes the resulting group, so the picker's
     * own state and the shared model can never disagree.
     */
    private fun updateRow(row: Int, transform: (NoteGroupsPickerRow) -> NoteGroupsPickerRow) {
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
