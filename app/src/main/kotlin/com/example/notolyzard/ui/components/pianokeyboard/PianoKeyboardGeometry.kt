package com.example.notolyzard.ui.components.pianokeyboard

import androidx.compose.ui.geometry.Offset

/**
 * One key of the keyboard, in pixels from the top-left corner of the whole keyboard.
 *
 * [index] counts semitones up from the lowest key, which is always a C.
 */
internal data class PianoKey(
    val index: Int,
    val isBlack: Boolean,
    val left: Float,
    val width: Float,
    val height: Float,
) {
    val right: Float get() = left + width

    /**
     * Where the note is drawn: in the square at the front end of the key, the part of a
     * white key that no black key covers.
     */
    val noteCenter: Offset get() = Offset(left + width / 2f, height - width / 2f)

    val noteSize: Float get() = width * NOTE_SIZE_RATIO
}

/**
 * Lays out [octaveCount] octaves of piano keys, C to C, plus the closing C on top, the way
 * a 61-key keyboard spans five octaves.
 *
 * The proportions follow a real piano: a black key is 0.58 of a white key wide and about
 * two thirds as long. The keys themselves are shorter than real ones, so the keyboard does
 * not take up the whole screen.
 */
internal class PianoKeyboardGeometry(
    val octaveCount: Int,
    private val whiteKeyWidthPx: Float,
) {

    private val blackKeyWidthPx: Float = whiteKeyWidthPx * BLACK_KEY_WIDTH_RATIO

    val height: Float = whiteKeyWidthPx * KEY_LENGTH_RATIO
    val blackKeyHeight: Float = height * BLACK_KEY_LENGTH_RATIO

    val whiteKeyCount: Int = octaveCount * WHITE_SEMITONES.size + 1
    val width: Float = whiteKeyWidthPx * whiteKeyCount

    val keys: List<PianoKey> = List(octaveCount * SEMITONES_PER_OCTAVE + 1, ::key)

    private fun key(index: Int): PianoKey {
        val octave = index / SEMITONES_PER_OCTAVE
        val semitone = index % SEMITONES_PER_OCTAVE
        val octaveLeft = whiteKeyWidthPx * WHITE_SEMITONES.size * octave
        val whiteIndex = WHITE_SEMITONES.indexOf(semitone)

        return if (whiteIndex >= 0) {
            PianoKey(
                index = index,
                isBlack = false,
                left = octaveLeft + whiteKeyWidthPx * whiteIndex,
                width = whiteKeyWidthPx,
                height = height,
            )
        } else {
            PianoKey(
                index = index,
                isBlack = true,
                left = octaveLeft + blackKeyLeft(semitone),
                width = blackKeyWidthPx,
                height = blackKeyHeight,
            )
        }
    }

    /**
     * Left edge of the black key [semitone] semitones above C, measured from that C.
     *
     * Black keys do not sit centred over the cracks between white keys. Within each group,
     * C–E and F–B, they are spread so that the white keys show equally wide between them.
     * That is what makes C# lean left, D# lean right, F# lean left, A# lean right and G#
     * sit in the middle, as on a real piano.
     */
    private fun blackKeyLeft(semitone: Int): Float = when (semitone) {
        1 -> blackKeyLeftInGroup(firstWhite = 0, whiteCount = 3, nth = 0) // C#
        3 -> blackKeyLeftInGroup(firstWhite = 0, whiteCount = 3, nth = 1) // D#
        6 -> blackKeyLeftInGroup(firstWhite = 3, whiteCount = 4, nth = 0) // F#
        8 -> blackKeyLeftInGroup(firstWhite = 3, whiteCount = 4, nth = 1) // G#
        10 -> blackKeyLeftInGroup(firstWhite = 3, whiteCount = 4, nth = 2) // A#
        else -> error("Semitone $semitone is a white key")
    }

    private fun blackKeyLeftInGroup(firstWhite: Int, whiteCount: Int, nth: Int): Float {
        val visibleWhite =
            (whiteKeyWidthPx * whiteCount - blackKeyWidthPx * (whiteCount - 1)) / whiteCount
        return whiteKeyWidthPx * firstWhite + visibleWhite * (nth + 1) + blackKeyWidthPx * nth
    }

    private companion object {
        const val SEMITONES_PER_OCTAVE = 12

        /** Semitones above C that fall on white keys, in order. */
        val WHITE_SEMITONES = listOf(0, 2, 4, 5, 7, 9, 11)

        /** 13.7 mm against 23.5 mm on a real piano. */
        const val BLACK_KEY_WIDTH_RATIO = 0.58f

        /** About 95 mm against 150 mm on a real piano. */
        const val BLACK_KEY_LENGTH_RATIO = 0.63f

        /** A real white key is 6.4 times as long as it is wide. Shortened to fit the screen. */
        const val KEY_LENGTH_RATIO = 4.5f
    }
}

private const val NOTE_SIZE_RATIO = 0.8f
