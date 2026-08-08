package com.example.notolyzard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notolyzard.R
import com.example.notolyzard.ui.theme.LocalNotePalette
import com.example.notolyzard.ui.theme.NotoLyzardTheme

enum class ArrowDirection { Up, Down }

/**
 * What one interval arrow shows.
 *
 * @param label the interval written inside the arrow. Empty draws no text.
 * @param isActive whether the arrow is switched on. An arrow that is off is drawn in grey.
 */
data class IntervalArrowButtonState(
    val label: String = "",
    val isActive: Boolean = true,
)

/**
 * A block arrow with an interval written in it, which switches on and off when tapped.
 *
 * On and off are the palette's outline and note-centre colours, which are the same black
 * and grey the original project baked into two separate images.
 *
 * The state only says whether it is on; flipping it is the caller's job, so the same button
 * can mean different things on different screens.
 */
@Composable
fun IntervalArrowButton(
    state: IntervalArrowButtonState,
    direction: ArrowDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalNotePalette.current
    val color = if (state.isActive) palette.outline else palette.noteCenter
    val description = when (direction) {
        ArrowDirection.Up -> "Interval up"
        ArrowDirection.Down -> "Interval down"
    }

    Box(
        modifier = modifier
            // Height drives the size; width follows the drawable's own proportions.
            .aspectRatio(ARROW_ASPECT_RATIO, matchHeightConstraintsFirst = true)
            .clickable(onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_up),
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .fillMaxSize()
                // One drawable covers both directions: the down arrow is the up arrow
                // mirrored, which is exactly how the original pair of SVGs related. The
                // flip is on the icon alone so the label stays the right way up.
                .graphicsLayer { scaleY = if (direction == ArrowDirection.Up) 1f else -1f },
        )

        if (state.label.isNotEmpty()) {
            ArrowLabel(text = state.label, color = color)
        }
    }
}

@Composable
private fun ArrowLabel(text: String, color: Color) {
    BoxWithConstraints(contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = color,
            style = TextStyle(
                fontSize = (maxWidth.value * LABEL_RATIO).sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

/** Width over height of ic_arrow_up's viewport, so the drawing never stretches. */
private const val ARROW_ASPECT_RATIO = 80.424431f / 110.09462f

/** Kept below the shaft's inner width, so the interval never touches the outline. */
private const val LABEL_RATIO = 0.30f

@Preview(showBackground = true, backgroundColor = 0xFF4F4F4F)
@Composable
private fun IntervalArrowButtonPreview() {
    NotoLyzardTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf(
                IntervalArrowButtonState(label = "9", isActive = true) to ArrowDirection.Up,
                IntervalArrowButtonState(label = "3", isActive = false) to ArrowDirection.Up,
                IntervalArrowButtonState(label = "", isActive = true) to ArrowDirection.Down,
                IntervalArrowButtonState(label = "", isActive = false) to ArrowDirection.Down,
            ).forEach { (state, direction) ->
                IntervalArrowButton(
                    state = state,
                    direction = direction,
                    onClick = {},
                    modifier = Modifier.height(88.dp),
                )
            }
        }
    }
}
