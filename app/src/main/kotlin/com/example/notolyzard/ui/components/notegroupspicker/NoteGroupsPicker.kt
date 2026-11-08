package com.example.notolyzard.ui.components.notegroupspicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
                onShownToggled = { state.onShownToggled(index) },
                onFollowPickedNotesToggled = { state.onFollowPickedNotesToggled(index) },
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
    onShownToggled: () -> Unit,
    onFollowPickedNotesToggled: () -> Unit,
) {
    Row(
        // Intrinsic height, so the toggles can be as tall as the cells and easy to hit.
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PickerToggle(
            checked = row.isShown,
            color = color,
            onToggle = onShownToggled,
            description = "Show note group",
            shape = { dot() },
            modifier = Modifier
                .width(TOGGLE_WIDTH)
                .fillMaxHeight(),
        )
        PickerCell(
            label = row.patternGroupName,
            options = listOf(null) + patternGroupNames,
            optionLabel = { it ?: EMPTY_LABEL },
            onSelected = onPatternGroupSelected,
            color = color,
            // Narrower than the rest to make room for the toggle in front of it; "Scales"
            // and "Chords" still fit.
            modifier = Modifier.weight(0.7f),
        )
        PickerCell(
            // Closed, a chord shows just its symbol; the open menu keeps the full names.
            label = row.patternLabel,
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
            // Narrow, as its longest label is two characters; the toggle after it took the rest.
            modifier = Modifier.weight(0.5f),
            makeClickable = row.patternGroupName != null,
        )
        PickerToggle(
            checked = row.followsPickedNotes,
            color = color,
            onToggle = onFollowPickedNotesToggled,
            description = "Root note follows picked notes",
            shape = { pointerTriangle() },
            modifier = Modifier
                .width(TOGGLE_WIDTH)
                .fillMaxHeight(),
        )
    }
}

/**
 * A small on/off switch drawn as [shape]: filled with the row's [color] while on, just a
 * black outline while off. The two toggles of a row differ only in their shape.
 */
@Composable
private fun PickerToggle(
    checked: Boolean,
    color: Color,
    onToggle: () -> Unit,
    description: String,
    shape: DrawScope.() -> Path,
    modifier: Modifier = Modifier,
) {
    val outline = LocalNotePalette.current.outline

    Canvas(
        modifier = modifier
            .toggleable(value = checked, role = Role.Switch, onValueChange = { onToggle() })
            .semantics { contentDescription = description },
    ) {
        val path = shape()
        if (checked) drawPath(path = path, color = color)
        drawPath(
            path = path,
            color = outline,
            style = Stroke(width = TOGGLE_OUTLINE_WIDTH.toPx(), join = StrokeJoin.Round),
        )
    }
}

/** A circle in the middle of the canvas, as tall as [pointerTriangle]. */
private fun DrawScope.dot(): Path = Path().apply {
    addOval(Rect(center = center, radius = TOGGLE_SHAPE_HEIGHT.toPx() / 2f))
}

/** A triangle in the middle of the canvas, its tip pointing left at the root note. */
private fun DrawScope.pointerTriangle(): Path {
    val halfWidth = TOGGLE_TRIANGLE_WIDTH.toPx() / 2f
    val halfHeight = TOGGLE_SHAPE_HEIGHT.toPx() / 2f
    return Path().apply {
        moveTo(center.x - halfWidth, center.y)
        lineTo(center.x + halfWidth, center.y - halfHeight)
        lineTo(center.x + halfWidth, center.y + halfHeight)
        close()
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

private val TOGGLE_WIDTH = 32.dp
private val TOGGLE_SHAPE_HEIGHT = 14.dp
private val TOGGLE_TRIANGLE_WIDTH = 12.dp
private val TOGGLE_OUTLINE_WIDTH = 1.5.dp

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun NoteGroupsPickerPreview() {
    val state = remember { NoteGroupsPickerState(NoteGroupsModel()) }

    NotoLyzardTheme {
        NoteGroupsPicker(state = state)
    }
}
