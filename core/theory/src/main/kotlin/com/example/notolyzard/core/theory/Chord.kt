package com.example.notolyzard.core.theory

/**
 * A chord: a [ChordPattern] applied to a root [Pitch].
 *
 * Ported from `Chord`. Note that [notes] is built with [Pitch.plus], so it inherits that
 * operator's octave behaviour (the octave rolls over between G# and A).
 */
class Chord(val pattern: ChordPattern, val baseNote: Pitch) : NoteGroup<Pitch> {
    override val notes: List<Pitch> = pattern.intervals.map { baseNote + it }
}
