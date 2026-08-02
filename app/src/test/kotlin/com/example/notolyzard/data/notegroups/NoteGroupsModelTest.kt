package com.example.notolyzard.data.notegroups

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NoteGroupsModelTest {

    private fun cMajor() = Scale(
        StandardScales.patterns(listOf(ScaleType.Major)).single(),
        PitchClass(NoteName.C),
    )

    @Test
    fun `catalogue holds scales before chords`() {
        val model = NoteGroupsModel()

        assertEquals(
            listOf(NoteGroupsModel.SCALES, NoteGroupsModel.CHORDS),
            model.patternGroups.keys.toList(),
        )
        assertEquals(10, model.patternGroups.getValue(NoteGroupsModel.SCALES).size)
        assertEquals(13, model.patternGroups.getValue(NoteGroupsModel.CHORDS).size)
    }

    @Test
    fun `catalogue holds all twelve pitch classes in NoteName order`() {
        val model = NoteGroupsModel()

        assertEquals(12, model.basicNotes.size)
        assertEquals(PitchClass(NoteName.A), model.basicNotes.first())
        assertEquals(PitchClass(NoteName.G_SHARP), model.basicNotes.last())
    }

    @Test
    fun `selection starts empty`() {
        assertEquals(emptyList<Any?>(), NoteGroupsModel().selectedNoteGroups.value)
    }

    @Test
    fun `rows start empty and can be filled`() {
        val model = NoteGroupsModel()
        model.setRowCount(3)
        assertEquals(listOf(null, null, null), model.selectedNoteGroups.value)

        val scale = cMajor()
        model.select(1, scale)

        assertEquals(listOf(null, scale, null), model.selectedNoteGroups.value)
    }

    @Test
    fun `clearing a row keeps the row`() {
        val model = NoteGroupsModel()
        model.setRowCount(2)
        model.select(0, cMajor())

        model.clear(0)

        assertEquals(2, model.selectedNoteGroups.value.size)
        assertNull(model.selectedNoteGroups.value[0])
    }

    @Test
    fun `shrinking keeps the surviving rows and growing adds empty ones`() {
        val model = NoteGroupsModel()
        model.setRowCount(2)
        val scale = cMajor()
        model.select(0, scale)

        model.setRowCount(1)
        assertEquals(listOf(scale), model.selectedNoteGroups.value)

        model.setRowCount(3)
        assertEquals(listOf(scale, null, null), model.selectedNoteGroups.value)
    }

    @Test
    fun `addRow appends a single empty row`() {
        val model = NoteGroupsModel()

        model.addRow()
        model.addRow()

        assertEquals(listOf(null, null), model.selectedNoteGroups.value)
    }

    @Test
    fun `state flow emits a new list rather than mutating the old one`() {
        val model = NoteGroupsModel()
        model.setRowCount(1)
        val before = model.selectedNoteGroups.value

        model.select(0, cMajor())

        assertEquals(listOf(null), before)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `selecting a row that does not exist fails loudly`() {
        NoteGroupsModel().select(0, cMajor())
    }
}
