package com.example.notolyzard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * A round, tappable button showing one note, ringed by a segment for every selected note
 * group that contains it.
 *
 * Stateless on purpose: it takes a [NoteCircleButtonState] and reports taps. It has no
 * ViewModel of its own, so the same button works inside the circle of fifths, inside a
 * grid, or on its own, and renders in a `@Preview` with no setup.
 */
@Composable
fun NoteCircleButton(
    state: NoteCircleButtonState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ringBackground = MaterialTheme.colorScheme.surfaceVariant
    val centreColor = MaterialTheme.colorScheme.surface
    val label = state.pitchClass.noteName.symbol

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val diameter = minOf(size.width, size.height)
            val outerRadius = diameter / 2f
            val innerRadius = outerRadius * CENTRE_RATIO
            val topLeft = Offset(
                x = (size.width - diameter) / 2f,
                y = (size.height - diameter) / 2f,
            )
            val ringSize = Size(diameter, diameter)

            val segments = state.segments
            if (segments.isEmpty()) {
                drawCircle(color = ringBackground, radius = outerRadius)
            } else {
                val sweep = 360f / segments.size
                segments.forEachIndexed { index, color ->
                    drawArc(
                        color = color,
                        startAngle = -90f + index * sweep,
                        sweepAngle = sweep,
                        useCenter = true,
                        topLeft = topLeft,
                        size = ringSize,
                    )
                }
            }
            drawCircle(color = centreColor, radius = innerRadius)
        }

        Text(
            text = label,
            color = state.textColor ?: MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/** How much of the button the plain centre takes up; the rest is the coloured ring. */
private const val CENTRE_RATIO = 0.72f

@Preview
@Composable
private fun NoteCircleButtonPreview() {
    NotoLyzardTheme {
        Box(modifier = Modifier.size(56.dp)) {
            NoteCircleButton(
                state = NoteCircleButtonState(
                    pitchClass = PitchClass(NoteName.C),
                    outerColors = listOf(Color.Red, null, Color.Blue),
                    textColor = null,
                ),
                onClick = {},
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
