package com.example.notolyzard.core.theory

import com.example.notolyzard.core.theory.NoteName.Companion.SEMITONES_PER_OCTAVE

/** Ported from `ScaleType`. */
enum class ScaleType {
    None,

    Minor,
    Locrian,
    Major,
    Dorian,
    Phrygian,
    Lydian,
    Mixolydian,

    HarmonicMinor,
    MelodicMinor,

    Chromatic,
}

/** Ported from `ScalePattern`. */
class ScalePattern(
    val type: ScaleType,
    intervals: List<Int>,
    name: String = "",
) : Pattern(intervals, name) {

    /**
     * The pattern as a 12-element mask, one entry per semitone of the octave.
     * Ported from `GetMask()`.
     */
    fun mask(): List<Boolean> =
        List(SEMITONES_PER_OCTAVE) { i -> i == 0 || i in intervals }

    companion object {
        /**
         * Builds a pattern from a 12-element mask.
         *
         * Replaces the second `ScalePattern` constructor. The original used
         * `Debug.Assert`, which is compiled out of release builds; [require] always
         * checks, so a bad mask fails the same way in every build type.
         */
        fun fromMask(type: ScaleType, mask: List<Boolean>, name: String = ""): ScalePattern {
            require(mask.size == SEMITONES_PER_OCTAVE) { "Length of the mask must be 12" }
            require(mask[0]) { "Base note must be included" }
            val intervals = mask.indices.filter { mask[it] }
            return ScalePattern(type, intervals, name)
        }
    }
}
