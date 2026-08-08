package com.example.notolyzard.ui.components.pitchclasspicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.components.NoteCircleButton
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

private const val DEFAULT_COLUMNS = 4
private val DEFAULT_SPACING = 8.dp
@Composable
fun PitchClassPicker(
    state: PitchClassPickerState,
    modifier: Modifier = Modifier,
    columns: Int = DEFAULT_COLUMNS,
    spacing: Dp = DEFAULT_SPACING,
    selectionColors: List<Color> = LocalNotePalette.current.groupColors,
) {
    val uiState by state.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        uiState.pitchClasses.chunked(columns).forEach { rowNotes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                rowNotes.forEach { pitchClass ->
                    val selectionIndex = uiState.selectionIndexOf(pitchClass)
                    NoteCircleButton(
                        state = NoteCircleButtonState(
                            pitchClass = pitchClass,
                            outerColors = listOf(
                                selectionIndex?.let {
                                    selectionColors[it % selectionColors.size]
                                },
                            ),
                            textColor = null,
                        ),
                        onClick = { state.onPitchClassClicked(pitchClass) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                    )
                }
                repeat(columns - rowNotes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}