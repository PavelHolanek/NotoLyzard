package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * The layout of the circle is a pure function, so the whole mapping from "these groups
 * are selected" to "these buttons show these colours" is testable with plain JUnit — no
 * Compose runtime, no emulator.
 */
class CircleOfFifthsTest {

    private val red = Color.Red
    private val blue = Color.Blue

    private fun scale(type: ScaleType, root: NoteName) = Scale(
        StandardScales.patterns(listOf(type)).single(),
        PitchClass(root),
    )

    @Test
    fun `outer ring walks up in fifths from C`() {
        assertEquals(
            listOf("C", "G", "D", "A", "E", "B", "F#", "C#", "G#", "D#", "A#", "F"),
            circleOfFifthsRoots().map { it.noteName.symbol },
        )
    }

    @Test
    fun `inner ring holds the relative minor of each major key`() {
        val rings = circleOfFifthsRings(emptyList())

        assertEquals(12, rings.major.size)
        assertEquals(12, rings.minor.size)
        // C major / A minor, G major / E minor.
        assertEquals("C", rings.major[0].pitchClass.noteName.symbol)
        assertEquals("A", rings.minor[0].pitchClass.noteName.symbol)
        assertEquals("G", rings.major[1].pitchClass.noteName.symbol)
        assertEquals("E", rings.minor[1].pitchClass.noteName.symbol)
    }

    @Test
    fun `with no layers no button is coloured`() {
        val rings = circleOfFifthsRings(emptyList())

        assertEquals(emptyList<Color>(), rings.major.first().segments)
        assertEquals(emptyList<Color>(), rings.minor.first().segments)
    }

    @Test
    fun `C major colours exactly the seven notes of the scale`() {
        val layers = listOf(NoteGroupLayer(scale(ScaleType.Major, NoteName.C), red))
        val rings = circleOfFifthsRings(layers)

        val colouredMajors = rings.major
            .filter { it.segments.isNotEmpty() }
            .map { it.pitchClass.noteName.symbol }
            .toSet()

        // C major has no accidentals, so on the outer ring it covers F C G D A E B.
        assertEquals(setOf("F", "C", "G", "D", "A", "E", "B"), colouredMajors)
    }

    @Test
    fun `a note in two layers gets two segments in layer order`() {
        val layers = listOf(
            NoteGroupLayer(scale(ScaleType.Major, NoteName.C), red),
            NoteGroupLayer(scale(ScaleType.Minor, NoteName.A), blue),
        )
        val rings = circleOfFifthsRings(layers)
        val c = rings.major.single { it.pitchClass.noteName == NoteName.C }

        // C is in both C major and A natural minor.
        assertEquals(listOf(red, blue), c.segments)
    }

    @Test
    fun `an empty layer keeps its slot so colour indices stay stable`() {
        val layers = listOf(
            NoteGroupLayer(null, red),
            NoteGroupLayer(scale(ScaleType.Major, NoteName.C), blue),
        )
        val rings = circleOfFifthsRings(layers)
        val c = rings.major.single { it.pitchClass.noteName == NoteName.C }

        assertEquals(2, c.outerColors.size)
        assertNull(c.outerColors[0])
        assertEquals(blue, c.outerColors[1])
        assertEquals(listOf(blue), c.segments)
    }
}

class NoteGroupLayerTest {

    @Test
    fun `colorsFor keeps one entry per layer`() {
        val layers = listOf(
            NoteGroupLayer(null, Color.Red),
            NoteGroupLayer(null, Color.Blue),
            NoteGroupLayer(null, Color.Green),
        )

        assertEquals(listOf(null, null, null), layers.colorsFor(PitchClass(NoteName.C)))
    }

    @Test
    fun `a chord matches by note name regardless of octave`() {
        val cMajorTriad = com.example.notolyzard.core.theory.Chord(
            com.example.notolyzard.core.theory.StandardChords
                .patterns(listOf(com.example.notolyzard.core.theory.ChordType.MajorTriad)).single(),
            com.example.notolyzard.core.theory.Pitch(4, NoteName.C),
        )
        val layers = listOf(NoteGroupLayer(cMajorTriad, Color.Red))

        assertEquals(listOf(Color.Red), layers.colorsFor(PitchClass(NoteName.E)))
        assertEquals(listOf(null), layers.colorsFor(PitchClass(NoteName.D)))
    }
}
