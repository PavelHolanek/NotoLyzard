package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PitchTest {

    @Test
    fun `transposing up within an octave`() {
        assertEquals(Pitch(4, NoteName.E), Pitch(4, NoteName.C) + 4)
        assertEquals(Pitch(4, NoteName.G), Pitch(4, NoteName.C) + 7)
    }

    @Test
    fun `octave changes between G sharp and A`() {
        // Consequence of the A = 0 ordering: an octave runs A..G#.
        assertEquals(Pitch(5, NoteName.A), Pitch(4, NoteName.G_SHARP) + 1)
        assertEquals(Pitch(4, NoteName.G_SHARP), Pitch(5, NoteName.A) - 1)
        // B and C therefore sit in the same octave.
        assertEquals(Pitch(4, NoteName.C), Pitch(4, NoteName.B) + 1)
    }

    @Test
    fun `transposing down crosses the octave boundary correctly`() {
        assertEquals(Pitch(4, NoteName.C), Pitch(4, NoteName.E) - 4)
        assertEquals(Pitch(4, NoteName.A), Pitch(4, NoteName.C) - 3)
        // Used to throw: the original produced an out-of-range NoteType here.
        assertEquals(Pitch(3, NoteName.G_SHARP), Pitch(4, NoteName.C) - 4)
    }

    @Test
    fun `transposing works for offsets beyond a single octave in both directions`() {
        assertEquals(Pitch(6, NoteName.C), Pitch(4, NoteName.C) + 24)
        // Used to be broken: the original only handled offsets down to -12.
        assertEquals(Pitch(2, NoteName.C), Pitch(4, NoteName.C) - 24)
        assertEquals(Pitch(2, NoteName.C), Pitch(4, NoteName.C) + (-24))
        assertEquals(Pitch(1, NoteName.C_SHARP), Pitch(4, NoteName.C) - 35)
    }

    @Test
    fun `transposing up and back down is an identity`() {
        val start = Pitch(4, NoteName.F)
        for (offset in -30..30) {
            assertEquals(start, (start + offset) - offset)
        }
    }

    @Test
    fun `distance between two pitches is a signed semitone count`() {
        assertEquals(3, Pitch(4, NoteName.C) - Pitch(4, NoteName.A))
        assertEquals(-3, Pitch(4, NoteName.A) - Pitch(4, NoteName.C))
        assertEquals(0, Pitch(4, NoteName.C) - Pitch(4, NoteName.C))
        assertEquals(12, Pitch(5, NoteName.C) - Pitch(4, NoteName.C))
        assertEquals(-12, Pitch(4, NoteName.C) - Pitch(5, NoteName.C))
    }

    @Test
    fun `distance agrees with transposition`() {
        val low = Pitch(3, NoteName.D_SHARP)
        val high = Pitch(5, NoteName.B)
        assertEquals(high, low + (high - low))
    }

    @Test
    fun `absoluteSemitone counts from A in octave 0`() {
        assertEquals(0, Pitch(0, NoteName.A).absoluteSemitone)
        assertEquals(51, Pitch(4, NoteName.C).absoluteSemitone)
        assertEquals(Pitch(4, NoteName.C), Pitch.of(51))
        assertEquals(Pitch(-1, NoteName.G_SHARP), Pitch.of(-1))
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
