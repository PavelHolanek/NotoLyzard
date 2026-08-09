package com.example.notolyzard.feature.scalesandchords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.components.CircleOfFifths
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPicker
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPickerState
import com.example.notolyzard.ui.components.tonnetz.Tonnetz
import com.example.notolyzard.ui.components.tonnetz.rememberTonnetzState
import com.example.notolyzard.ui.theme.LocalNotePalette

@Composable
fun ScalesAndChordsVisualizationScreen(
    modifier: Modifier = Modifier,
    viewModel: ScalesAndChordsVisualizationViewModel = viewModel(factory = ScalesAndChordsVisualizationViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScalesAndChordsVisualizationContent(
        uiState = uiState,
        pickerState = viewModel.picker,
        onNoteClicked = viewModel::onNoteClicked,
        onPreviousVisualization = viewModel::onPreviousVisualization,
        onNextVisualization = viewModel::onNextVisualization,
        modifier = modifier,
    )
}

@Composable
fun ScalesAndChordsVisualizationContent(
    uiState: ScalesAndChordsVisualizationUiState,
    pickerState: NoteGroupsPickerState,
    onNoteClicked: (PitchClass) -> Unit,
    onPreviousVisualization: () -> Unit,
    onNextVisualization: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupColors = LocalNotePalette.current.groupColors

    // Held here rather than inside Tonnetz, so switching away and back does not drop the
    // reader somewhere else on the lattice.
    val tonnetzState = rememberTonnetzState()
    val layers = remember(uiState.selectedNoteGroups, groupColors) {
        uiState.selectedNoteGroups.mapIndexed { index, group ->
            NoteGroupLayer(noteGroup = group, color = groupColors[index % groupColors.size])
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        NoteGroupsPicker(
            state = pickerState,
            rowColors = groupColors,
        )

        VisualizationSwitcher(
            visualization = uiState.visualization,
            onPrevious = onPreviousVisualization,
            onNext = onNextVisualization,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (uiState.visualization) {
                Visualization.Tonnetz -> Tonnetz(
                    layers = layers,
                    state = tonnetzState,
                    modifier = Modifier.fillMaxSize(),
                )

                Visualization.CircleOfFifths -> CircleOfFifths(
                    layers = layers,
                    onNoteClick = onNoteClicked,
                )
            }
        }
    }
}
