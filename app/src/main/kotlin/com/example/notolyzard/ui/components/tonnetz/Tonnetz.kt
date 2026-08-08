package com.example.notolyzard.ui.components.tonnetz

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.ui.components.NoteCircleButton
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.colorsFor
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.roundToInt

/**
 * The Tonnetz: notes on a triangular lattice, fifths across and thirds along the diagonals,
 * so that every triangle of neighbours spells a major or a minor triad.
 *
 * The lattice is unbounded and can be dragged like a map. Nothing is created or destroyed
 * while dragging — see [TonnetzCoord.pitchClass] for why that is not needed.
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
    val noteRadiusPx = with(density) { noteSize.toPx() } / 2f
    val edgeWidthPx = with(density) { EDGE_WIDTH.toPx() }
    val geometry = remember(cellPx) { TonnetzGeometry(cellPx) }

    BoxWithConstraints(
        modifier = modifier
            .clipToBounds()
            .pointerInput(state) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    state.panBy(dragAmount)
                }
            },
    ) {
        val viewport = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val cells = remember(geometry, viewport) { geometry.window(viewport) }

        // Whole cells of pan are folded into the note the lattice starts on, so the buttons
        // themselves never move and the set of them never changes. Crossing a cell boundary
        // costs one recomposition of the labels and colours, the same work a lazy grid does
        // per row scrolled.
        val anchor by remember(geometry, state) { derivedStateOf { geometry.anchor(state.pan) } }
        val anchorNote = anchor.pitchClass(origin)

        Box(
            modifier = Modifier
                .fillMaxSize()
                // Read inside the lambda on purpose: the sub-cell remainder changes every
                // frame of a drag, and this keeps that in the draw phase rather than
                // recomposing or re-laying out a hundred buttons sixty times a second.
                .graphicsLayer {
                    val residual = geometry.residual(state.pan)
                    translationX = residual.x
                    translationY = residual.y
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLattice(
                    cells = cells,
                    geometry = geometry,
                    color = palette.outline,
                    strokeWidth = edgeWidthPx,
                )
            }

            cells.forEach { cell ->
                val pitchClass = cell.pitchClass(anchorNote)
                NoteCircleButton(
                    state = NoteCircleButtonState(
                        pitchClass = pitchClass,
                        outerColors = layers.colorsFor(pitchClass),
                        textColor = null,
                    ),
                    modifier = Modifier
                        .offset {
                            val center = geometry.screenOffset(cell)
                            IntOffset(
                                x = (center.x - noteRadiusPx).roundToInt(),
                                y = (center.y - noteRadiusPx).roundToInt(),
                            )
                        }
                        .size(noteSize),
                )
            }
        }
    }
}

private fun DrawScope.drawLattice(
    cells: List<TonnetzCoord>,
    geometry: TonnetzGeometry,
    color: Color,
    strokeWidth: Float,
) {
    cells.forEach { cell ->
        val start = geometry.screenOffset(cell)
        EDGE_DIRECTIONS.forEach { direction ->
            drawLine(
                color = color,
                start = start,
                end = geometry.screenOffset(cell + direction),
                strokeWidth = strokeWidth,
            )
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
