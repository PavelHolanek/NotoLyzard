package com.example.notolyzard.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * Shared component: the twelve pitch classes laid out around the circle of fifths,
 * with an arbitrary subset highlighted.
 *
 * Deliberately generic — it knows nothing about scales or chords. Callers decide what
 * [highlighted] means: the notes of a scale on one screen, the notes of a chord on
 * another, the answers to a quiz question on a third.
 *
 * @param highlighted notes to emphasise, in no particular order
 * @param selected the single note the user picked, if any
 * @param onNoteClick invoked with the note the user tapped
 *
 * TODO: implement. Notes for whoever picks this up:
 *  - the ring order is fifths, not semitones: from any note, each step is +7 semitones,
 *    which `PitchClass.plus` already gives you
 *  - lay the twelve [NoteCircleButton]s out on a circle; a custom `Layout` positions them
 *    without measuring twice, but a `Box` with offsets is fine to start
 *  - keep the aspect ratio square so the ring does not turn into an ellipse
 */
@Composable
fun CircleOfFifths(
    highlighted: Set<PitchClass>,
    selected: PitchClass?,
    onNoteClick: (PitchClass) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Placeholder so the screen runs; replace wholesale.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "CircleOfFifths\nhighlighted: " +
                highlighted.joinToString { it.noteName.symbol }.ifEmpty { "none" },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun CircleOfFifthsPreview() {
    NotoLyzardTheme {
        CircleOfFifths(
            highlighted = setOf(PitchClass(NoteName.C), PitchClass(NoteName.G)),
            selected = PitchClass(NoteName.C),
            onNoteClick = {},
        )
    }
}
