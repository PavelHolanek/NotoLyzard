package com.example.notolyzard.feature.scaleviewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.ui.components.CircleOfFifths
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * Screen entry point. Its only job is to connect the ViewModel to [ScaleViewerContent] —
 * keep it this thin, so that all layout stays previewable.
 */
@Composable
fun ScaleViewerScreen(
    modifier: Modifier = Modifier,
    viewModel: ScaleViewerViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScaleViewerContent(
        uiState = uiState,
        onRootNoteSelected = viewModel::onRootNoteSelected,
        onScaleTypeSelected = viewModel::onScaleTypeSelected,
        modifier = modifier,
    )
}

/**
 * The screen's layout. Stateless: it takes a [ScaleViewerUiState] and reports events, so
 * it can be rendered in a `@Preview` and asserted on in a UI test without a ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaleViewerContent(
    uiState: ScaleViewerUiState,
    onRootNoteSelected: (PitchClass) -> Unit,
    onScaleTypeSelected: (ScaleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = "Scale Viewer") }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ScalesPicker(
                scaleTypes = uiState.availableScaleTypes,
                selectedType = uiState.scaleType,
                onTypeSelected = onScaleTypeSelected,
            )

            CircleOfFifths(
                highlighted = uiState.scaleNotes,
                selected = uiState.rootNote,
                onNoteClick = onRootNoteSelected,
            )

            // TODO: replace with a proper rendering of the scale — a staff, a keyboard, or
            // both. Until then, show the notes as text so the wiring is visible.
            Text(
                text = uiState.scale?.notes?.joinToString(" ") { it.noteName.symbol }
                    ?: "No scale selected",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScaleViewerContentPreview() {
    val root = PitchClass(NoteName.C)
    val pattern = StandardScales.patterns(listOf(ScaleType.Major)).single()

    NotoLyzardTheme {
        ScaleViewerContent(
            uiState = ScaleViewerUiState(
                rootNote = root,
                scaleType = ScaleType.Major,
                availableScaleTypes = StandardScales.allPatterns.map { it.type },
                scale = Scale(pattern, root),
            ),
            onRootNoteSelected = {},
            onScaleTypeSelected = {},
        )
    }
}
