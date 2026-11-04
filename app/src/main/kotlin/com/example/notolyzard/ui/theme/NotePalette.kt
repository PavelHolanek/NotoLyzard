package com.example.notolyzard.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class NotePalette(
    val groupColors: List<Color>,
    val outline: Color,
    val emptyRing: Color,
    val noteCenter: Color,
    val noteText: Color,
    val background: Color,
    val correct: Color,
    val incorrect: Color,
    val fretboard: Color,
    val whiteKey: Color,
    val blackKey: Color,
) {
    companion object {
        val Basic = NotePalette(
            groupColors = listOf(
                Color(0xFFD99B00),
                Color(0xFF0007D9),
                Color(0xFF007733),
                Color(0xFFB90089),
            ),
            outline = Color(0xFF000000),
            emptyRing = Color(0xFF4F4F4F),
            noteCenter = Color(0xFF909090),
            noteText = Color(0xFF000000),
            background = Color(0xFF4F4F4F),
            correct = Color(0xFF00A82D),
            incorrect = Color(0xFFD90007),
            fretboard = Color(0xFF9C7A5B),
            whiteKey = Color(0xFFE8E8E8),
            blackKey = Color(0xFF1E1E1E),
        )
    }
}

val LocalNotePalette = staticCompositionLocalOf { NotePalette.Basic }
