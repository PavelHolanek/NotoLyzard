package com.example.notolyzard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Colours used to tell selected note groups apart — the equivalent of the old
 * `ColorPalette.GetGroupColors()`.
 *
 * Read from the theme rather than stored in a ViewModel, so the same selection renders
 * correctly in light and dark mode without the ViewModel knowing anything about it.
 */
@Composable
@ReadOnlyComposable
fun groupColors(): List<Color> = listOf(
    MaterialTheme.colorScheme.primary,
    MaterialTheme.colorScheme.tertiary,
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.error,
)
