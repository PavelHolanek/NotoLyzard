package com.example.notolyzard.feature.intervalguessinggame

import com.example.notolyzard.ui.components.ArrowDirection
import com.example.notolyzard.ui.components.IntervalArrowButtonState
import com.example.notolyzard.ui.components.NoteCircleButtonState

enum class AnswerFeedback { Correct, Incorrect }

data class IntervalGuessingGameUiState(
    val streak: UInt,
    val baseNoteState: NoteCircleButtonState,
    val upperNoteState: NoteCircleButtonState,
    val bottomNoteState: NoteCircleButtonState,
    val topArrowState: IntervalArrowButtonState = IntervalArrowButtonState(),
    val bottomArrowState: IntervalArrowButtonState = IntervalArrowButtonState(),
    val guessDirection: ArrowDirection = ArrowDirection.Up,
    val feedback: AnswerFeedback? = null,
)
