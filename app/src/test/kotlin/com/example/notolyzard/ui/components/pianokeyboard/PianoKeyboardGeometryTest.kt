package com.example.notolyzard.ui.components.pianokeyboard

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PianoKeyboardGeometryTest {

    private val geometry = PianoKeyboardGeometry(octaveCount = 5, whiteKeyWidthPx = 100f)
    private val whiteKeys = geometry.keys.filterNot { it.isBlack }
    private val blackKeys = geometry.keys.filter { it.isBlack }

    @Test
    fun `five octaves span 61 keys from C to C`() {
        assertEquals(61, geometry.keys.size)
        assertEquals(36, whiteKeys.size)
        assertEquals(25, blackKeys.size)
        assertEquals(PitchClass(NoteName.C), geometry.keys.first().pitchClass)
        assertEquals(PitchClass(NoteName.C), geometry.keys.last().pitchClass)
        assertEquals(3600f, geometry.width)
    }

    @Test
    fun `black keys are the sharps`() {
        assertEquals(
            listOf(NoteName.C_SHARP, NoteName.D_SHARP, NoteName.F_SHARP, NoteName.G_SHARP, NoteName.A_SHARP),
            blackKeys.take(5).map { it.pitchClass.noteName },
        )
    }

    @Test
    fun `white keys sit side by side without gaps`() {
        whiteKeys.zipWithNext { left, right -> assertEquals(left.right, right.left, DELTA) }
        assertEquals(0f, whiteKeys.first().left, DELTA)
        assertEquals(geometry.width, whiteKeys.last().right, DELTA)
    }

    @Test
    fun `every black key covers the crack between its two neighbours`() {
        blackKeys.forEach { black ->
            val crack = geometry.keys[black.index + 1].left
            assertTrue(black.left < crack && crack < black.right)
        }
    }

    @Test
    fun `G sharp sits in the middle, its neighbours lean outwards by the same amount`() {
        assertEquals(0f, offsetFromCrack(NoteName.G_SHARP), DELTA)
        assertTrue(offsetFromCrack(NoteName.F_SHARP) < 0f)
        assertEquals(-offsetFromCrack(NoteName.F_SHARP), offsetFromCrack(NoteName.A_SHARP), DELTA)
    }

    @Test
    fun `C sharp leans left and D sharp right by the same amount`() {
        assertTrue(offsetFromCrack(NoteName.C_SHARP) < 0f)
        assertEquals(-offsetFromCrack(NoteName.C_SHARP), offsetFromCrack(NoteName.D_SHARP), DELTA)
    }

    @Test
    fun `notes on white keys stay clear of the black keys`() {
        whiteKeys.forEach { key ->
            assertTrue(key.noteCenter.y - key.noteSize / 2f > geometry.blackKeyHeight)
        }
    }

    @Test
    fun `notes fit within their key`() {
        geometry.keys.forEach { key -> assertTrue(key.noteSize < key.width) }
    }

    /** How far the centre of the first black key named [name] is from the crack it covers. */
    private fun offsetFromCrack(name: NoteName): Float {
        val black = blackKeys.first { it.pitchClass.noteName == name }
        val crack = geometry.keys[black.index + 1].left
        return (black.left + black.right) / 2f - crack
    }

    private companion object {
        const val DELTA = 1e-3f
    }
}
