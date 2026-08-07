package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.PitchClass

data class NoteGroupLayer(
    val noteGroup: NoteGroup<PitchClass>?,
    val color: Color,
)

fun List<NoteGroupLayer>.colorsFor(pitchClass: PitchClass): List<Color?> = map { layer ->
    val contains = layer.noteGroup?.notes?.any { it.noteName == pitchClass.noteName } == true
    if (contains) layer.color else null
}
