package com.example.notolyzard.ui.components.fretboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.core.theory.Tuning
import com.example.notolyzard.ui.components.NoteCircleButton
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.components.NoteGroupLayer
import com.example.notolyzard.ui.components.colorsFor
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.roundToInt

/**
 * A guitar fretboard with a note on every string at every fret, coloured by [layers].
 *
 * See [FretboardGeometry] for the layout. The fretboard is far wider than a phone, so it
 * scrolls sideways. Unlike the Tonnetz it has two ends, which is exactly what the stock
 * [horizontalScroll] handles, fling and overscroll included. The notes are not clickable,
 * so a swipe that starts on one still scrolls.
 *
 * The fret markers sit in a strip below the board rather than on it, where they would
 * compete with the notes — much like the side dots on the edge of a real neck.
 */
@Composable
fun Fretboard(
    layers: List<NoteGroupLayer>,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    tuning: Tuning = Tuning.Standard,
    fretCount: Int = DEFAULT_FRET_COUNT,
    noteSize: Dp = DEFAULT_NOTE_SIZE,
    stringSpacing: Dp = DEFAULT_STRING_SPACING,
    fretSpacing: Dp = DEFAULT_FRET_SPACING,
) {
    val palette = LocalNotePalette.current
    val density = LocalDensity.current
    val geometry = remember(density, tuning.stringCount, fretCount, stringSpacing, fretSpacing) {
        with(density) {
            FretboardGeometry(
                stringCount = tuning.stringCount,
                fretCount = fretCount,
                fretSpacingPx = fretSpacing.toPx(),
                stringSpacingPx = stringSpacing.toPx(),
                markerRowHeightPx = MARKER_ROW_HEIGHT.toPx(),
            )
        }
    }
    val notes = remember(tuning, fretCount, layers) { fretboardNotes(tuning, fretCount, layers) }
    val noteRadiusPx = with(density) { noteSize.toPx() } / 2f

    Box(
        modifier = modifier
            .horizontalScroll(scrollState)
            .size(
                width = with(density) { geometry.width.toDp() },
                height = with(density) { geometry.height.toDp() },
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawFretboard(geometry, palette)
        }

        notes.forEach { note ->
            NoteCircleButton(
                state = note.state,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (geometry.noteX(note.fret) - noteRadiusPx).roundToInt(),
                            y = (geometry.stringY(note.string) - noteRadiusPx).roundToInt(),
                        )
                    }
                    .size(noteSize),
            )
        }
    }
}

internal data class FretboardNote(
    val string: Int,
    val fret: Int,
    val state: NoteCircleButtonState,
)

/** Every note from the open strings up to [fretCount], lowest string first. */
internal fun fretboardNotes(
    tuning: Tuning,
    fretCount: Int,
    layers: List<NoteGroupLayer>,
): List<FretboardNote> = (0 until tuning.stringCount).flatMap { string ->
    (0..fretCount).map { fret ->
        val pitchClass = tuning.pitchClassAt(string, fret)
        FretboardNote(
            string = string,
            fret = fret,
            state = NoteCircleButtonState(
                pitchClass = pitchClass,
                outerColors = layers.colorsFor(pitchClass),
                textColor = null,
            ),
        )
    }
}

private fun DrawScope.drawFretboard(geometry: FretboardGeometry, palette: NotePalette) {
    drawRect(
        color = palette.fretboard,
        topLeft = Offset(geometry.nutX, 0f),
        size = Size(geometry.boardEndX - geometry.nutX, geometry.boardHeight),
    )

    (0..geometry.fretCount).forEach { fret ->
        val x = geometry.fretWireX(fret)
        drawLine(
            color = palette.outline,
            start = Offset(x, 0f),
            end = Offset(x, geometry.boardHeight),
            strokeWidth = (if (fret == 0) NUT_WIDTH else FRET_WIRE_WIDTH).toPx(),
        )
    }

    // From the very left edge rather than the nut, so the open-string notes sit on a string
    // too, as if it carried on towards the headstock.
    (0 until geometry.stringCount).forEach { string ->
        val y = geometry.stringY(string)
        drawLine(
            color = palette.outline,
            start = Offset(0f, y),
            end = Offset(geometry.boardEndX, y),
            strokeWidth = stringWidth(string, geometry.stringCount).toPx(),
        )
    }

    (1..geometry.fretCount).forEach { fret ->
        drawMarker(
            dots = fretMarkerDots(fret),
            center = Offset(geometry.noteX(fret), geometry.markerY),
            color = palette.fretboard,
        )
    }
}

/** [dots] side by side along the neck, centred on [center]. */
private fun DrawScope.drawMarker(dots: Int, center: Offset, color: Color) {
    val spacing = MARKER_DOT_SPACING.toPx()
    repeat(dots) { index ->
        drawCircle(
            color = color,
            radius = MARKER_RADIUS.toPx(),
            center = center + Offset(spacing * (index - (dots - 1) / 2f), 0f),
        )
    }
}

/** Thicker towards the bass, as real strings are. */
private fun stringWidth(string: Int, stringCount: Int): Dp =
    if (stringCount == 1) LOWEST_STRING_WIDTH
    else lerp(LOWEST_STRING_WIDTH, HIGHEST_STRING_WIDTH, string / (stringCount - 1f))

private const val DEFAULT_FRET_COUNT = 24

private val DEFAULT_NOTE_SIZE = 40.dp

/** Has to stay above [DEFAULT_NOTE_SIZE], or notes on neighbouring strings would overlap. */
private val DEFAULT_STRING_SPACING = 48.dp

/** Has to stay above [DEFAULT_NOTE_SIZE], or notes on neighbouring frets would overlap. */
private val DEFAULT_FRET_SPACING = 64.dp

private val NUT_WIDTH = 6.dp
private val FRET_WIRE_WIDTH = 2.dp
private val LOWEST_STRING_WIDTH = 3.dp
private val HIGHEST_STRING_WIDTH = 1.dp

private val MARKER_ROW_HEIGHT = 20.dp
private val MARKER_RADIUS = 4.dp
private val MARKER_DOT_SPACING = 12.dp

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F, widthDp = 360, heightDp = 360)
@Composable
private fun FretboardPreview() {
    val cMajor = Scale(
        StandardScales.patterns(listOf(ScaleType.Major)).single(),
        PitchClass(NoteName.C),
    )
    NotoLyzardTheme {
        Fretboard(
            layers = listOf(NoteGroupLayer(cMajor, Color(0xFF3F51B5))),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
