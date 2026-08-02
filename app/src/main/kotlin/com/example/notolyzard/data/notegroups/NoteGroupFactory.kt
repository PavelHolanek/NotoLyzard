package com.example.notolyzard.data.notegroups

import com.example.notolyzard.core.theory.Chord
import com.example.notolyzard.core.theory.ChordPattern
import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.Pitch
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScalePattern

/** Octave used for chords when the caller only cares about pitch classes. */
const val DEFAULT_DISPLAY_OCTAVE: Int = 4

/**
 * Applies [pattern] to [rootNote], or returns `null` if either is missing.
 *
 * Scales are built from a [PitchClass] but chords need a [Pitch], so a chord requires an
 * octave the user never chose — hence [octave], which defaults to something arbitrary.
 * Every visualization built so far compares notes by name, so the octave is invisible;
 * pass a real one if that ever stops being true.
 *
 * Shared rather than inlined into one picker, because anything that turns a chosen pattern
 * into a group needs exactly this.
 */
fun noteGroupOf(
    pattern: Pattern?,
    rootNote: PitchClass?,
    octave: Int = DEFAULT_DISPLAY_OCTAVE,
): NoteGroup<PitchClass>? {
    if (pattern == null || rootNote == null) return null
    return when (pattern) {
        is ScalePattern -> Scale(pattern, rootNote)
        is ChordPattern -> Chord(pattern, Pitch(octave, rootNote.noteName))
        else -> null
    }
}
