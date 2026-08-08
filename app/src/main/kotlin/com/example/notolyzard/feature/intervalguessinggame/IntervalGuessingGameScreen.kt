package com.example.notolyzard.feature.intervalguessinggame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.components.ArrowDirection
import com.example.notolyzard.ui.components.IntervalArrowButton
import com.example.notolyzard.ui.components.IntervalArrowButtonState
import com.example.notolyzard.ui.components.NoteCircleButton
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.components.pitchclasspicker.PitchClassPicker
import com.example.notolyzard.ui.components.pitchclasspicker.PitchClassPickerState
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

@Composable
fun IntervalGuessingGameScreen(
    modifier: Modifier = Modifier,
    viewModel: IntervalGuessingGameViewModel = viewModel(factory = IntervalGuessingGameViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IntervalGuessingGameContent(
        uiState = uiState,
        pickerState = viewModel.pickerState,
        onTopArrowClicked = viewModel::onTopArrowButtonClicked,
        onBottomArrowClicked = viewModel::onBottomArrowButtonClicked,
        onCenterNoteClicked = viewModel::onCenterNoteClicked,
        modifier = modifier,
    )
}


@Composable
fun IntervalGuessingGameContent(
    uiState: IntervalGuessingGameUiState,
    pickerState: PitchClassPickerState,
    onTopArrowClicked: () -> Unit,
    onBottomArrowClicked: () -> Unit,
    onCenterNoteClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalNotePalette.current
    val feedbackColor = when (uiState.feedback) {
        AnswerFeedback.Correct -> palette.correct
        AnswerFeedback.Incorrect -> palette.incorrect
        null -> null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ELEMENT_SPACING),
    ) {
        UpperNoteRow(
            noteState = uiState.upperNoteState.flashing(
                feedbackColor.takeIf { uiState.guessDirection == ArrowDirection.Up },
            ),
            streak = uiState.streak,
        )

        IntervalArrowButton(
            state = uiState.topArrowState,
            direction = ArrowDirection.Up,
            onClick = onTopArrowClicked,
            modifier = Modifier.height(ARROW_HEIGHT),
        )

        NoteCircleButton(
            state = uiState.baseNoteState,
            onClick = onCenterNoteClicked,
            modifier = Modifier.size(NOTE_BUTTON_SIZE),
        )

        IntervalArrowButton(
            state = uiState.bottomArrowState,
            direction = ArrowDirection.Down,
            onClick = onBottomArrowClicked,
            modifier = Modifier.height(ARROW_HEIGHT),
        )

        NoteCircleButton(
            state = uiState.bottomNoteState.flashing(
                feedbackColor.takeIf { uiState.guessDirection == ArrowDirection.Down },
            ),
            onClick = {},
            modifier = Modifier.size(NOTE_BUTTON_SIZE),
        )

        PitchClassPicker(
            state = pickerState,
            modifier = Modifier.width(PICKER_WIDTH),
            columns = PICKER_COLUMNS,
            spacing = PICKER_SPACING,
            selectionColors = listOf(Color.Green),
        )
    }
}

@Composable
private fun UpperNoteRow(
    noteState: NoteCircleButtonState,
    streak: UInt,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        NoteCircleButton(
            state = noteState,
            onClick = {},
            modifier = Modifier
                .align(Alignment.Center)
                .size(NOTE_BUTTON_SIZE),
        )

        Text(
            text = streak.toString(),
            color = LocalNotePalette.current.noteText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

private fun NoteCircleButtonState.flashing(color: Color?): NoteCircleButtonState =
    if (color == null) this else copy(outerColors = listOf(color))

private val NOTE_BUTTON_SIZE = 75.dp
private val ARROW_HEIGHT = 72.dp
private val ELEMENT_SPACING = 16.dp

private val PICKER_BUTTON_SIZE = 70.dp
private val PICKER_SPACING = 15.dp
private const val PICKER_COLUMNS = 4
private val PICKER_WIDTH =
    PICKER_BUTTON_SIZE * PICKER_COLUMNS + PICKER_SPACING * (PICKER_COLUMNS - 1)
