package com.example.notolyzard.ui.components.tonnetz

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.example.notolyzard.core.theory.PitchClass
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * A node of the Tonnetz lattice.
 *
 * [col] runs to the right in perfect fifths, [row] runs down and to the right in minor
 * thirds. The third direction falls out of those two: one step right and one step up is
 * 7 - 3 = 4 semitones, a major third. That is what makes every triangle of neighbouring
 * nodes a major or a minor triad.
 */
data class TonnetzCoord(val col: Int, val row: Int) {
    operator fun plus(other: TonnetzCoord) = TonnetzCoord(col + other.col, row + other.row)
}

/**
 * The note at this node, given the note at (0, 0).
 *
 * Being linear in [TonnetzCoord.col] and [TonnetzCoord.row] is the whole trick behind
 * panning: shifting the lattice by whole nodes is indistinguishable from changing [origin],
 * so nothing has to be created or destroyed at the edges of the screen.
 */
fun TonnetzCoord.pitchClass(origin: PitchClass): PitchClass =
    origin + COL_SEMITONES * col + ROW_SEMITONES * row

/**
 * Maps between lattice coordinates and screen pixels for a lattice whose neighbours are
 * [cellPx] apart — in all six directions, since the rows are offset by half a cell.
 */
class TonnetzGeometry(private val cellPx: Float) {

    /** Rows sit closer together than columns: the height of an equilateral triangle. */
    val rowHeightPx: Float = cellPx * ROW_HEIGHT_RATIO

    /** Where [coord] sits relative to node (0, 0), with y growing downwards. */
    fun screenOffset(coord: TonnetzCoord): Offset = Offset(
        x = cellPx * (coord.col + coord.row / 2f),
        y = rowHeightPx * coord.row,
    )

    /**
     * The node that [pan] has brought closest to the top-left corner, rounded so that it
     * never lands past the corner. [window] is expressed relative to this node.
     */
    fun anchor(pan: Offset): TonnetzCoord {
        val row = floor(-pan.y / rowHeightPx).toInt()
        // The row has to be settled first: the lattice is sheared, so which column sits at
        // a given x depends on how far down the row already pushed it.
        val col = floor(-pan.x / cellPx - row / 2f).toInt()
        return TonnetzCoord(col, row)
    }

    /** What is left of [pan] once [anchor] has absorbed the whole nodes. Under one cell. */
    fun residual(pan: Offset): Offset = pan + screenOffset(anchor(pan))

    /**
     * The nodes worth drawing for a viewport of this size, relative to the anchor.
     *
     * The column range slides left as rows go down, following the shear. A rectangular
     * bounding box would work too, but it would spend a whole triangle of nodes off the
     * side of the screen.
     */
    fun window(viewport: Size): List<TonnetzCoord> {
        val lastRow = ceil(viewport.height / rowHeightPx).toInt() + MARGIN
        val colSpan = ceil(viewport.width / cellPx).toInt() + MARGIN * 2
        return (-MARGIN..lastRow).flatMap { row ->
            val firstCol = floor(-row / 2f).toInt() - MARGIN
            (firstCol..firstCol + colSpan).map { col -> TonnetzCoord(col, row) }
        }
    }

    private companion object {
        /** One node of slack all round, so edges leading off screen still have both ends. */
        const val MARGIN = 1
    }
}

/** Semitones gained by one step along +[TonnetzCoord.col]: a perfect fifth. */
private const val COL_SEMITONES = 7

/** Semitones gained by one step along +[TonnetzCoord.row]: a minor third. */
private const val ROW_SEMITONES = 3

private val ROW_HEIGHT_RATIO = sqrt(3f) / 2f
