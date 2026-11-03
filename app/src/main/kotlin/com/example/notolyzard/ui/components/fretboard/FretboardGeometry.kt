package com.example.notolyzard.ui.components.fretboard

/**
 * Where everything on the fretboard sits, in pixels from the top-left corner of the whole
 * fretboard, scrolling aside.
 *
 * Laid out as a right-handed guitar seen from the front: the open strings on the far left,
 * then the nut, then the frets running right, with the lowest string at the bottom. Every
 * fret is [fretSpacingPx] wide. Real frets narrow towards the body, but equal ones keep the
 * notes evenly spread instead of crowding them at one end.
 */
internal class FretboardGeometry(
    val stringCount: Int,
    val fretCount: Int,
    private val fretSpacingPx: Float,
    private val stringSpacingPx: Float,
    private val markerRowHeightPx: Float,
) {

    /** Where the board itself begins. The open strings take one fret's width before it. */
    val nutX: Float = fretWireX(0)

    /** The board runs a little past the last fret wire, as on a real neck. */
    val boardEndX: Float = fretWireX(fretCount) + fretSpacingPx * OVERHANG_RATIO

    val boardHeight: Float = stringSpacingPx * stringCount

    /** Centre line of the strip below the board that holds the fret markers. */
    val markerY: Float = boardHeight + markerRowHeightPx / 2f

    val width: Float = boardEndX
    val height: Float = boardHeight + markerRowHeightPx

    /** The wire that ends [fret]. Wire 0 is the nut. */
    fun fretWireX(fret: Int): Float = fretSpacingPx * (fret + 1)

    /**
     * The middle of [fret], halfway between its wire and the previous one, where the note
     * is drawn. Fret 0 is the open string, left of the nut.
     */
    fun noteX(fret: Int): Float = fretSpacingPx * (fret + 0.5f)

    /** [string] counts from the lowest, which is drawn at the bottom. */
    fun stringY(string: Int): Float = stringSpacingPx * (stringCount - 1 - string + 0.5f)

    private companion object {
        const val OVERHANG_RATIO = 0.25f
    }
}

/**
 * How many marker dots [fret] gets: one on 3, 5, 7 and 9, two on 12, and the same again an
 * octave higher.
 */
internal fun fretMarkerDots(fret: Int): Int = when {
    fret <= 0 -> 0
    fret % OCTAVE_FRETS == 0 -> 2
    fret % OCTAVE_FRETS in SINGLE_DOT_FRETS -> 1
    else -> 0
}

private const val OCTAVE_FRETS = 12
private val SINGLE_DOT_FRETS = setOf(3, 5, 7, 9)
