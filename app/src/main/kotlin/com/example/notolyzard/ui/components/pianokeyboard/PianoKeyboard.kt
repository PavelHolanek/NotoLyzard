package com.example.notolyzard.ui.components.pianokeyboard

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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.notolyzard.ui.theme.NotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.roundToInt

/**
 * A piano keyboard with a note on every key, coloured by [layers].
 *
 * See [PianoKeyboardGeometry] for the layout. Five octaves are far wider than a phone, so
 * the keyboard scrolls sideways, the same way the fretboard does. The notes are not
 * clickable, so a swipe that starts on one still scrolls.
 */
@Composable
fun PianoKeyboard(
    layers: List<NoteGroupLayer>,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    octaveCount: Int = DEFAULT_OCTAVE_COUNT,
    whiteKeyWidth: Dp = DEFAULT_WHITE_KEY_WIDTH,
) {
    val palette = LocalNotePalette.current
    val density = LocalDensity.current
    val geometry = remember(density, octaveCount, whiteKeyWidth) {
        PianoKeyboardGeometry(
            octaveCount = octaveCount,
            whiteKeyWidthPx = with(density) { whiteKeyWidth.toPx() },
        )
    }
    val notes = remember(geometry, layers) { geometry.keys.map { it.toButtonState(layers) } }

    Box(
        modifier = modifier
            .horizontalScroll(scrollState)
            .size(
                width = with(density) { geometry.width.toDp() },
                height = with(density) { geometry.height.toDp() },
            ),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawKeys(geometry, palette)
        }

        geometry.keys.forEachIndexed { index, key ->
            NoteCircleButton(
                state = notes[index],
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (key.noteCenter.x - key.noteSize / 2f).roundToInt(),
                            y = (key.noteCenter.y - key.noteSize / 2f).roundToInt(),
                        )
                    }
                    .size(with(density) { key.noteSize.toDp() }),
            )
        }
    }
}

/** The note on [PianoKey], counting up from the C every keyboard here starts on. */
internal val PianoKey.pitchClass: PitchClass get() = PitchClass(NoteName.C) + index

private fun PianoKey.toButtonState(layers: List<NoteGroupLayer>) = NoteCircleButtonState(
    pitchClass = pitchClass,
    outerColors = layers.colorsFor(pitchClass),
    textColor = null,
)

private fun DrawScope.drawKeys(geometry: PianoKeyboardGeometry, palette: NotePalette) {
    val outlineWidth = KEY_OUTLINE_WIDTH.toPx()
    val (blackKeys, whiteKeys) = geometry.keys.partition { it.isBlack }

    // The white keys are one surface split by lines, which keeps every line the same width
    // instead of doubling up where two keys' own outlines would meet.
    drawRect(color = palette.whiteKey)
    whiteKeys.drop(1).forEach { key ->
        drawLine(
            color = palette.outline,
            start = Offset(key.left, 0f),
            end = Offset(key.left, size.height),
            strokeWidth = outlineWidth,
        )
    }
    // Inset by half the stroke, or the edges of the canvas would cut it in half.
    drawRect(
        color = palette.outline,
        topLeft = Offset(outlineWidth / 2f, outlineWidth / 2f),
        size = Size(size.width - outlineWidth, size.height - outlineWidth),
        style = Stroke(width = outlineWidth),
    )

    blackKeys.forEach { key ->
        drawRect(
            color = palette.blackKey,
            topLeft = Offset(key.left, 0f),
            size = Size(key.width, key.height),
        )
    }
}

private const val DEFAULT_OCTAVE_COUNT = 5

private val DEFAULT_WHITE_KEY_WIDTH = 52.dp

private val KEY_OUTLINE_WIDTH = 2.dp

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F, widthDp = 360, heightDp = 300)
@Composable
private fun PianoKeyboardPreview() {
    val cMajor = Scale(
        StandardScales.patterns(listOf(ScaleType.Major)).single(),
        PitchClass(NoteName.C),
    )
    NotoLyzardTheme {
        PianoKeyboard(
            layers = listOf(NoteGroupLayer(cMajor, Color(0xFF3F51B5))),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
