package com.example.notolyzard.ui.components.notegrouppicker

import com.example.notolyzard.core.theory.ChordType
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardChords
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The picker's rules are a plain state holder, so they test with plain JUnit — no Compose,
 * no `Dispatchers.Main`, no ViewModel. That is the practical payoff of keeping the logic
 * out of both the Composable and the screen's ViewModel.
 */
class NoteGroupPickerStateTest {

    private val model = NoteGroupsModel()
    private val picker = NoteGroupPickerState(model)

    private val majorScale = StandardScales.patterns(listOf(ScaleType.Major)).single()
    private val minorScale = StandardScales.patterns(listOf(ScaleType.Minor)).single()
    private val majorTriad = StandardChords.patterns(listOf(ChordType.MajorTriad)).single()

    private fun row(index: Int = 0) = picker.uiState.value.rows[index]

    @Test
    fun `starts with four empty rows`() {
        assertEquals(4, picker.uiState.value.rows.size)
        picker.uiState.value.rows.forEach {
            assertNull(it.patternGroupName)
            assertNull(it.pattern)
            assertNull(it.rootNote)
            assertFalse(it.isComplete)
        }
    }

    @Test
    fun `the second column only offers patterns from the chosen group`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        assertEquals(10, picker.uiState.value.patternsFor(row()).size)

        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)
        assertEquals(13, picker.uiState.value.patternsFor(row()).size)
    }

    @Test
    fun `changing the pattern group clears the rest of the row`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.D))
        assertTrue(row().isComplete)

        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)

        assertEquals(NoteGroupsModel.CHORDS, row().patternGroupName)
        assertNull(row().pattern)
        assertNull(row().rootNote)
        assertNull(model.selectedNoteGroups.value[0])
    }

    @Test
    fun `reselecting the same pattern group changes nothing`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.D))

        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)

        assertEquals(majorScale, row().pattern)
        assertEquals(PitchClass(NoteName.D), row().rootNote)
    }

    @Test
    fun `changing the pattern keeps the root note`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.D))

        picker.onPatternSelected(0, minorScale)

        assertEquals(PitchClass(NoteName.D), row().rootNote)
        assertEquals(
            listOf("D", "E", "F", "G", "A", "A#", "C"),
            model.selectedNoteGroups.value[0]?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `changing the root note keeps the pattern`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        picker.onRootNoteSelected(0, PitchClass(NoteName.G))

        assertEquals(majorScale, row().pattern)
        assertEquals(
            listOf("G", "A", "B", "C", "D", "E", "F#"),
            model.selectedNoteGroups.value[0]?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `a row only reaches the shared model once it is complete`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        assertNull(model.selectedNoteGroups.value[0])

        picker.onPatternSelected(0, majorScale)
        assertNull(model.selectedNoteGroups.value[0])

        picker.onRootNoteSelected(0, PitchClass(NoteName.C))
        assertEquals(7, model.selectedNoteGroups.value[0]?.notes?.size)
    }

    @Test
    fun `chords work the same way as scales`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)
        picker.onPatternSelected(0, majorTriad)
        picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        assertEquals(
            listOf("C", "E", "G"),
            model.selectedNoteGroups.value[0]?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `rows are independent`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        picker.onPatternGroupSelected(2, NoteGroupsModel.CHORDS)
        picker.onPatternSelected(2, majorTriad)
        picker.onRootNoteSelected(2, PitchClass(NoteName.E))

        assertEquals(7, model.selectedNoteGroups.value[0]?.notes?.size)
        assertNull(model.selectedNoteGroups.value[1])
        assertEquals(3, model.selectedNoteGroups.value[2]?.notes?.size)
        assertEquals(majorScale, row(0).pattern)
        assertEquals(majorTriad, row(2).pattern)
    }

    @Test
    fun `clearing a row empties it in the picker and in the model`() {
        picker.onPatternGroupSelected(1, NoteGroupsModel.SCALES)
        picker.onPatternSelected(1, majorScale)
        picker.onRootNoteSelected(1, PitchClass(NoteName.C))

        picker.onRowCleared(1)

        assertNull(row(1).patternGroupName)
        assertNull(row(1).pattern)
        assertNull(row(1).rootNote)
        assertNull(model.selectedNoteGroups.value[1])
    }

    @Test
    fun `two pickers over one model see each other's selections`() {
        val other = NoteGroupPickerState(model)

        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, majorScale)
        picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        // The second picker has its own column state but reads the same shared selection.
        assertNull(other.uiState.value.rows[0].pattern)
        assertEquals(7, model.selectedNoteGroups.value[0]?.notes?.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `an out of range row fails loudly`() {
        picker.onPatternGroupSelected(99, NoteGroupsModel.SCALES)
    }
}
