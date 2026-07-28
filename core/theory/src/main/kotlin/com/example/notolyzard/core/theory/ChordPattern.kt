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

/** Ported from `ChordPattern`. */
class ChordPattern(
    val type: ChordType,
    intervals: List<Int>,
    name: String = "",
) : Pattern(intervals, name)
