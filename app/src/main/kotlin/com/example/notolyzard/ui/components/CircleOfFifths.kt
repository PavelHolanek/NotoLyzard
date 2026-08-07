package com.example.notolyzard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val POSITIONS = 12
private const val FIFTH = 7
private const val RELATIVE_MINOR_OFFSET = 9

private const val BUTTON_RATIO = 1f / 11f * 1.2f
private const val OUTER_RING_RATIO = 0.88f
private const val INNER_RING_RATIO = 0.6f
private const val GUIDE_CIRCLE_RATIO = 0.74f
private const val GUIDE_CIRCLE_STROKE_RATIO = 0.02f

@Composable
fun CircleOfFifths(
    layers: List<NoteGroupLayer>,
    onNoteClick: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rings = remember(layers) { circleOfFifthsRings(layers) }

    val palette = LocalNotePalette.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        val diameter = minOf(maxWidth, maxHeight)
        val buttonSize = diameter * BUTTON_RATIO

        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = minOf(size.width, size.height) * GUIDE_CIRCLE_RATIO / 2f
            drawCircle(
                color = palette.outline,
                radius = radius,
                center = Offset(size.width / 2f, size.height / 2f),
                style = Stroke(width = radius * GUIDE_CIRCLE_STROKE_RATIO),
            )
        }

        Ring(rings.major, OUTER_RING_RATIO, diameter, buttonSize, onNoteClick)
        Ring(rings.minor, INNER_RING_RATIO, diameter, buttonSize, onNoteClick)
    }
}

@Composable
private fun BoxScope.Ring(
    buttons: List<NoteCircleButtonState>,
    ringRatio: Float,
    diameter: Dp,
    buttonSize: Dp,
    onNoteClick: (PitchClass) -> Unit,
) {
    val radius = diameter * ringRatio / 2f

    buttons.forEachIndexed { index, state ->
        val angle = 2.0 * PI * index / POSITIONS
        val x = radius * sin(angle).toFloat()
        val y = -radius * cos(angle).toFloat()

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = x, y = y)
                .size(buttonSize),
        ) {
            NoteCircleButton(
                state = state,
                onClick = { onNoteClick(state.pitchClass) },
                modifier = Modifier.size(buttonSize),
            )
        }
    }
}

internal data class CircleOfFifthsRings(
    val major: List<NoteCircleButtonState>,
    val minor: List<NoteCircleButtonState>,
)


internal fun circleOfFifthsRings(layers: List<NoteGroupLayer>): CircleOfFifthsRings {
    val majors = circleOfFifthsRoots()
    return CircleOfFifthsRings(
        major = majors.map { it.toButtonState(layers) },
        minor = majors.map { (it + RELATIVE_MINOR_OFFSET).toButtonState(layers) },
    )
}

internal fun circleOfFifthsRoots(): List<PitchClass> {
    var note = PitchClass(NoteName.C)
    return List(POSITIONS) {
        val current = note
        note += FIFTH
        current
    }
}

private fun PitchClass.toButtonState(layers: List<NoteGroupLayer>) = NoteCircleButtonState(
    pitchClass = this,
    outerColors = layers.colorsFor(this),
    textColor = null,
)

@Preview(showBackground = true, widthDp = 360, heightDp = 360)
@Composable
private fun CircleOfFifthsPreview() {
    val cMajor = Scale(
        StandardScales.patterns(listOf(ScaleType.Major)).single(),
        PitchClass(NoteName.C),
    )
    NotoLyzardTheme {
        CircleOfFifths(
            layers = listOf(NoteGroupLayer(cMajor, Color(0xFF3F51B5))),
            onNoteClick = {},
        )
    }
}
