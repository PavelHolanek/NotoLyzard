package com.example.notolyzard.core.theory

/**
 * The built-in catalogue of chord patterns. Ported from `StandardChordsProvider`
 * (a static class, hence an `object` here).
 *
 * The interval lists are a faithful 1:1 port and **two of them are musically wrong** —
 * see [allPatterns].
 */
object StandardChords {

    /**
     * Every known pattern, in catalogue order. All lookups preserve this order, matching
     * the original `GetPatterns`, which iterated the catalogue and filtered it rather than
     * following the order of the requested types.
     */
    val allPatterns: List<ChordPattern> = listOf(
        ChordPattern(ChordType.MajorTriad, listOf(0, 4, 7), "Major Triad"),
        ChordPattern(ChordType.MinorTriad, listOf(0, 3, 7), "Minor Triad"),
        ChordPattern(ChordType.DiminishedTriad, listOf(0, 3, 6), "Diminished Triad"),
        ChordPattern(ChordType.AugmentedTriad, listOf(0, 4, 8), "Augmented Triad"),
        ChordPattern(ChordType.MajorSeventh, listOf(0, 4, 7, 11), "Major Seventh"),
        ChordPattern(ChordType.MinorSeventh, listOf(0, 3, 7, 10), "Minor Seventh"),
        // Faithful to the original. Musically this should be [0, 4, 7, 10] — the 3 looks
        // like a typo for 7, which makes this pattern a minor/major hybrid.
        ChordPattern(ChordType.DominantSeventh, listOf(0, 4, 3, 10), "Dominant Seventh"),
        ChordPattern(ChordType.HalfDiminishedSeventh, listOf(0, 3, 6, 10), "Half Diminished Seventh"),
        ChordPattern(ChordType.DiminishedSeventh, listOf(0, 3, 6, 9), "Diminished Seventh"),
        ChordPattern(ChordType.MinorMajorSeventh, listOf(0, 3, 7, 11), "Minor Major Seventh"),
        ChordPattern(ChordType.AugmentedMajorSeventh, listOf(0, 4, 8, 11), "Augmented Major Seventh"),
        // Faithful to the original, where the two sus patterns are swapped: sus2 should be
        // [0, 2, 7] and sus4 should be [0, 5, 7].
        ChordPattern(ChordType.SusSecondTriad, listOf(0, 5, 7), "Sus Second"),
        ChordPattern(ChordType.SusFourthTriad, listOf(0, 2, 7), "Sus Fourth"),
    )

    /** Patterns for the given [types], in catalogue order. */
    fun patterns(types: Collection<ChordType>): List<ChordPattern> =
        allPatterns.filter { it.type in types }

    val standardTriadPatterns: List<ChordPattern>
        get() = patterns(
            listOf(
                ChordType.MajorTriad,
                ChordType.MinorTriad,
                ChordType.AugmentedTriad,
                ChordType.DiminishedTriad,
            ),
        )

    val susTriadPatterns: List<ChordPattern>
        get() = patterns(
            listOf(
                ChordType.SusSecondTriad,
                ChordType.SusFourthTriad,
            ),
        )

    /** Standard triads followed by sus triads, matching the original's `Concat`. */
    val allTriadPatterns: List<ChordPattern>
        get() = standardTriadPatterns + susTriadPatterns

    val standardSeventhPatterns: List<ChordPattern>
        get() = patterns(
            listOf(
                ChordType.MajorSeventh,
                ChordType.MinorSeventh,
                ChordType.DominantSeventh,
                ChordType.MinorMajorSeventh,
                ChordType.AugmentedMajorSeventh,
                ChordType.DiminishedSeventh,
                ChordType.HalfDiminishedSeventh,
            ),
        )

    fun chords(types: Collection<ChordType>, baseNote: Pitch): List<Chord> =
        patterns(types).map { Chord(it, baseNote) }

    fun standardTriads(baseNote: Pitch): List<Chord> =
        standardTriadPatterns.map { Chord(it, baseNote) }

    fun susTriads(baseNote: Pitch): List<Chord> =
        susTriadPatterns.map { Chord(it, baseNote) }

    fun allTriads(baseNote: Pitch): List<Chord> =
        allTriadPatterns.map { Chord(it, baseNote) }

    fun standardSevenths(baseNote: Pitch): List<Chord> =
        standardSeventhPatterns.map { Chord(it, baseNote) }

    fun allChords(baseNote: Pitch): List<Chord> =
        allPatterns.map { Chord(it, baseNote) }
}
