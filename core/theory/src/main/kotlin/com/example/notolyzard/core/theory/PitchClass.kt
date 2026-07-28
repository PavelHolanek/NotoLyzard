package com.example.notolyzard.core.theory

import com.example.notolyzard.core.theory.NoteName.Companion.SEMITONES_PER_OCTAVE

/**
 * A note without octave information — a pitch class.
 *
 * Ported from `BasicNote`. Immutable: transposing returns a new instance instead of
 * mutating [noteName], which keeps the type stable for Compose.
 */
open class PitchClass(val noteName: NoteName) {

    /** Semitone index of this pitch class, 0..11 (A = 0, see [NoteName]). */
    val semitone: Int get() = noteName.ordinal

    /** Transposes up by [semitones], wrapping within the octave. */
    open operator fun plus(semitones: Int): PitchClass =
        PitchClass(NoteName.ofSemitone(floorMod(semitone + semitones, SEMITONES_PER_OCTAVE)))

    /** Transposes down by [semitones], wrapping within the octave. */
    open operator fun minus(semitones: Int): PitchClass = this + (-semitones)

    /** Distance from [other] up to this pitch class, 0..11. */
    operator fun minus(other: PitchClass): Int =
        floorMod(semitone - other.semitone, SEMITONES_PER_OCTAVE)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other::class != this::class) return false
        return noteName == (other as PitchClass).noteName
    }

    override fun hashCode(): Int = noteName.hashCode()

    override fun toString(): String = noteName.symbol
}

/**
 * Euclidean modulo — always returns a non-negative result, unlike Kotlin's `%`.
 * Ported from the private `BasicNote.modulo` helper.
 */
internal fun floorMod(a: Int, b: Int): Int {
    val result = a % b
    return if (result < 0) result + b else result
}
