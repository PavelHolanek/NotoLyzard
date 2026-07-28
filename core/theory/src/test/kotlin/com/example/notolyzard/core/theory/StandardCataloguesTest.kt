package com.example.notolyzard.core.theory

import org.junit.Assert.assertEquals
import org.junit.Test

class StandardChordsTest {

    @Test
    fun `catalogue holds all thirteen patterns`() {
        assertEquals(13, StandardChords.allPatterns.size)
        // Every entry has a distinct type, so type is a usable key.
        assertEquals(13, StandardChords.allPatterns.map { it.type }.toSet().size)
    }

    @Test
    fun `major triad on C4`() {
        val chord = Chord(
            StandardChords.patterns(listOf(ChordType.MajorTriad)).single(),
            Pitch(4, NoteName.C),
        )
        assertEquals(
            listOf(Pitch(4, NoteName.C), Pitch(4, NoteName.E), Pitch(4, NoteName.G)),
            chord.notes,
        )
    }

    @Test
    fun `dominant seventh keeps the original typo`() {
        // Faithful: should be [0, 4, 7, 10].
        val pattern = StandardChords.patterns(listOf(ChordType.DominantSeventh)).single()
        assertEquals(listOf(0, 4, 3, 10), pattern.intervals)
    }

    @Test
    fun `sus2 and sus4 stay swapped`() {
        // Faithful: SusSecond should be [0, 2, 7] and SusFourth [0, 5, 7].
        assertEquals(
            listOf(0, 5, 7),
            StandardChords.patterns(listOf(ChordType.SusSecondTriad)).single().intervals,
        )
        assertEquals(
            listOf(0, 2, 7),
            StandardChords.patterns(listOf(ChordType.SusFourthTriad)).single().intervals,
        )
    }

    @Test
    fun `lookups follow catalogue order, not the requested order`() {
        // Augmented is requested before Diminished, but the catalogue lists Diminished first.
        assertEquals(
            listOf(
                ChordType.MajorTriad,
                ChordType.MinorTriad,
                ChordType.DiminishedTriad,
                ChordType.AugmentedTriad,
            ),
            StandardChords.standardTriadPatterns.map { it.type },
        )
    }

    @Test
    fun `all triads are standard triads followed by sus triads`() {
        assertEquals(
            listOf(
                ChordType.MajorTriad,
                ChordType.MinorTriad,
                ChordType.DiminishedTriad,
                ChordType.AugmentedTriad,
                ChordType.SusSecondTriad,
                ChordType.SusFourthTriad,
            ),
            StandardChords.allTriadPatterns.map { it.type },
        )
    }

    @Test
    fun `unknown types yield nothing`() {
        assertEquals(emptyList<ChordPattern>(), StandardChords.patterns(listOf(ChordType.None)))
    }

    @Test
    fun `chord factories cover every pattern`() {
        assertEquals(13, StandardChords.allChords(Pitch(4, NoteName.C)).size)
        assertEquals(6, StandardChords.allTriads(Pitch(4, NoteName.C)).size)
        assertEquals(7, StandardChords.standardSevenths(Pitch(4, NoteName.C)).size)
    }
}

class StandardScalesTest {

    @Test
    fun `catalogue holds all ten patterns`() {
        assertEquals(10, StandardScales.allPatterns.size)
        assertEquals(7, StandardScales.diadicPatterns.size)
    }

    @Test
    fun `C major scale spells C D E F G A B`() {
        val scale = Scale(
            StandardScales.patterns(listOf(ScaleType.Major)).single(),
            PitchClass(NoteName.C),
        )
        assertEquals(
            listOf("C", "D", "E", "F", "G", "A", "B"),
            scale.notes.map { it.noteName.symbol },
        )
    }

    @Test
    fun `A natural minor uses only naturals`() {
        val scale = Scale(
            StandardScales.patterns(listOf(ScaleType.Minor)).single(),
            PitchClass(NoteName.A),
        )
        assertEquals(
            listOf("A", "B", "C", "D", "E", "F", "G"),
            scale.notes.map { it.noteName.symbol },
        )
    }

    @Test
    fun `chromatic scale covers all twelve pitch classes`() {
        val scale = Scale(
            StandardScales.patterns(listOf(ScaleType.Chromatic)).single(),
            PitchClass(NoteName.C),
        )
        assertEquals(12, scale.notes.size)
        assertEquals(12, scale.notes.toSet().size)
    }

    @Test
    fun `scales are readable as a group of pitch classes`() {
        // What the original NoteGroupBase.GetBasicNotes existed for; covariance gives it
        // for free.
        val group: NoteGroup<PitchClass> = Chord(
            StandardChords.patterns(listOf(ChordType.MajorTriad)).single(),
            Pitch(4, NoteName.C),
        )
        assertEquals(3, group.notes.size)
    }
}
