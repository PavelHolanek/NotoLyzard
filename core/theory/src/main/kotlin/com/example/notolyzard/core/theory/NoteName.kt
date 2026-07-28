package com.example.notolyzard.core.theory

/**
 * The twelve pitch classes of the chromatic scale.
 *
 * Ported from `BasicNote.NoteType`. The ordering starts at A, so [Ordinal] values run
 * A = 0, A# = 1, B = 2, C = 3 ... G# = 11. This is deliberately kept from the C# original
 * rather than switched to the more common C = 0 convention, because all semitone
 * arithmetic in this library is expressed against it.
 *
 * [symbol] replaces the original `BasicNote.GetNoteString` switch.
 */
enum class NoteName(val symbol: String) {
    A("A"),
    A_SHARP("A#"),
    B("B"),
    C("C"),
    C_SHARP("C#"),
    D("D"),
    D_SHARP("D#"),
    E("E"),
    F("F"),
    F_SHARP("F#"),
    G("G"),
    G_SHARP("G#"),
    ;

    companion object {
        /** Number of pitch classes in an octave. */
        const val SEMITONES_PER_OCTAVE: Int = 12

        /**
         * Maps a semitone index to a [NoteName].
         *
         * Note: unlike the C# original, which used an unchecked `(NoteType)` cast and would
         * silently produce an undefined enum value, this throws for anything outside 0..11.
         * Kotlin enums cannot represent an out-of-range ordinal.
         */
        fun ofSemitone(semitone: Int): NoteName {
            require(semitone in entries.indices) {
                "Semitone $semitone is outside 0..${entries.size - 1}"
            }
            return entries[semitone]
        }
    }
}
