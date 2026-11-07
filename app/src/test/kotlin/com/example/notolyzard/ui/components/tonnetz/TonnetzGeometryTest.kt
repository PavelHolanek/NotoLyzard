package com.example.notolyzard.ui.components.tonnetz

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TonnetzGeometryTest {

    private val geometry = TonnetzGeometry(cellPx = 100f)
    private val radius = 30f

    @Test
    fun `a tap on a node's centre finds it`() {
        listOf(
            TonnetzCoord(0, 0),
            TonnetzCoord(3, 0),
            TonnetzCoord(0, 1),
            TonnetzCoord(-2, 3),
            TonnetzCoord(1, -4),
        ).forEach { node ->
            assertEquals(node, geometry.nodeAt(geometry.screenOffset(node), radius))
        }
    }

    @Test
    fun `a tap near the edge of a note still finds it`() {
        val node = TonnetzCoord(2, 1)
        val nearEdge = geometry.screenOffset(node) + Offset(20f, -20f)
        assertEquals(node, geometry.nodeAt(nearEdge, radius))
    }

    @Test
    fun `a tap between the notes finds nothing`() {
        // Halfway between (0, 0) and (1, 0), 50 from each.
        assertNull(geometry.nodeAt(Offset(50f, 0f), radius))
    }

    @Test
    fun `notes reaching into the next row are still told apart`() {
        // Big enough to overlap the row above, where rounding the row alone goes wrong:
        // this point rounds to row 0 but sits closer to (0, 1).
        val big = 60f
        val point = Offset(50f, geometry.rowHeightPx * 0.45f)
        assertEquals(TonnetzCoord(0, 1), geometry.nodeAt(point, big))
    }
}
