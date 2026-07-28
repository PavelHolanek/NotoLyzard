package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * These tests pin the behaviour of the faithful 1:1 port of `NoteInOctave`. Several of
 * them assert results that are musically wrong — they are marked as such. If the defects
 * are ever fixed, these are the assertions to update.
 */
class PitchTest {

    @Test
    fun `transposing up within an octave`() {
        assertEquals(Pitch(4, NoteName.E), Pitch(4, NoteName.C) + 4)
        assertEquals(Pitch(4, NoteName.G), Pitch(4, NoteName.C) + 7)
    }

    @Test
    fun `octave rolls over between G sharp and A, not between B and C`() {
        // Consequence of the A = 0 enum ordering, faithful to the original.
        assertEquals(Pitch(5, NoteName.A), Pitch(4, NoteName.G_SHARP) + 1)
        // Musically wrong: B4 + 1 semitone is C5, not C4.
        assertEquals(Pitch(4, NoteName.C), Pitch(4, NoteName.B) + 1)
    }

    @Test
    fun `transposing down works while the result stays inside the octave`() {
        assertEquals(Pitch(4, NoteName.C), Pitch(4, NoteName.E) - 4)
    }

    @Test
    fun `transposing down uses truncating division, so the octave shift is inconsistent`() {
        // E4 - 4: the octave term is -9 / 12, which truncates to 0, so the octave is kept.
        assertEquals(4, (Pitch(4, NoteName.E) - 4).octave)
        // C4 - 3: the octave term is -12 / 12 = -1, so the octave drops even though A is
        // the first note of the octave under the A = 0 ordering.
        assertEquals(Pitch(3, NoteName.A), Pitch(4, NoteName.C) - 3)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `transposing down below the enum start throws`() {
        // The original applied no modulo here and produced an undefined NoteType.
        // Kotlin enums cannot hold an out-of-range ordinal, so this throws instead.
        Pitch(4, NoteName.C) - 4
    }

    @Test
    fun `distance between two pitches is not a real interval`() {
        // Musically wrong: the original multiplies the octave by the note index instead
        // of scaling it by 12, so C4 - A4 reports 0 rather than 3.
        assertEquals(0, Pitch(4, NoteName.C) - Pitch(4, NoteName.A))
        assertEquals(0, Pitch(4, NoteName.C) - Pitch(4, NoteName.C))
    }

    @Test
    fun `equality accounts for the octave`() {
        assertEquals(Pitch(4, NoteName.C), Pitch(4, NoteName.C))
        assertNotEquals(Pitch(4, NoteName.C), Pitch(5, NoteName.C))
    }

    @Test
    fun `toString includes the octave`() {
        assertEquals("C4", Pitch(4, NoteName.C).toString())
        assertEquals("A#3", Pitch(3, NoteName.A_SHARP).toString())
    }
}
