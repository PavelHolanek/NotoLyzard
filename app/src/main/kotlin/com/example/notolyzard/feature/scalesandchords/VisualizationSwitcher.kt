package com.example.notolyzard.feature.scalesandchords

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notolyzard.R
import com.example.notolyzard.ui.components.ARROW_ASPECT_RATIO
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * Names the visualization on screen and steps to the one on either side of it.
 *
 * Knows nothing about which visualizations exist beyond [Visualization] itself, so a new
 * entry there shows up here for free.
 */
@Composable
fun VisualizationSwitcher(
    visualization: Visualization,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArrowButton(
            rotationDegrees = POINTING_LEFT,
            description = "Previous visualization",
            onClick = onPrevious,
        )

        Text(
            text = visualization.label,
            color = LocalNotePalette.current.noteText,
            style = MaterialTheme.typography.titleMedium,
        )

        ArrowButton(
            rotationDegrees = POINTING_RIGHT,
            description = "Next visualization",
            onClick = onNext,
        )
    }
}

@Composable
private fun ArrowButton(
    rotationDegrees: Float,
    description: String,
    onClick: () -> Unit,
) {
    Box(
        // Square, because a quarter turn swaps the drawable's width and height and
        // Modifier.rotate turns the drawing without touching the layout.
        modifier = Modifier
            .size(ARROW_SIZE)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_up),
            contentDescription = null,
            tint = LocalNotePalette.current.outline,
            modifier = Modifier
                .height(ARROW_SIZE)
                .aspectRatio(ARROW_ASPECT_RATIO)
                .rotate(rotationDegrees),
        )
    }
}

/** Modifier.rotate turns clockwise, so the up arrow lands on its right side at +90°. */
private const val POINTING_RIGHT = 90f
private const val POINTING_LEFT = -90f

private val ARROW_SIZE = 36.dp

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F, widthDp = 360)
@Composable
private fun VisualizationSwitcherPreview() {
    NotoLyzardTheme {
        VisualizationSwitcher(
            visualization = Visualization.Tonnetz,
            onPrevious = {},
            onNext = {},
        )
    }
}
