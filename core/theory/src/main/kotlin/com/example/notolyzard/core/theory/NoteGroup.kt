package com.example.notolyzard.core.theory

/**
 * Any ordered collection of notes — a chord, a scale, a melodic fragment.
 *
 * Ported from `NoteGroupBase` + `NoteGroup<NoteClass>`. The `out` variance makes the
 * original `GetBasicNotes()` unnecessary: a `NoteGroup<Pitch>` already *is* a
 * `NoteGroup<PitchClass>`, so [notes] can be read as a list of [PitchClass] without
 * copying or casting.
 */
interface NoteGroup<out T : PitchClass> {
    val notes: List<T>
}
