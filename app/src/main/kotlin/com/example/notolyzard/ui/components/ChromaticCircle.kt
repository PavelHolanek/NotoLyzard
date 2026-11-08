package com.example.notolyzard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import com.example.notolyzard.core.theory.ChordType
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.Pitch
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.StandardChords
import com.example.notolyzard.ui.theme.NotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val POSITIONS = 12

private const val NOTE_RATIO = 0.16f
private const val LINE_WIDTH_RATIO = 0.01f

/**
 * The twelve notes round a circle in chromatic order, each note group joined into a polygon
 * of its own colour.
 *
 * The notes go clockwise a semitone at a time, with [top] at twelve o'clock. A polygon
 * visits its group's notes in that same order, so its shape shows the group's intervals:
 * transposing a group only turns its polygon.
 *
 * Where groups share a side, each still draws its own line and they run side by side, so a
 * side shared by N groups is N lines thick. Sides that cross are simply drawn over each
 * other. No circle is drawn, only the notes and the polygons.
 *
 * Clicking a note hands it to [onNoteClick]; without one, the notes are not clickable.
 */
@Composable
fun ChromaticCircle(
    layers: List<NoteGroupLayer>,
    modifier: Modifier = Modifier,
    onNoteClick: ((PitchClass) -> Unit)? = null,
    top: PitchClass = PitchClass(NoteName.C),
) {
    val notes = remember(layers, top) {
        List(POSITIONS) { position ->
            val pitchClass = top + position
            NoteCircleButtonState(
                pitchClass = pitchClass,
                outerColors = layers.colorsFor(pitchClass),
                textColor = null,
            )
        }
    }
    val lines = remember(layers, top) { polygonLines(layers, top) }

    // No fillMaxWidth: on its own, aspectRatio settles on whichever of the two constraints
    // it can satisfy, so the circle fits a wide box as happily as a tall one.
    BoxWithConstraints(modifier = modifier.aspectRatio(1f)) {
        val diameter = minOf(maxWidth, maxHeight)
        val noteSize = diameter * NOTE_RATIO
        // The notes reach right to the edge of the square.
        val ringRadius = (diameter - noteSize) / 2f

        Canvas(modifier = Modifier.matchParentSize()) {
            drawPolygonLines(
                lines = lines,
                ringRadius = ringRadius.toPx(),
                lineWidth = diameter.toPx() * LINE_WIDTH_RATIO,
            )
        }

        notes.forEachIndexed { position, state ->
            val point = circlePoint(position)
            NoteCircleButton(
                state = state,
                onClick = onNoteClick?.let { click -> { state.pitchClass?.let(click) } },
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = ringRadius * point.x, y = ringRadius * point.y)
                    .size(noteSize),
            )
        }
    }
}

/**
 * One line of a polygon side, from note [from] to note [to] (positions round the circle,
 * 0 at the top, [from] the lower), drawn [shift] line widths to one side of the straight
 * line between them.
 */
internal data class PolygonLine(
    val from: Int,
    val to: Int,
    val color: Color,
    val shift: Float,
)

/**
 * Every line to draw for [layers]. A side that only one group has gets a single line down
 * its middle; a side shared by several groups gets one line per group, in layer order,
 * spread evenly either side of the middle.
 */
internal fun polygonLines(layers: List<NoteGroupLayer>, top: PitchClass): List<PolygonLine> {
    val colorsBySide = linkedMapOf<Pair<Int, Int>, MutableList<Color>>()
    layers.forEach { layer ->
        val positions = layer.noteGroup?.notes.orEmpty().map { it - top }
        polygonSides(positions).forEach { side ->
            colorsBySide.getOrPut(side) { mutableListOf() } += layer.color
        }
    }
    return colorsBySide.flatMap { (side, colors) ->
        colors.mapIndexed { index, color ->
            PolygonLine(
                from = side.first,
                to = side.second,
                color = color,
                shift = index - (colors.size - 1) / 2f,
            )
        }
    }
}

/**
 * The sides of the polygon through [positions], each as a (lower, higher) pair so that the
 * same side found by two groups compares equal. Two notes make a single side rather than
 * two coinciding ones; one note or none make no sides at all.
 */
internal fun polygonSides(positions: List<Int>): List<Pair<Int, Int>> {
    val corners = positions.distinct().sorted()
    return when (corners.size) {
        0, 1 -> emptyList()
        2 -> listOf(corners[0] to corners[1])
        else -> corners.zipWithNext() + (corners.first() to corners.last())
    }
}

/** Where [position] sits on a circle of radius 1 round the origin, 0 at the top, clockwise. */
private fun circlePoint(position: Int): Offset {
    val angle = 2.0 * PI * position / POSITIONS
    return Offset(sin(angle).toFloat(), -cos(angle).toFloat())
}

private fun DrawScope.drawPolygonLines(
    lines: List<PolygonLine>,
    ringRadius: Float,
    lineWidth: Float,
) {
    lines.forEach { line ->
        val start = center + circlePoint(line.from) * ringRadius
        val end = center + circlePoint(line.to) * ringRadius
        val along = (end - start) / (end - start).getDistance()
        // The same side always runs from the lower position to the higher, so every group
        // that shares it agrees on which way "sideways" is.
        val sideways = Offset(-along.y, along.x) * (line.shift * lineWidth)
        drawLine(
            color = line.color,
            start = start + sideways,
            end = end + sideways,
            strokeWidth = lineWidth,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F, widthDp = 360, heightDp = 360)
@Composable
private fun ChromaticCirclePreview() {
    fun triad(type: ChordType, root: NoteName) =
        StandardChords.chords(listOf(type), Pitch(4, root)).single()

    val colors = NotePalette.Basic.groupColors
    NotoLyzardTheme {
        ChromaticCircle(
            // C major shares C–E with A minor and E–G with E minor.
            layers = listOf(
                NoteGroupLayer(triad(ChordType.MajorTriad, NoteName.C), colors[0]),
                NoteGroupLayer(triad(ChordType.MinorTriad, NoteName.A), colors[1]),
                NoteGroupLayer(triad(ChordType.MinorTriad, NoteName.E), colors[2]),
            ),
        )
    }
}
