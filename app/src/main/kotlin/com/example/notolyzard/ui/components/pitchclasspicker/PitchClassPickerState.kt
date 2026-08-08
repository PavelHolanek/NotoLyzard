package com.example.notolyzard.ui.components.pitchclasspicker

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PitchClassPickerState(
    val pitchClasses: List<PitchClass> = chromaticPitchClasses(),
    val maxSelected: Int = UNLIMITED,
    private val onSelectionChanged: (List<PitchClass>) -> Unit = {},
) {

    init {
        require(maxSelected >= 1) { "maxSelected must be at least 1, was $maxSelected" }
    }

    private val _uiState = MutableStateFlow(
        PitchClassPickerUiState(pitchClasses = pitchClasses, selected = emptyList()),
    )
    val uiState: StateFlow<PitchClassPickerUiState> = _uiState.asStateFlow()

    fun onPitchClassClicked(pitchClass: PitchClass) = updateSelection { current ->
        when {
            pitchClass in current -> current - pitchClass
            current.size < maxSelected -> current + pitchClass
            else -> current.drop(current.size - maxSelected + 1) + pitchClass
        }
    }

    fun setSelection(selection: List<PitchClass>) = updateSelection {
        selection.distinct().takeLast(maxSelected)
    }

    fun clearSelection() = updateSelection { emptyList() }

    private fun updateSelection(transform: (List<PitchClass>) -> List<PitchClass>) {
        val updated = transform(_uiState.value.selected)
        require(pitchClasses.containsAll(updated)) {
            "Selection contains notes the picker does not offer: ${updated - pitchClasses.toSet()}"
        }

        if (updated == _uiState.value.selected) return

        _uiState.value = _uiState.value.copy(selected = updated)
        onSelectionChanged(updated)
    }

    companion object {
        const val UNLIMITED = Int.MAX_VALUE
    }
}

fun chromaticPitchClasses(): List<PitchClass> = NoteName.entries.map { PitchClass(it) }
