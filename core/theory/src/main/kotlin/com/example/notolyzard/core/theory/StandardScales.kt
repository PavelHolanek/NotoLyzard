package com.example.notolyzard.core.theory

/**
 * The built-in catalogue of scale patterns. Ported from `StandardScalesProvider`.
 *
 * All ten masks were checked against standard music theory during the port and are
 * correct, so they are reproduced verbatim.
 */
object StandardScales {

    private fun mask(vararg semitones: Int): List<Boolean> =
        List(12) { it in semitones }

    /** Every known pattern, in catalogue order. */
    val allPatterns: List<ScalePattern> = listOf(
        ScalePattern.fromMask(ScaleType.Minor, mask(0, 2, 3, 5, 7, 8, 10), "Minor"),
        ScalePattern.fromMask(ScaleType.Locrian, mask(0, 1, 3, 5, 6, 8, 10), "Locrian"),
        ScalePattern.fromMask(ScaleType.Major, mask(0, 2, 4, 5, 7, 9, 11), "Major"),
        ScalePattern.fromMask(ScaleType.Dorian, mask(0, 2, 3, 5, 7, 9, 10), "Dorian"),
        ScalePattern.fromMask(ScaleType.Phrygian, mask(0, 1, 3, 5, 7, 8, 10), "Phrygian"),
        ScalePattern.fromMask(ScaleType.Lydian, mask(0, 2, 4, 6, 7, 9, 11), "Lydian"),
        ScalePattern.fromMask(ScaleType.Mixolydian, mask(0, 2, 4, 5, 7, 9, 10), "Mixolydian"),
        ScalePattern.fromMask(ScaleType.Chromatic, mask(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11), "Chromatic"),
        ScalePattern.fromMask(ScaleType.HarmonicMinor, mask(0, 2, 3, 5, 7, 8, 11), "Harmonic minor"),
        ScalePattern.fromMask(ScaleType.MelodicMinor, mask(0, 2, 3, 5, 7, 9, 11), "Melodic minor"),
    )

    /** Patterns for the given [types], in catalogue order. */
    fun patterns(types: Collection<ScaleType>): List<ScalePattern> =
        allPatterns.filter { it.type in types }

    /** The seven modes of the major scale. Ported from `GetDiadicPatterns`. */
    val diadicPatterns: List<ScalePattern>
        get() = patterns(
            listOf(
                ScaleType.Minor,
                ScaleType.Locrian,
                ScaleType.Major,
                ScaleType.Dorian,
                ScaleType.Phrygian,
                ScaleType.Lydian,
                ScaleType.Mixolydian,
            ),
        )

    fun scales(types: Collection<ScaleType>, baseNote: PitchClass): List<Scale> =
        patterns(types).map { Scale(it, baseNote) }

    fun diadicScales(baseNote: PitchClass): List<Scale> =
        diadicPatterns.map { Scale(it, baseNote) }

    fun allScales(baseNote: PitchClass): List<Scale> =
        allPatterns.map { Scale(it, baseNote) }
}
