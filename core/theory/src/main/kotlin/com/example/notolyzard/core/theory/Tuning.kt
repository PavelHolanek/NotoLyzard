package com.example.notolyzard.core.theory

/**
 * The notes a fretted instrument's strings sound when played open.
 *
 * [strings] runs from the lowest string to the highest, the order tunings are usually
 * written in ("E A D G B E" for a guitar). Only pitch classes are kept: which fret sounds
 * which note does not depend on the octave.
 */
data class Tuning(val strings: List<PitchClass>) {

    init {
        require(strings.isNotEmpty()) { "A tuning needs at least one string" }
    }

    val stringCount: Int get() = strings.size

    /** The note sounded on [string] (0 = lowest) held down at [fret] (0 = open). */
    fun pitchClassAt(string: Int, fret: Int): PitchClass = strings[string] + fret

    companion object {
        /** Standard six-string guitar tuning, E A D G B E from the lowest string. */
        val Standard = Tuning(
            listOf(NoteName.E, NoteName.A, NoteName.D, NoteName.G, NoteName.B, NoteName.E)
                .map(::PitchClass),
        )
    }
}
