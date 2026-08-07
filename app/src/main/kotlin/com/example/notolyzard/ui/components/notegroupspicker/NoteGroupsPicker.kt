package com.example.notolyzard.ui.components.notegroupspicker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notolyzard.core.theory.Pattern
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.data.notegroups.NoteGroupsModel
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

@Composable
fun NoteGroupsPicker(
    state: NoteGroupsPickerState,
    modifier: Modifier = Modifier,
    rowColors: List<Color> = LocalNotePalette.current.groupColors,
) {
    val uiState by state.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        uiState.rows.forEachIndexed { index, row ->
            PickerRow(
                row = row,
                patternGroupNames = uiState.patternGroups.keys.toList(),
                patterns = uiState.patternsFor(row),
                rootNotes = uiState.rootNotes,
                color = rowColors[index % rowColors.size],
                onPatternGroupSelected = { state.onPatternGroupSelected(index, it) },
                onPatternSelected = { state.onPatternSelected(index, it) },
                onRootNoteSelected = { state.onRootNoteSelected(index, it) },
            )
        }
    }
}

@Composable
private fun PickerRow(
    row: NoteGroupsPickerRow,
    patternGroupNames: List<String>,
    patterns: List<Pattern>,
    rootNotes: List<PitchClass>,
    color: Color,
    onPatternGroupSelected: (String?) -> Unit,
    onPatternSelected: (Pattern) -> Unit,
    onRootNoteSelected: (PitchClass) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PickerCell(
            label = row.patternGroupName,
            options = listOf(null) + patternGroupNames,
            optionLabel = { it ?: EMPTY_LABEL },
            onSelected = onPatternGroupSelected,
            color = color,
            modifier = Modifier.weight(1f),
        )
        PickerCell(
            label = row.pattern?.name,
            options = patterns,
            optionLabel = { it.name },
            onSelected = onPatternSelected,
            color = color,
            modifier = Modifier.weight(1.4f),
            makeClickable = row.patternGroupName != null,
        )
        PickerCell(
            label = row.rootNote?.noteName?.symbol,
            options = rootNotes,
            optionLabel = { it.noteName.symbol },
            onSelected = onRootNoteSelected,
            color = color,
            modifier = Modifier.weight(0.8f),
            makeClickable = row.patternGroupName != null,
        )
    }
}

@Composable
private fun <T> PickerCell(
    label: String?,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    makeClickable: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Text(
            text = label ?: EMPTY_LABEL,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .clickable(enabled = options.isNotEmpty() && makeClickable) { expanded = true }
                .padding(horizontal = 8.dp, vertical = 12.dp),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, containerColor = LocalNotePalette.current.noteCenter) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                )
            }
        }
    }
}

private const val EMPTY_LABEL = "—"

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NoteGroupsPickerPreview() {
    val state = remember { NoteGroupsPickerState(NoteGroupsModel()) }

    NotoLyzardTheme {
        NoteGroupsPicker(state = state)
    }
}
