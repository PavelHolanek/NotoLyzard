package com.example.notolyzard.core.theory

/**
 * A shape that can be applied to a root note, expressed as semitone offsets from that
 * root. A major triad is `[0, 4, 7]`.
 *
 * Ported from `Pattern`.
 */
open class Pattern(val intervals: List<Int>, val name: String = "") {

    /**
     * Distances between consecutive notes of the pattern. The first entry is the offset
     * of the first note from the root, so a root-based pattern starts with 0.
     * `[0, 4, 7, 11]` yields `[0, 4, 3, 4]`.
     *
     * Ported from `GetGabs()` with its bug fixed: the original accumulated the cumulative
     * offsets instead of the gaps, which happened to work for up to three notes but gave
     * `[0, 4, 3, 0]` for the example above.
     */
    fun gaps(): List<Int> = intervals.mapIndexed { i, interval ->
        if (i == 0) interval else interval - intervals[i - 1]
    }
}
