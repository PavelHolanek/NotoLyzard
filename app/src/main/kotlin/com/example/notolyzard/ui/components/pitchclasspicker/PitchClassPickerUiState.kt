package com.example.notolyzard.ui.components.pitchclasspicker

import com.example.notolyzard.core.theory.PitchClass

data class PitchClassPickerUiState(
    val pitchClasses: List<PitchClass>,
    val selected: List<PitchClass>,
) {
    fun selectionIndexOf(pitchClass: PitchClass): Int? =
        selected.indexOf(pitchClass).takeIf { it >= 0 }

    fun isSelected(pitchClass: PitchClass): Boolean = pitchClass in selected
}
