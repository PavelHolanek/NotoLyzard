package com.example.notolyzard.feature.scalesandchords

import com.example.notolyzard.MainDispatcherRule
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScalesAndChordsVisualizationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var model: NoteGroupsModel
    private lateinit var viewModel: ScalesAndChordsVisualizationViewModel

    private val majorScale = StandardScales.patterns(listOf(ScaleType.Major)).single()

    /**
     * Built here rather than in a field initializer: the ViewModel touches
     * `viewModelScope` while constructing `uiState`, and that needs `Dispatchers.Main`
     * already swapped, which only happens once the rule has run.
     */
    @Before
    fun setUp() {
        model = NoteGroupsModel()
        viewModel = ScalesAndChordsVisualizationViewModel(model)
    }

    /**
     * `WhileSubscribed` only emits while something collects, so tests need a collector.
     * Reusing the rule's dispatcher keeps the test and `viewModelScope` on one scheduler,
     * and being unconfined makes emissions land synchronously.
     */
    private fun runWithCollector(block: suspend () -> Unit) =
        runTest(mainDispatcherRule.dispatcher) {
            backgroundScope.launch { viewModel.uiState.collect {} }
            block()
        }

    @Test
    fun `starts with one empty slot per picker row`() = runWithCollector {
        val state = viewModel.uiState.value

        assertEquals(viewModel.picker.rowCount, state.selectedNoteGroups.size)
        state.selectedNoteGroups.forEach { assertNull(it) }
    }

    @Test
    fun `a selection made through the picker reaches the page state`() = runWithCollector {
        viewModel.picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        viewModel.picker.onPatternSelected(0, majorScale)
        viewModel.picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        assertEquals(
            listOf("C", "D", "E", "F", "G", "A", "B"),
            viewModel.uiState.value.selectedNoteGroups[0]?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `tapping a note retunes rows that already have a pattern`() = runWithCollector {
        viewModel.picker.onPatternGroupSelected(0, NoteGroupsModel.SCALES)
        viewModel.picker.onPatternSelected(0, majorScale)
        viewModel.picker.onRootNoteSelected(0, PitchClass(NoteName.C))

        viewModel.onNoteClicked(PitchClass(NoteName.G))

        assertEquals(
            listOf("G", "A", "B", "C", "D", "E", "F#"),
            viewModel.uiState.value.selectedNoteGroups[0]?.notes?.map { it.noteName.symbol },
        )
    }

    @Test
    fun `tapping a note leaves rows without a pattern alone`() = runWithCollector {
        viewModel.onNoteClicked(PitchClass(NoteName.G))

        viewModel.uiState.value.selectedNoteGroups.forEach { assertNull(it) }
        viewModel.picker.uiState.value.rows.forEach { assertNull(it.rootNote) }
    }

    @Test
    fun `the page reflects a change made elsewhere in the shared model`() = runWithCollector {
        // Stands in for another screen writing to the same NoteGroupsModel.
        model.select(
            row = 1,
            group = com.example.notolyzard.core.theory.Scale(majorScale, PitchClass(NoteName.A)),
        )

        assertEquals(
            listOf("A", "B", "C#", "D", "E", "F#", "G#"),
            viewModel.uiState.value.selectedNoteGroups[1]?.notes?.map { it.noteName.symbol },
        )
    }
}
