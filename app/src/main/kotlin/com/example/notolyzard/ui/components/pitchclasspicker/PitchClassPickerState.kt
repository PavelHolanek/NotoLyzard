package com.example.notolyzard.ui.components.pitchclasspicker

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class PitchClassPickerState(
    initialPitchClasses: List<PitchClass> = chromaticPitchClasses(),
    val maxSelected: Int = UNLIMITED,
    private val onSelectionChanged: (List<PitchClass>) -> Unit = {},
) {

    init {
        require(maxSelected >= 1) { "maxSelected must be at least 1, was $maxSelected" }
    }

    private val _uiState = MutableStateFlow(
        PitchClassPickerUiState(pitchClasses = initialPitchClasses, selected = emptyList()),
    )
    val uiState: StateFlow<PitchClassPickerUiState> = _uiState.asStateFlow()

    val pitchClasses: List<PitchClass> get() = _uiState.value.pitchClasses

    fun randomize(random: Random = Random.Default) = reorder { it.shuffled(random) }

    fun sort() = reorder { it.sortedBy(PitchClass::semitone) }

    fun onPitchClassClicked(pitchClass: PitchClass) = updateSelection { current ->
        when {
            pitchClass in current -> current - pitchClass
            current.size < maxSelected -> current + pitchClass
            // Room has to be made: drop from the front, so the newest pick always survives.
            else -> current.drop(current.size - maxSelected + 1) + pitchClass
        }
    }

    fun setSelection(selection: List<PitchClass>) = updateSelection {
        selection.distinct().takeLast(maxSelected)
    }

    fun clearSelection() = updateSelection { emptyList() }

    private fun reorder(transform: (List<PitchClass>) -> List<PitchClass>) {
        _uiState.update { current -> current.copy(pitchClasses = transform(current.pitchClasses)) }
    }

    private fun updateSelection(transform: (List<PitchClass>) -> List<PitchClass>) {
        val current = _uiState.value
        val updated = transform(current.selected)
        require(current.pitchClasses.containsAll(updated)) {
            "Selection contains notes the picker does not offer: " +
                "${updated - current.pitchClasses.toSet()}"
        }

        if (updated == current.selected) return

        _uiState.value = current.copy(selected = updated)
        onSelectionChanged(updated)
    }

    companion object {
        const val UNLIMITED = Int.MAX_VALUE
    }
}

fun chromaticPitchClasses(): List<PitchClass> = NoteName.entries.map { PitchClass(it) }
