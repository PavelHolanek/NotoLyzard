package com.example.notolyzard.feature.scalesandchords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notolyzard.NotoLyzardApplication
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPickerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ScalesAndChordsVisualizationViewModel(
    noteGroupsModel: NoteGroupsModel,
) : ViewModel() {
    val picker: NoteGroupsPickerState = NoteGroupsPickerState(noteGroupsModel)

    private val selectedVisualization = MutableStateFlow(Visualization.Default)

    val uiState: StateFlow<ScalesAndChordsVisualizationUiState> = combine(
        picker.shownNoteGroups,
        selectedVisualization,
    ) { noteGroups, visualization ->
        ScalesAndChordsVisualizationUiState(
            shownNoteGroups = noteGroups,
            visualization = visualization,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ScalesAndChordsVisualizationUiState(
            shownNoteGroups = List(NoteGroupsPickerState.DEFAULT_ROW_COUNT) { null },
        ),
    )

    fun onPreviousVisualization() = cycleVisualization(-1)

    fun onNextVisualization() = cycleVisualization(1)

    private fun cycleVisualization(step: Int) {
        selectedVisualization.update { it.shiftedBy(step) }
    }

    /** A note clicked in any visualization, handed to the picker as a picked note. */
    fun onNoteClicked(pitchClass: PitchClass) = picker.onNotePicked(pitchClass)

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
