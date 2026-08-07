package com.example.notolyzard.data.notegroups

import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.StandardChords
import com.example.notolyzard.core.theory.StandardScales
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NoteGroupsModel {

    val patternGroups: Map<String, List<Pattern>> = linkedMapOf(
        SCALES to StandardScales.allPatterns,
        CHORDS to StandardChords.allPatterns,
    )

    val basicNotes: List<PitchClass> = NoteName.entries.map { PitchClass(it) }

    private val _selectedNoteGroups = MutableStateFlow<List<NoteGroup<PitchClass>?>>(emptyList())

    val selectedNoteGroups: StateFlow<List<NoteGroup<PitchClass>?>> =
        _selectedNoteGroups.asStateFlow()

    fun addRow() {
        _selectedNoteGroups.update { it + null }
    }

    fun setRowCount(count: Int) {
        require(count >= 0) { "Row count cannot be negative, was $count" }
        _selectedNoteGroups.update { current ->
            when {
                count == current.size -> current
                count < current.size -> current.take(count)
                else -> current + List(count - current.size) { null }
            }
        }
    }

    fun select(row: Int, group: NoteGroup<PitchClass>?) {
        _selectedNoteGroups.update { current ->
            require(row in current.indices) {
                "Row $row is outside 0..${current.size - 1}; call setRowCount first"
            }
            current.mapIndexed { index, existing -> if (index == row) group else existing }
        }
    }

    fun clear(row: Int) = select(row, null)

    fun clearAll() {
        _selectedNoteGroups.update { current -> List(current.size) { null } }
    }

    companion object {
        const val SCALES = "Scales"
        const val CHORDS = "Chords"
    }
}
