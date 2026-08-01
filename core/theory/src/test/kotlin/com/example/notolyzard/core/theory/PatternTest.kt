package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Test

class PatternTest {

    @Test
    fun `gaps are correct for three note patterns`() {
        assertEquals(listOf(0, 4, 3), Pattern(listOf(0, 4, 7)).gaps())
        assertEquals(listOf(0, 3, 4), Pattern(listOf(0, 3, 7)).gaps())
    }

    @Test
    fun `gaps are correct for four note patterns`() {
        // The original's GetGabs bug produced [0, 4, 3, 0] here.
        assertEquals(listOf(0, 4, 3, 4), Pattern(listOf(0, 4, 7, 11)).gaps())
        assertEquals(listOf(0, 3, 4, 3), Pattern(listOf(0, 3, 7, 10)).gaps())
    }

    @Test
    fun `gaps sum back to the last interval`() {
        for (pattern in StandardChords.allPatterns + StandardScales.allPatterns) {
            assertEquals(pattern.name, pattern.intervals.last(), pattern.gaps().sum())
        }
    }

    @Test
    fun `name defaults to empty`() {
        assertEquals("", Pattern(listOf(0)).name)
    }
}

class ScalePatternTest {

    @Test
    fun `mask and intervals round trip`() {
        val major = StandardScales.patterns(listOf(ScaleType.Major)).single()
        assertEquals(listOf(0, 2, 4, 5, 7, 9, 11), major.intervals)
        assertEquals(
            listOf(true, false, true, false, true, true, false, true, false, true, false, true),
            major.mask(),
        )
        assertEquals(major.intervals, ScalePattern.fromMask(ScaleType.Major, major.mask()).intervals)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromMask rejects a mask of the wrong length`() {
        ScalePattern.fromMask(ScaleType.Major, List(11) { true })
    }

    @Test(expected = IllegalArgumentException::class)
    fun `fromMask requires the base note`() {
        ScalePattern.fromMask(ScaleType.Major, List(12) { it != 0 })
    }
}
