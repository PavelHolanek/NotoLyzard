package com.example.notolyzard.feature.scaleviewer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notolyzard.core.theory.ScaleType
import com.example.notolyzard.ui.theme.NotoLyzardTheme

/**
 * Lets the user choose which scale to view.
 *
 * Lives in the Scale Viewer package rather than in `ui/components`, because it is only
 * used by this screen. If a second screen ever needs it, move the file to
 * `ui/components` — that is the whole cost of the move.
 *
 * Stateless: it renders [selectedType] and reports taps through [onTypeSelected]. It does
 * not remember a selection of its own, so the ViewModel stays the only source of truth.
 *
 * TODO: implement. Something worth deciding first: with ten scale types a dropdown, a
 * horizontally scrolling row of filter chips, and a grid all work — the choice depends on
 * whether the user is browsing or looking for one specific scale.
 */
@Composable
fun ScalesPicker(
    scaleTypes: List<ScaleType>,
    selectedType: ScaleType,
    onTypeSelected: (ScaleType) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Placeholder so the screen runs; replace wholesale.
    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "ScalesPicker — selected: $selectedType (${scaleTypes.size} available)",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun ScalesPickerPreview() {
    NotoLyzardTheme {
        ScalesPicker(
            scaleTypes = listOf(ScaleType.Major, ScaleType.Minor, ScaleType.Dorian),
            selectedType = ScaleType.Major,
            onTypeSelected = {},
        )
    }
}
