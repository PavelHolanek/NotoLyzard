package com.example.notolyzard.data.notegroups

import com.example.notolyzard.core.theory.Chord
import com.example.notolyzard.core.theory.ChordPattern
import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.Pitch
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScalePattern

const val DEFAULT_DISPLAY_OCTAVE: Int = 4

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
