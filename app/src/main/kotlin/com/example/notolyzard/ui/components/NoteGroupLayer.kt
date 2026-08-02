package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.PitchClass

/**
 * One selected note group together with the colour that identifies it on screen.
 *
 * This is the contract every visualization of note groups shares — the circle of fifths
 * today, a keyboard or a guitar fretboard later. They differ wildly in what they draw
 * (the circle has two rings of round buttons, a keyboard has several keys per pitch
 * class), but they all answer the same question: *which of the selected groups contains
 * this note?*
 *
 * Keeping the contract at this level is what lets a visualization switcher be a `when`
 * over one shared state instead of three parallel ViewModel hierarchies.
 *
 * A `null` [noteGroup] means the slot exists but nothing is chosen in it yet, matching
 * the nullable rows of [com.example.notolyzard.data.notegroups.NoteGroupsModel].
 */
data class NoteGroupLayer(
    val noteGroup: NoteGroup<PitchClass>?,
    val color: Color,
)

/**
 * For [pitchClass], the colour of every layer that contains it, `null` where it does not.
 *
 * The returned list is always the same length as the receiver, so the index of a colour
 * still identifies its layer. Ported from the old `CircleViewModel.setUpNoteGroups()`,
 * with two differences: it is a pure function rather than a mutation of shared adapters,
 * and it does not assume a fixed number of layers — the original looped `i < 4` and threw
 * when fewer rows were selected.
 *
 * Comparison is by [com.example.notolyzard.core.theory.NoteName], so a chord's
 * [com.example.notolyzard.core.theory.Pitch] matches regardless of its octave.
 */
fun List<NoteGroupLayer>.colorsFor(pitchClass: PitchClass): List<Color?> = map { layer ->
    val contains = layer.noteGroup?.notes?.any { it.noteName == pitchClass.noteName } == true
    if (contains) layer.color else null
}
