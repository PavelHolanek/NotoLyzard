package com.example.notolyzard.ui.components

import androidx.compose.ui.graphics.Color
import com.example.notolyzard.core.theory.NoteGroup
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import org.junit.Assert.assertEquals
import org.junit.Test

class PolygonSidesTest {

    @Test
    fun `a triad is a triangle`() {
        assertEquals(listOf(0 to 4, 4 to 7, 0 to 7), polygonSides(listOf(0, 4, 7)))
    }

    @Test
    fun `corners are taken in circle order, whatever order the notes come in`() {
        assertEquals(polygonSides(listOf(0, 4, 7)), polygonSides(listOf(7, 0, 4)))
    }

    @Test
    fun `a note repeated in another octave is one corner`() {
        assertEquals(polygonSides(listOf(0, 4, 7)), polygonSides(listOf(0, 4, 7, 0)))
    }

    @Test
    fun `two notes make a single side`() {
        assertEquals(listOf(0 to 7), polygonSides(listOf(7, 0)))
    }

    @Test
    fun `one note or none make no sides`() {
        assertEquals(emptyList<Pair<Int, Int>>(), polygonSides(listOf(3)))
        assertEquals(emptyList<Pair<Int, Int>>(), polygonSides(emptyList()))
    }
}

class PolygonLinesTest {

    private val c = PitchClass(NoteName.C)

    @Test
    fun `positions count from the note at the top`() {
        val lines = polygonLines(listOf(layer(RED, NoteName.C, NoteName.E)), top = PitchClass(NoteName.A))
        assertEquals(listOf(3 to 7), lines.map { it.from to it.to })
    }

    @Test
    fun `a side of one group is a single line down the middle`() {
        val lines = polygonLines(listOf(layer(RED, NoteName.C, NoteName.E, NoteName.G)), top = c)
        assertEquals(3, lines.size)
        lines.forEach { assertEquals(0f, it.shift) }
    }

    @Test
    fun `a shared side gets one line per group, side by side in layer order`() {
        val lines = polygonLines(
            listOf(
                layer(RED, NoteName.C, NoteName.E, NoteName.G), // C major
                layer(BLUE, NoteName.A, NoteName.C, NoteName.E), // A minor, shares C–E
            ),
            top = c,
        )
        val shared = lines.filter { it.from == 0 && it.to == 4 }
        assertEquals(listOf(RED to -0.5f, BLUE to 0.5f), shared.map { it.color to it.shift })
        assertEquals(6, lines.size)
    }

    @Test
    fun `three groups on one side spread round the middle`() {
        val lines = polygonLines(
            listOf(RED, BLUE, GREEN).map { layer(it, NoteName.C, NoteName.G) },
            top = c,
        )
        assertEquals(listOf(-1f, 0f, 1f), lines.map { it.shift })
    }

    @Test
    fun `a row with nothing picked draws nothing`() {
        assertEquals(emptyList<PolygonLine>(), polygonLines(listOf(NoteGroupLayer(null, RED)), top = c))
    }

    private fun layer(color: Color, vararg names: NoteName) = NoteGroupLayer(
        noteGroup = object : NoteGroup<PitchClass> {
            override val notes = names.map(::PitchClass)
        },
        color = color,
    )

    private companion object {
        val RED = Color.Red
        val BLUE = Color.Blue
        val GREEN = Color.Green
    }
}
