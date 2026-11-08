package com.example.notolyzard.core.theory

/** Ported from `ChordType`. */
enum class ChordType {
    None,

    // Triads
    MajorTriad,
    MinorTriad,
    DiminishedTriad,
    AugmentedTriad,

    SusSecondTriad,
    SusFourthTriad,

    // Sevenths
    MajorSeventh,
    MinorSeventh,
    DominantSeventh,
    HalfDiminishedSeventh,
    DiminishedSeventh,
    MinorMajorSeventh,
    AugmentedMajorSeventh,
}

/**
 * Ported from `ChordPattern`.
 *
 * [symbol] is not in the original. It is the chord-symbol suffix written after the root,
 * as on a lead sheet: `m7` makes `Cm7`. It is empty for a major triad, which is written as
 * its root alone. It sticks to plain characters, with `b` and `#` for flat and sharp as in
 * [NoteName.symbol], so it renders in any font.
 */
class ChordPattern(
    val type: ChordType,
    intervals: List<Int>,
    name: String = "",
    val symbol: String,
) : Pattern(intervals, name)
