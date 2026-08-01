package com.example.notolyzard.core.theory

import com.example.notolyzard.core.theory.NoteName.Companion.SEMITONES_PER_OCTAVE

/**
 * A note together with its octave.
 *
 * Ported from `NoteInOctave`, with its arithmetic corrected — see the notes on each
 * operator for what changed against the C# original.
 *
 * All operators are expressed in terms of [absoluteSemitone], which is the single source
 * of truth for pitch arithmetic. Because [NoteName] is ordered from A, an octave here runs
 * A..G#, so the octave number changes between G# and A rather than between B and C.
 *
 * Unlike the original, [octave] is public. In the C# source it had no access modifier and
 * was therefore private, which made it unreadable from outside the class.
 */
class Pitch(val octave: Int, noteName: NoteName) : PitchClass(noteName) {

    /**
     * Absolute position of this pitch in semitones, counted from A in octave 0.
     * Monotonic and signed, so it can be compared and subtracted directly.
     */
    val absoluteSemitone: Int get() = octave * SEMITONES_PER_OCTAVE + semitone

    /**
     * Transposes up by [semitones], carrying into the octave as needed.
     *
     * Fixed: the original computed the octave with truncating division and corrected the
     * note with a hardcoded `+ 12`, so it was wrong for negative offsets and broke
     * entirely below -12. Flooring division and Euclidean modulo make it correct for any
     * offset.
     */
    override operator fun plus(semitones: Int): Pitch = of(absoluteSemitone + semitones)

    /**
     * Transposes down by [semitones].
     *
     * Fixed: the original applied no modulo to the resulting note (`a.Type - b`), which
     * produced an out-of-range `NoteType` whenever it crossed below A.
     */
    override operator fun minus(semitones: Int): Pitch = this + (-semitones)

    /**
     * Distance from [other] up to this pitch, in semitones. Negative if this pitch is
     * lower.
     *
     * Fixed: the original multiplied the octave by the note index
     * (`a.Octave * a.Type - b.Octave * b.Type`) and took it modulo 12, which did not
     * produce an interval at all.
     */
    operator fun minus(other: Pitch): Int = absoluteSemitone - other.absoluteSemitone

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        other as Pitch
        return noteName == other.noteName && octave == other.octave
    }

    override fun hashCode(): Int = 31 * noteName.hashCode() + octave

    override fun toString(): String = "${noteName.symbol}$octave"

    companion object {
        /** Builds a pitch from an [absoluteSemitone] value, valid for any integer. */
        fun of(absoluteSemitone: Int): Pitch = Pitch(
            absoluteSemitone.floorDiv(SEMITONES_PER_OCTAVE),
            NoteName.ofSemitone(absoluteSemitone.mod(SEMITONES_PER_OCTAVE)),
        )
    }
}
