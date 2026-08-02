package com.example.notolyzard.feature.scalesandchords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notolyzard.NotoLyzardApplication
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.ui.components.notegrouppicker.NoteGroupPickerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Owns the state holders the page is built from and exposes what the visualization needs.
 *
 * There is almost nothing here, and that is the point: the picker's rules live in
 * [NoteGroupPickerState] so other screens reuse them, and the circle's geometry lives in
 * the circle component so other data can drive it. A screen ViewModel's job is to decide
 * *which* holders make up this screen, not to reimplement their behaviour.
 *
 * When the circle is replaced by a visualization switcher, the switcher's state holder
 * joins [picker] here and [uiState] stays as it is.
 */
class ScalesAndChordsVisualizationViewModel(
    noteGroupsModel: NoteGroupsModel,
) : ViewModel() {

    /**
     * Exposed rather than wrapped: [com.example.notolyzard.ui.components.notegrouppicker.NoteGroupPicker]
     * takes this holder directly, the way `LazyColumn` takes a `LazyListState`. Copying its
     * state into [ScalesAndChordsVisualizationUiState] would mean two sources of truth for one thing.
     */
    val picker: NoteGroupPickerState = NoteGroupPickerState(noteGroupsModel)

    val uiState: StateFlow<ScalesAndChordsVisualizationUiState> = noteGroupsModel.selectedNoteGroups
        .map { ScalesAndChordsVisualizationUiState(selectedNoteGroups = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ScalesAndChordsVisualizationUiState(
                selectedNoteGroups = List(NoteGroupPickerState.DEFAULT_ROW_COUNT) { null },
            ),
        )

    /**
     * A tap in the visualization sets the root note of every row that already has a
     * pattern, which is what makes the circle usable as an input and not just a display.
     */
    fun onNoteClicked(pitchClass: PitchClass) {
        picker.uiState.value.rows.forEachIndexed { row, selection ->
            if (selection.pattern != null) picker.onRootNoteSelected(row, pitchClass)
        }
    }

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as NotoLyzardApplication
                ScalesAndChordsVisualizationViewModel(application.container.noteGroupsModel)
            }
        }
    }
}
