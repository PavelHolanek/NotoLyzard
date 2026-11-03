package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Test

class TuningTest {

    @Test
    fun `standard tuning runs E A D G B E from the lowest string`() {
        assertEquals(
            listOf(NoteName.E, NoteName.A, NoteName.D, NoteName.G, NoteName.B, NoteName.E),
            Tuning.Standard.strings.map { it.noteName },
        )
        assertEquals(6, Tuning.Standard.stringCount)
    }

    @Test
    fun `open string sounds the tuning note`() {
        assertEquals(PitchClass(NoteName.A), Tuning.Standard.pitchClassAt(string = 1, fret = 0))
    }

    @Test
    fun `each fret raises the string by a semitone`() {
        assertEquals(PitchClass(NoteName.G), Tuning.Standard.pitchClassAt(string = 0, fret = 3))
        assertEquals(PitchClass(NoteName.C), Tuning.Standard.pitchClassAt(string = 4, fret = 1))
    }

    @Test
    fun `twelfth fret is the open note again`() {
        Tuning.Standard.strings.indices.forEach { string ->
            assertEquals(
                Tuning.Standard.pitchClassAt(string, fret = 0),
                Tuning.Standard.pitchClassAt(string, fret = 12),
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a tuning without strings is rejected`() {
        Tuning(emptyList())
    }
}
