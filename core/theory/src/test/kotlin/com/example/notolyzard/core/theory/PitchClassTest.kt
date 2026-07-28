package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class NoteNameTest {

    @Test
    fun `ordering starts at A as in the C# original`() {
        assertEquals(0, NoteName.A.ordinal)
        assertEquals(3, NoteName.C.ordinal)
        assertEquals(11, NoteName.G_SHARP.ordinal)
        assertEquals(12, NoteName.entries.size)
    }

    @Test
    fun `symbol replaces GetNoteString`() {
        assertEquals("A", NoteName.A.symbol)
        assertEquals("A#", NoteName.A_SHARP.symbol)
        assertEquals("G#", NoteName.G_SHARP.symbol)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `ofSemitone rejects out of range values`() {
        NoteName.ofSemitone(-1)
    }
}

class PitchClassTest {

    @Test
    fun `transposing up wraps within the octave`() {
        assertEquals(PitchClass(NoteName.E), PitchClass(NoteName.C) + 4)
        assertEquals(PitchClass(NoteName.C), PitchClass(NoteName.G_SHARP) + 4)
    }

    @Test
    fun `transposing down wraps within the octave`() {
        assertEquals(PitchClass(NoteName.G_SHARP), PitchClass(NoteName.C) - 4)
    }

    @Test
    fun `distance between pitch classes is always 0 to 11`() {
        assertEquals(3, PitchClass(NoteName.C) - PitchClass(NoteName.A))
        assertEquals(9, PitchClass(NoteName.A) - PitchClass(NoteName.C))
        assertEquals(0, PitchClass(NoteName.C) - PitchClass(NoteName.C))
    }

    @Test
    fun `equality is by value`() {
        assertEquals(PitchClass(NoteName.C), PitchClass(NoteName.C))
        assertNotEquals(PitchClass(NoteName.C), PitchClass(NoteName.D))
        // A PitchClass is never equal to a Pitch, even with the same note name.
        val pitchClass: Any = PitchClass(NoteName.C)
        val pitch: Any = Pitch(4, NoteName.C)
        assertNotEquals(pitchClass, pitch)
    }
}
