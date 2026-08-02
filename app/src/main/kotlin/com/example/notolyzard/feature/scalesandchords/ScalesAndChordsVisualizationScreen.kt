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
import com.example.notolyzard.ui.components.notegrouppicker.NoteGroupPicker
import com.example.notolyzard.ui.components.notegrouppicker.NoteGroupPickerState
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import com.example.notolyzard.ui.theme.groupColors

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

/**
 * The page: a picker and a visualization, nothing else.
 *
 * This is also where colours enter — the ViewModel says *which* groups are selected and
 * this pairs them with theme colours, so the same selection renders correctly in light and
 * dark mode and the ViewModel never imports a Compose graphics type.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScalesAndChordsVisualizationContent(
    uiState: ScalesAndChordsVisualizationUiState,
    pickerState: NoteGroupPickerState,
    onNoteClicked: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = groupColors()
    val layers = remember(uiState.selectedNoteGroups, palette) {
        uiState.selectedNoteGroups.mapIndexed { index, group ->
            NoteGroupLayer(noteGroup = group, color = palette[index % palette.size])
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = "Circle of Fifths") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NoteGroupPicker(
                state = pickerState,
                rowColors = palette,
            )

            // TODO: replace with a visualization switcher once a keyboard or fretboard
            // exists. Everything it needs is already here: `layers` is the shared contract,
            // and only the choice of component changes.
            CircleOfFifths(
                layers = layers,
                onNoteClick = onNoteClicked,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ScalesAndChordsVisualizationContentPreview() {
    val model = remember { NoteGroupsModel() }
    val pickerState = remember { NoteGroupPickerState(model) }

    NotoLyzardTheme {
        ScalesAndChordsVisualizationContent(
            uiState = ScalesAndChordsVisualizationUiState(
                selectedNoteGroups = List(pickerState.rowCount) { null },
            ),
            pickerState = pickerState,
            onNoteClicked = {},
        )
    }
}
