package com.example.notolyzard.feature.scaleviewer

import androidx.lifecycle.ViewModel
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the state of the Scale Viewer screen and turns user events into new state.
 *
 * No Android or Compose types cross this boundary — the screen sends events in, a
 * [ScaleViewerUiState] comes out, which is what makes both sides testable on their own.
 */
class ScaleViewerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<ScaleViewerUiState> = _uiState.asStateFlow()

    fun onRootNoteSelected(note: PitchClass) {
        setState { it.copy(rootNote = note) }
    }

    fun onScaleTypeSelected(type: ScaleType) {
        setState { it.copy(scaleType = type) }
    }

    /**
     * Applies [transform] and then recomputes everything derived from it, so the scale can
     * never drift out of sync with the root note and type that produced it.
     */
    private fun setState(transform: (ScaleViewerUiState) -> ScaleViewerUiState) {
        _uiState.update { current ->
            val next = transform(current)
            next.copy(scale = buildScale(next.rootNote, next.scaleType))
        }
    }

    private companion object {
        val DEFAULT_ROOT = PitchClass(NoteName.C)
        val DEFAULT_TYPE = ScaleType.Major

        fun buildScale(rootNote: PitchClass, type: ScaleType): Scale? =
            StandardScales.patterns(listOf(type)).firstOrNull()?.let { Scale(it, rootNote) }

        fun initialState() = ScaleViewerUiState(
            rootNote = DEFAULT_ROOT,
            scaleType = DEFAULT_TYPE,
            availableScaleTypes = StandardScales.allPatterns.map { it.type },
            scale = buildScale(DEFAULT_ROOT, DEFAULT_TYPE),
        )
    }
}
