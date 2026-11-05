package com.example.notolyzard.ui.components.tonnetz

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.colorsFor
import com.example.notolyzard.ui.components.drawNoteCircle
import com.example.notolyzard.ui.components.measureNoteLabel
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * The Tonnetz: notes on a triangular lattice, fifths across and thirds along the diagonals,
 * so that every triangle of neighbours spells a major or a minor triad.
 *
 * The lattice is unbounded and can be dragged like a map. Nothing is created or destroyed
 * while dragging — see [TonnetzCoord.pitchClass] for why that is not needed. The notes are
 * not clickable, so they are all drawn on one canvas with [drawNoteCircle] rather than
 * composed as buttons, which keeps dragging out of composition and layout entirely.
 */
@Composable
fun Tonnetz(
    layers: List<NoteGroupLayer>,
    modifier: Modifier = Modifier,
    state: TonnetzState = rememberTonnetzState(),
    noteSize: Dp = DEFAULT_NOTE_SIZE,
    cellSpacing: Dp = DEFAULT_CELL_SPACING,
    origin: PitchClass = PitchClass(NoteName.C),
) {
    val palette = LocalNotePalette.current
    val density = LocalDensity.current
    val cellPx = with(density) { cellSpacing.toPx() }
    val noteDiameterPx = with(density) { noteSize.toPx() }
    val edgeWidthPx = with(density) { EDGE_WIDTH.toPx() }
    val geometry = remember(cellPx) { TonnetzGeometry(cellPx) }
    val textMeasurer = rememberTextMeasurer()

    // However many nodes are on screen, there are only twelve notes. Each is worked out once,
    // indexed by semitone, and every node looks its note up.
    val notes = remember(layers) {
        NoteName.entries.map { name ->
            val pitchClass = PitchClass(name)
            NoteCircleButtonState(pitchClass, layers.colorsFor(pitchClass), textColor = null)
        }
    }

    // Whole cells of pan are folded into the note the lattice starts on, so the nodes never
    // move on the canvas. Crossing a cell boundary only relabels them, in a single redraw.
    val anchor by remember(geometry, state) { derivedStateOf { geometry.anchor(state.pan) } }

    Spacer(
        modifier = modifier
            // The lattice has no natural size, so it takes all the room it is given.
            .fillMaxSize()
            .clipToBounds()
            .pointerInput(state) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    state.panBy(dragAmount)
                }
            }
            // The drawn notes have no semantics of their own.
            .semantics { contentDescription = "Tonnetz" }
            // Read inside the lambda on purpose: the sub-cell remainder changes every frame
            // of a drag, and reading it here only moves the drawn layer, without redrawing it.
            .graphicsLayer {
                val residual = geometry.residual(state.pan)
                translationX = residual.x
                translationY = residual.y
            }
            .drawWithCache {
                // Rebuilt when the size or the inputs change, never during a drag.
                val cells = geometry.window(size)
                val edges = latticeEdges(cells, geometry)
                val labels = notes.map { measureNoteLabel(textMeasurer, it, noteDiameterPx, palette) }

                onDrawBehind {
                    drawPoints(edges, PointMode.Lines, palette.outline, edgeWidthPx)

                    val anchorNote = anchor.pitchClass(origin)
                    cells.forEach { cell ->
                        val semitone = cell.pitchClass(anchorNote).semitone
                        drawNoteCircle(
                            segments = notes[semitone].segments,
                            label = labels[semitone],
                            center = geometry.screenOffset(cell),
                            diameter = noteDiameterPx,
                            palette = palette,
                        )
                    }
                }
            },
    )
}

/**
 * Every edge leaving [cells] in one of [EDGE_DIRECTIONS], as the start and end pairs that
 * [PointMode.Lines] expects.
 */
private fun latticeEdges(cells: List<TonnetzCoord>, geometry: TonnetzGeometry): List<Offset> =
    buildList {
        cells.forEach { cell ->
            val start = geometry.screenOffset(cell)
            EDGE_DIRECTIONS.forEach { direction ->
                add(start)
                add(geometry.screenOffset(cell + direction))
            }
        }
    }

/**
 * Three of the six neighbours. Drawing all six from every node would draw every edge twice.
 */
private val EDGE_DIRECTIONS = listOf(
    TonnetzCoord(col = 1, row = 0), // perfect fifth, to the right
    TonnetzCoord(col = 0, row = 1), // minor third, down and to the right
    TonnetzCoord(col = 1, row = -1), // major third, up and to the right
)

private val DEFAULT_NOTE_SIZE = 56.dp

/** Has to stay above [DEFAULT_NOTE_SIZE], or neighbouring notes would overlap. */
private val DEFAULT_CELL_SPACING = 76.dp

private val EDGE_WIDTH = 2.dp

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F, widthDp = 360, heightDp = 480)
@Composable
private fun TonnetzPreview() {
    val cMajor = Scale(
        StandardScales.patterns(listOf(ScaleType.Major)).single(),
        PitchClass(NoteName.C),
    )
    NotoLyzardTheme {
        Tonnetz(
            layers = listOf(NoteGroupLayer(cMajor, Color(0xFF3F51B5))),
            modifier = Modifier.fillMaxSize(),
        )
    }
}
