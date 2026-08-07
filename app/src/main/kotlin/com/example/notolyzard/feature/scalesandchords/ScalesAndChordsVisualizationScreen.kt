package com.example.notolyzard.feature.scalesandchords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.ui.components.CircleOfFifths
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPicker
import com.example.notolyzard.ui.components.notegroupspicker.NoteGroupsPickerState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalesAndChordsVisualizationContent(
    uiState: ScalesAndChordsVisualizationUiState,
    pickerState: NoteGroupsPickerState,
    onNoteClicked: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalNotePalette.current
    val groupColors = palette.groupColors
    val layers = remember(uiState.selectedNoteGroups, groupColors) {
        uiState.selectedNoteGroups.mapIndexed { index, group ->
            NoteGroupLayer(noteGroup = group, color = groupColors[index % groupColors.size])
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = palette.background,
        topBar = { TopAppBar(title = { Text(text = "Scales and Chords") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NoteGroupsPicker(
                state = pickerState,
                rowColors = groupColors,
            )

            CircleOfFifths(
                layers = layers,
                onNoteClick = onNoteClicked,
            )
        }
    }
}
