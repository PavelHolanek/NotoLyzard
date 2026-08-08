package com.example.notolyzard.feature.scalesandchords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPicker
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPickerState
import com.example.notolyzard.ui.components.tonnetz.Tonnetz
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

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
        modifier = modifier,
    )
}

@Composable
fun ScalesAndChordsVisualizationContent(
    uiState: ScalesAndChordsVisualizationUiState,
    pickerState: NoteGroupsPickerState,
    onNoteClicked: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupColors = LocalNotePalette.current.groupColors
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

        // CircleOfFifths(
        //     layers = layers,
        //     onNoteClick = onNoteClicked,
        // )

        Tonnetz(
            layers = layers,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}
