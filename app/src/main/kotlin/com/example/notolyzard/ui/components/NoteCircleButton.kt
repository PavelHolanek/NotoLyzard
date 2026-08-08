package com.example.notolyzard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalNotePalette.current
    val label = state.label

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics { if (label.isNotEmpty()) contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawNoteRing(
                segments = state.segments,
                palette = palette,
            )
        }

        if (label.isNotEmpty()) {
            NoteLabel(
                text = label,
                color = state.textColor ?: palette.noteText,
            )
        }
    }
}

@Composable
private fun NoteLabel(text: String, color: Color) {
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = color,
            style = TextStyle(
                fontSize = (minOf(maxWidth, maxHeight).value * LABEL_RATIO).sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

private fun DrawScope.drawNoteRing(segments: List<Color>, palette: NotePalette) {
    val diameter = minOf(size.width, size.height)
    val center = Offset(size.width / 2f, size.height / 2f)

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
