package com.example.notolyzard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * Shared component: a round, tappable button showing a single note.
 *
 * Used standalone and as the building block of [CircleOfFifths], so it must stay free of
 * any assumption about its surroundings — no fixed position, no knowledge of a scale.
 *
 * TODO: implement. What this needs beyond the placeholder below:
 *  - a circular shape sized from the incoming [Modifier] rather than a hardcoded size
 *  - visual states for [selected], [enabled] and pressed, drawn from [MaterialTheme]
 *  - a click target of at least 48.dp and a content description for accessibility
 */
@Composable
fun NoteCircleButton(
    note: PitchClass,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    // Placeholder so the screen runs; replace wholesale.
    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = note.noteName.symbol,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
private fun NoteCircleButtonPreview() {
    NotoLyzardTheme {
        NoteCircleButton(
            note = PitchClass(NoteName.C),
            selected = true,
            onClick = {},
        )
    }
}
