package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.PitchClass

/**
 * Everything one note button draws.
 *
 * [outerColors] has one entry per selected note group, `null` where that group does not
 * contain this note — the same shape the old `NoteButton.Colors` collection had, except
 * the nulls are kept so a colour's index still identifies its group.
 */
data class NoteCircleButtonState(
    val pitchClass: PitchClass,
    val outerColors: List<Color?>,
    val textColor: Color?,
) {
    /** The colours actually drawn as ring segments, in order. */
    val segments: List<Color> = outerColors.filterNotNull()
}
