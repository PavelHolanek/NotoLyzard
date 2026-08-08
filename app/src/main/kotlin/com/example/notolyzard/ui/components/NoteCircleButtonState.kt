package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.PitchClass

data class NoteCircleButtonState(
    val pitchClass: PitchClass?,
    val outerColors: List<Color?>,
    val textColor: Color?,
    val label: String = pitchClass?.noteName?.symbol.orEmpty(),
) {
    val segments: List<Color> = outerColors.filterNotNull()
}
