package com.example.notolyzard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NoteCircleButton(
    state: NoteCircleButtonState,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val palette = LocalNotePalette.current
    val textMeasurer = rememberTextMeasurer()
    val label = state.label

    // The label is drawn rather than composed as a Text: its font scales with the button,
    // which a Text could only learn in a subcomposition, and screens such as the fretboard
    // hold over a hundred of these buttons.
    Box(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .semantics { if (label.isNotEmpty()) contentDescription = label }
            .drawWithCache {
                val diameter = size.minDimension
                val labelLayout = measureNoteLabel(textMeasurer, state, diameter, palette)
                onDrawBehind {
                    drawNoteCircle(state.segments, labelLayout, center, diameter, palette)
                }
            },
    )
}

/**
 * Lays out the label of [state] for a note [diameter] pixels across, or returns null when
 * there is none. Separate from [drawNoteCircle] so that a caller can measure once and draw
 * many times.
 */
internal fun Density.measureNoteLabel(
    textMeasurer: TextMeasurer,
    state: NoteCircleButtonState,
    diameter: Float,
    palette: NotePalette,
): TextLayoutResult? {
    if (state.label.isEmpty()) return null
    return textMeasurer.measure(
        text = state.label,
        style = TextStyle(
            color = state.textColor ?: palette.noteText,
            fontSize = (diameter.toDp().value * LABEL_RATIO).sp,
            fontWeight = FontWeight.Bold,
        ),
    )
}

/**
 * Draws a note as [NoteCircleButton] shows it, [diameter] pixels across around [center].
 *
 * Components with many notes and no clicks, such as the Tonnetz, use this to draw every note
 * on one canvas instead of composing a button for each.
 */
internal fun DrawScope.drawNoteCircle(
    segments: List<Color>,
    label: TextLayoutResult?,
    center: Offset,
    diameter: Float,
    palette: NotePalette,
) {
    drawNoteRing(segments, center, diameter, palette)
    if (label != null) {
        drawText(
            textLayoutResult = label,
            topLeft = center - Offset(label.size.width / 2f, label.size.height / 2f),
        )
    }
}

private fun DrawScope.drawNoteRing(
    segments: List<Color>,
    center: Offset,
    diameter: Float,
    palette: NotePalette,
) {
    val outerRadius = diameter / 2f
    val innerRadius = diameter * CENTER_RATIO / 2f
    val bandWidth = outerRadius - innerRadius
    val bandRadius = innerRadius + bandWidth / 2f
    val outlineWidth = diameter * OUTLINE_RATIO

    val ringColors = segments.ifEmpty { listOf(palette.emptyRing) }
    val arcs = noteRingSegments(ringColors.size)

    ringColors.forEachIndexed { index, color ->
        val arc = arcs[index]
        drawArc(
            color = color,
            // Compose measures clockwise from 3 o'clock; the original measured
            // counterclockwise, hence the negated end angle.
            startAngle = -arc.endDegrees,
            sweepAngle = arc.sweepDegrees,
            useCenter = false,
            topLeft = Offset(center.x - bandRadius, center.y - bandRadius),
            size = Size(bandRadius * 2f, bandRadius * 2f),
            style = Stroke(width = bandWidth),
        )
    }

    // Radial dividers, so neighbouring groups stay legible where they meet.
    if (ringColors.size > 1) {
        arcs.forEach { arc ->
            val radians = Math.toRadians(arc.startDegrees.toDouble())
            val dx = cos(radians).toFloat()
            val dy = -sin(radians).toFloat()
            drawLine(
                color = palette.outline,
                start = Offset(center.x + dx * innerRadius, center.y + dy * innerRadius),
                end = Offset(center.x + dx * outerRadius, center.y + dy * outerRadius),
                strokeWidth = outlineWidth,
            )
        }
    }

    drawCircle(color = palette.noteCenter, radius = innerRadius, center = center)
    drawCircle(
        color = palette.emptyRing,
        radius = innerRadius,
        center = center,
        style = Stroke(width = outlineWidth / 2f),
    )
    drawCircle(
        color = palette.outline,
        radius = outerRadius - outlineWidth / 2f,
        center = center,
        style = Stroke(width = outlineWidth),
    )
}

internal data class NoteRingSegment(val startDegrees: Float, val endDegrees: Float) {
    val sweepDegrees: Float get() = endDegrees - startDegrees
}

internal fun noteRingSegments(count: Int): List<NoteRingSegment> {
    if (count <= 1) return listOf(NoteRingSegment(0f, FULL_TURN))
    val starts = START_ANGLES[count]
        ?: List(count) { FIRST_EVEN_ANGLE + FULL_TURN * it / count }

    return starts.mapIndexed { index, start ->
        val next = starts.getOrNull(index + 1) ?: (starts.first() + FULL_TURN)
        NoteRingSegment(start, next)
    }
}

private val START_ANGLES: Map<Int, List<Float>> = mapOf(
    2 to listOf(60f, 240f),
    3 to listOf(90f, 210f, 330f),
    4 to listOf(45f, 135f, 225f, 315f),
)

private const val FULL_TURN = 360f
private const val FIRST_EVEN_ANGLE = 90f
private const val CENTER_RATIO = 0.70f
private const val LABEL_RATIO = 0.5f
private const val OUTLINE_RATIO = 0.04f

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F)
@Composable
private fun NoteCircleButtonPreview() {
    val groups = NotePalette.Basic.groupColors

    NotoLyzardTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                emptyList(),
                groups.take(1),
                groups.take(2),
                groups.take(3),
                groups.take(4),
            ).forEach { colors ->
                NoteCircleButton(
                    state = NoteCircleButtonState(
                        pitchClass = PitchClass(NoteName.A_SHARP),
                        outerColors = colors,
                        textColor = null,
                    ),
                    onClick = {},
                    modifier = Modifier.size(56.dp),
                )
            }
        }
    }
}
