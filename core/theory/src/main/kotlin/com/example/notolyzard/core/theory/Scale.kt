package com.example.notolyzard.core.theory

/**
 * A scale: a [ScalePattern] applied to a root [PitchClass].
 *
 * Ported from `Scale`.
 */
class Scale(val pattern: ScalePattern, val baseNote: PitchClass) : NoteGroup<PitchClass> {
    override val notes: List<PitchClass> = pattern.intervals.map { baseNote + it }
}
