package com.example.notolyzard.ui.components.fretboard

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Tuning
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FretboardGeometryTest {

    private val geometry = FretboardGeometry(
        stringCount = 6,
        fretCount = 24,
        fretSpacingPx = 100f,
        stringSpacingPx = 50f,
        markerRowHeightPx = 20f,
    )

    @Test
    fun `lowest string is drawn at the bottom`() {
        assertTrue(geometry.stringY(0) > geometry.stringY(5))
        assertTrue(geometry.stringY(0) < geometry.boardHeight)
        assertTrue(geometry.stringY(5) > 0f)
    }

    @Test
    fun `open strings sit left of the nut`() {
        assertTrue(geometry.noteX(0) < geometry.nutX)
    }

    @Test
    fun `every fret is the same width`() {
        val widths = (1..24).map { geometry.fretWireX(it) - geometry.fretWireX(it - 1) }
        assertEquals(setOf(100f), widths.toSet())
    }

    @Test
    fun `a note sits halfway between its fret wire and the previous one`() {
        (1..24).forEach { fret ->
            assertEquals(
                (geometry.fretWireX(fret - 1) + geometry.fretWireX(fret)) / 2f,
                geometry.noteX(fret),
            )
        }
    }

    @Test
    fun `markers sit below the board`() {
        assertTrue(geometry.markerY > geometry.boardHeight)
        assertTrue(geometry.markerY < geometry.height)
    }
}

class FretMarkerDotsTest {

    @Test
    fun `single dots on 3 5 7 9 and an octave higher`() {
        listOf(3, 5, 7, 9, 15, 17, 19, 21).forEach { assertEquals(1, fretMarkerDots(it)) }
    }

    @Test
    fun `double dots on 12 and 24`() {
        assertEquals(2, fretMarkerDots(12))
        assertEquals(2, fretMarkerDots(24))
    }

    @Test
    fun `no dots anywhere else`() {
        listOf(0, 1, 2, 4, 6, 8, 10, 11, 13, 14, 16, 18, 20, 22, 23).forEach {
            assertEquals(0, fretMarkerDots(it))
        }
    }
}

class FretboardNotesTest {

    @Test
    fun `every string gets the open note plus one per fret`() {
        val notes = fretboardNotes(Tuning.Standard, fretCount = 24, layers = emptyList())
        assertEquals(6 * 25, notes.size)
    }

    @Test
    fun `notes follow the tuning`() {
        val notes = fretboardNotes(Tuning.Standard, fretCount = 24, layers = emptyList())
        val lowEFifthFret = notes.single { it.string == 0 && it.fret == 5 }
        assertEquals(PitchClass(NoteName.A), lowEFifthFret.state.pitchClass)
    }
}
