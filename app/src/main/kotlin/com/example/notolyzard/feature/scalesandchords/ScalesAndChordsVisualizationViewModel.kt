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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ScalesAndChordsVisualizationViewModel(
    noteGroupsModel: NoteGroupsModel,
) : ViewModel() {
    val picker: NoteGroupsPickerState = NoteGroupsPickerState(noteGroupsModel)

    val uiState: StateFlow<ScalesAndChordsVisualizationUiState> = noteGroupsModel.selectedNoteGroups
        .map { ScalesAndChordsVisualizationUiState(selectedNoteGroups = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ScalesAndChordsVisualizationUiState(
                selectedNoteGroups = List(NoteGroupsPickerState.DEFAULT_ROW_COUNT) { null },
            ),
        )

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
