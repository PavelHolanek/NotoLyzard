package com.example.notolyzard.core.theory

/**
 * A shape that can be applied to a root note, expressed as semitone offsets from that
 * root. A major triad is `[0, 4, 7]`.
 *
 * Ported from `Pattern`.
 */
open class Pattern(val intervals: List<Int>, val name: String = "") {

    /**
     * Distances between consecutive notes of the pattern.
     *
     * Faithful 1:1 port of `GetGabs()`, **including its bug**: the running total
     * accumulates the cumulative offsets instead of the gaps, so the result is only
     * correct for patterns of up to three notes. `[0, 4, 7]` yields `[0, 4, 3]`
     * (correct), but `[0, 4, 7, 11]` yields `[0, 4, 3, 0]` instead of `[0, 4, 3, 4]`.
     *
     * A correct implementation would be
     * `intervals.mapIndexed { i, v -> if (i == 0) 0 else v - intervals[i - 1] }`.
     */
    fun gaps(): List<Int> {
        val value = mutableListOf<Int>()
        var accumulated = 0
        for (interval in intervals) {
            value.add(interval - accumulated)
            accumulated += interval
        }
        return value
    }
}
