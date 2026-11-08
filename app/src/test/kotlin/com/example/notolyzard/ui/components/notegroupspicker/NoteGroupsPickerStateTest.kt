package com.example.notolyzard.ui.components.notegroupspicker

import com.example.notolyzard.core.theory.ChordPattern
import com.example.notolyzard.core.theory.ChordType
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteGroupsPickerStateTest {

    private val model = NoteGroupsModel()
    private val picker = NoteGroupsPickerState(model)

    private val c = PitchClass(NoteName.C)
    private val d = PitchClass(NoteName.D)

    @Test
    fun `rows start shown and not following picked notes`() {
        assertTrue(rows().all { it.isShown })
        assertTrue(rows().none { it.followsPickedNotes })
    }

    @Test
    fun `a picked note moves nothing until a row is switched on`() {
        fill(row = 0, root = c)

        picker.onNotePicked(d)

        assertEquals(c, rows()[0].rootNote)
    }

    @Test
    fun `a picked note becomes the root of every row switched on`() {
        fill(row = 0, root = c)
        fill(row = 1, root = c)
        fill(row = 2, root = c)
        picker.onFollowPickedNotesToggled(0)
        picker.onFollowPickedNotesToggled(2)

        picker.onNotePicked(d)

        assertEquals(listOf(d, c, d), rows().take(3).map { it.rootNote })
        assertNotNull(model.selectedNoteGroups.value[2])
    }

    @Test
    fun `a pattern without a root gets the picked one`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onPatternSelected(0, model.patternGroups.getValue(NoteGroupsModel.SCALES).first())
        picker.onFollowPickedNotesToggled(0)

        picker.onNotePicked(d)

        assertEquals(d, rows()[0].rootNote)
        assertNotNull(model.selectedNoteGroups.value[0])
    }

    @Test
    fun `a row without a pattern ignores picked notes`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        picker.onFollowPickedNotesToggled(0)
        picker.onFollowPickedNotesToggled(1)

        picker.onNotePicked(d)

        assertNull(rows()[0].rootNote)
        assertNull(rows()[1].rootNote)
    }

    @Test
    fun `toggling keeps the row and leaves the shared model alone`() {
        fill(row = 0, root = c)
        val before = rows()[0]
        val published = model.selectedNoteGroups.value

        picker.onFollowPickedNotesToggled(0)
        picker.onShownToggled(0)

        assertEquals(before.copy(followsPickedNotes = true, isShown = false), rows()[0])
        assertSame(published, model.selectedNoteGroups.value)
    }

    @Test
    fun `emptying or regrouping a row keeps its toggles`() {
        fill(row = 0, root = c)
        picker.onFollowPickedNotesToggled(0)
        picker.onShownToggled(0)

        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)
        assertTrue(rows()[0].followsPickedNotes)
        assertFalse(rows()[0].isShown)
        assertNull(rows()[0].pattern)

        picker.onPatternGroupSelected(0, null)
        assertEquals(NoteGroupsPickerRow(isShown = false, followsPickedNotes = true), rows()[0])
        assertNull(model.selectedNoteGroups.value[0])
    }

    @Test
    fun `a hidden row's group is left out of the shown groups but stays selected`() = runTest {
        fill(row = 0, root = c)
        fill(row = 1, root = d)

        picker.onShownToggled(0)

        val shown = picker.shownNoteGroups.first()
        assertNull(shown[0])
        assertSame(model.selectedNoteGroups.value[1], shown[1])
        assertEquals(model.selectedNoteGroups.value.size, shown.size)
        assertNotNull(model.selectedNoteGroups.value[0])
    }

    @Test
    fun `showing a row again brings its group back`() = runTest {
        fill(row = 0, root = c)
        picker.onShownToggled(0)

        picker.onShownToggled(0)

        assertSame(model.selectedNoteGroups.value[0], picker.shownNoteGroups.first()[0])
    }

    @Test
    fun `a chord shows its symbol while a scale shows its name`() {
        val chord = chordPattern(ChordType.MinorSeventh)
        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)
        picker.onPatternSelected(0, chord)
        val scale = model.patternGroups.getValue(NoteGroupsModel.SCALES).first()
        picker.onPatternGroupSelected(1, NoteGroupsModel.SCALES)
        picker.onPatternSelected(1, scale)

        assertEquals("m7", rows()[0].patternLabel)
        assertEquals(scale.name, rows()[1].patternLabel)
        assertNull(rows()[2].patternLabel)
    }

    @Test
    fun `a major triad is not shown as an empty symbol`() {
        picker.onPatternGroupSelected(0, NoteGroupsModel.CHORDS)
        picker.onPatternSelected(0, chordPattern(ChordType.MajorTriad))

        assertEquals("maj", rows()[0].patternLabel)
    }

    private fun chordPattern(type: ChordType) = model.patternGroups.getValue(NoteGroupsModel.CHORDS)
        .single { (it as ChordPattern).type == type }

    private fun rows() = picker.uiState.value.rows

    private fun fill(row: Int, root: PitchClass) {
        picker.onPatternGroupSelected(row, NoteGroupsModel.SCALES)
        picker.onPatternSelected(row, model.patternGroups.getValue(NoteGroupsModel.SCALES).first())
        picker.onRootNoteSelected(row, root)
    }
}
