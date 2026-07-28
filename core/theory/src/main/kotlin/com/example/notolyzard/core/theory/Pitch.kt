package com.example.notolyzard.core.theory

/**
 * A note together with its octave.
 *
 * Ported from `NoteInOctave`. The arithmetic below is a **faithful 1:1 port of the C#
 * original, including its defects** — see the notes on each operator. Do not "clean up"
 * a formula here without deciding first whether the behaviour change is wanted.
 *
 * Unlike the original, [octave] is public. In the C# source it had no access modifier and
 * was therefore private, which made it unreadable from outside the class; that looked
 * unintentional rather than a design decision.
 */
class Pitch(val octave: Int, noteName: NoteName) : PitchClass(noteName) {

    /**
     * Transposes up by [semitones].
     *
     * Faithful to the original, which means:
     * - the octave rolls over between G# and A (not between B and C), because [NoteName]
     *   is ordered from A;
     * - the octave increment uses truncating division, so it is wrong for negative
     *   [semitones];
     * - only `semitones >= -12` is handled, because the wrap-around correction is a
     *   hardcoded `+ 12`. Lower values throw (the original produced a garbage note).
     */
    override operator fun plus(semitones: Int): Pitch = Pitch(
        octave + (semitone + semitones) / 12,
        NoteName.ofSemitone((semitone + semitones + 12) % 12),
    )

    /**
     * Transposes down by [semitones].
     *
     * Faithful to the original, which applies no modulo to the resulting note at all:
     * `a.Type - b`. In C# that silently produced an out-of-range `NoteType`; here it
     * throws via [NoteName.ofSemitone]. In practice this operator only works for
     * `semitones` in `0..semitone`.
     */
    override operator fun minus(semitones: Int): Pitch = Pitch(
        octave + (semitone - semitones - 12) / 12,
        NoteName.ofSemitone(semitone - semitones),
    )

    /**
     * Distance between two pitches.
     *
     * Faithful to the original, which **multiplies** the octave by the note index
     * (`a.Octave * a.Type - b.Octave * b.Type`) instead of scaling the octave by 12.
     * The result is not a meaningful interval and may be negative.
     */
    operator fun minus(other: Pitch): Int =
        (octave * semitone - other.octave * other.semitone) % 12

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        other as Pitch
        return noteName == other.noteName && octave == other.octave
    }

    override fun hashCode(): Int = 31 * noteName.hashCode() + octave

    override fun toString(): String = "${noteName.symbol}$octave"
}
