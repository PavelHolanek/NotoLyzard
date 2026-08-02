package com.example.notolyzard.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.Scale
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.core.theory.StandardScales
import com.example.notolyzard.ui.theme.NotoLyzardTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The circle of fifths: twelve major keys on an outer ring, their relative minors on an
 * inner ring, each note coloured by the selected groups that contain it.
 *
 * Takes [layers] — plain data — rather than a ViewModel, so a keyboard or a fretboard can
 * be dropped in beside it against the same input, and so a visualization switcher is a
 * `when` rather than three ViewModel hierarchies.
 */
@Composable
fun CircleOfFifths(
    layers: List<NoteGroupLayer>,
    onNoteClick: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rings = remember(layers) { circleOfFifthsRings(layers) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
    ) {
        val diameter = minOf(maxWidth, maxHeight)
        val buttonSize = diameter * BUTTON_RATIO

        Ring(rings.major, OUTER_RING_RATIO, diameter, buttonSize, onNoteClick)
        Ring(rings.minor, INNER_RING_RATIO, diameter, buttonSize, onNoteClick)
    }
}

/** Places twelve buttons evenly around one ring, position 0 at the top, going clockwise. */
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

/** The two rings of the circle, outer first. */
internal data class CircleOfFifthsRings(
    val major: List<NoteCircleButtonState>,
    val minor: List<NoteCircleButtonState>,
)

/**
 * Builds both rings for the given [layers].
 *
 * A pure function, so the whole mapping from "these groups are selected" to "these
 * buttons show these colours" is unit testable without Compose or an emulator.
 *
 * The outer ring walks up in fifths from C, which is what puts C at the top and one
 * accidental between neighbours. The inner ring holds each key's relative minor, three
 * semitones below its major — A minor under C major.
 *
 * This differs from the old `CircleView`, which placed the same note on both rings and
 * shifted the outer one by three positions. That looked unfinished rather than intended.
 */
internal fun circleOfFifthsRings(layers: List<NoteGroupLayer>): CircleOfFifthsRings {
    val majors = circleOfFifthsRoots()
    return CircleOfFifthsRings(
        major = majors.map { it.toButtonState(layers) },
        minor = majors.map { (it + RELATIVE_MINOR_OFFSET).toButtonState(layers) },
    )
}

/** The twelve major keys in fifths order, starting at C. */
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

private const val POSITIONS = 12

/** A perfect fifth, in semitones — one step clockwise around the circle. */
private const val FIFTH = 7

/** A minor third down, reached by going up nine semitones within the octave. */
private const val RELATIVE_MINOR_OFFSET = 9

private const val OUTER_RING_RATIO = 0.9f
private const val INNER_RING_RATIO = 0.6f
private const val BUTTON_RATIO = 0.16f

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
