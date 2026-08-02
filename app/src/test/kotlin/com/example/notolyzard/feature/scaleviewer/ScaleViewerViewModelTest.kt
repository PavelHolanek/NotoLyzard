package com.example.notolyzard.feature.scaleviewer

import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.ScaleType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A ViewModel holding only a StateFlow needs no test dispatcher and no Android runtime —
 * read `uiState.value` and assert. Once it starts launching coroutines, add
 * `kotlinx-coroutines-test` and a `MainDispatcherRule`.
 */
class ScaleViewerViewModelTest {

    @Test
    fun `starts on C major with every scale type offered`() {
        val state = ScaleViewerViewModel().uiState.value

        assertEquals(PitchClass(NoteName.C), state.rootNote)
        assertEquals(ScaleType.Major, state.scaleType)
        assertEquals(10, state.availableScaleTypes.size)
        assertEquals(
            listOf("C", "D", "E", "F", "G", "A", "B"),
            state.scale?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `choosing a root note rebuilds the scale`() {
        val viewModel = ScaleViewerViewModel()

        viewModel.onRootNoteSelected(PitchClass(NoteName.G))

        val state = viewModel.uiState.value
        assertEquals(PitchClass(NoteName.G), state.rootNote)
        assertEquals(
            listOf("G", "A", "B", "C", "D", "E", "F#"),
            state.scale?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `choosing a scale type rebuilds the scale and keeps the root`() {
        val viewModel = ScaleViewerViewModel()

        viewModel.onScaleTypeSelected(ScaleType.Minor)

        val state = viewModel.uiState.value
        assertEquals(PitchClass(NoteName.C), state.rootNote)
        assertEquals(ScaleType.Minor, state.scaleType)
        assertEquals(
            listOf("C", "D", "D#", "F", "G", "G#", "A#"),
            state.scale?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `scaleNotes exposes the scale as a set for highlighting`() {
        val viewModel = ScaleViewerViewModel()

        assertEquals(7, viewModel.uiState.value.scaleNotes.size)
    }
}
