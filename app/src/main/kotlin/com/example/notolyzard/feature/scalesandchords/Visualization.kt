package com.example.notolyzard.feature.scalesandchords

/**
 * The ways the picked note groups can be drawn.
 *
 * Declaration order is the order the switcher cycles through, and the first entry is what
 * the screen opens on. Adding a visualization means adding an entry here and a branch in
 * `ScalesAndChordsVisualizationContent` — the switcher itself needs no changes.
 */
enum class Visualization(val label: String) {
    Tonnetz("Tonnetz"),
    CircleOfFifths("Circle of Fifths"),
    ;

    /** The visualization [step] places along, wrapping both ways. Negative steps go back. */
    fun shiftedBy(step: Int): Visualization =
        Visualization.entries[(ordinal + step).mod(Visualization.entries.size)]

    companion object {
        val Default: Visualization = entries.first()
    }
}
