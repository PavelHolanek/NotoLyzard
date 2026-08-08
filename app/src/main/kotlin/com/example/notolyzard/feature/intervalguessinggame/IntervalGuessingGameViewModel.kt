package com.example.notolyzard.feature.intervalguessinggame

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notolyzard.NotoLyzardApplication
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.ui.components.ArrowDirection
import com.example.notolyzard.ui.components.IntervalArrowButtonState
import com.example.notolyzard.ui.components.NoteCircleButtonState
import com.example.notolyzard.ui.components.pitchclasspicker.PitchClassPickerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class IntervalGuessingGameViewModel() : ViewModel() {

    private var useRandomBaseNote : Boolean = true
        set(value) {
            field = value
            publish()
        }

    val pickerState: PitchClassPickerState = PitchClassPickerState(
        maxSelected = 1,
        onSelectionChanged = { selection -> selection.singleOrNull()?.let(::pitchClassChosen) },
    ).apply { randomize() }

    private var enabledDirections: Set<ArrowDirection> =
        setOf(ArrowDirection.Up, ArrowDirection.Down)

    private var streak: UInt = 0u

    private var round: Round = newRound(randomPitchClass())

    private var feedback: AnswerFeedback? = null
    private var feedbackJob: Job? = null

    private var _uiState = MutableStateFlow(currentUiState())
    val uiState: StateFlow<IntervalGuessingGameUiState> = _uiState.asStateFlow()

    private fun toggleDirection(direction: ArrowDirection) {
        val turningOff = direction in enabledDirections
        if (turningOff && enabledDirections.size == 1) return

        enabledDirections =
            if (turningOff) enabledDirections - direction else enabledDirections + direction

        if (round.direction !in enabledDirections) {
            // The flash belongs to a question that no longer exists, and a pending advance
            // would overwrite the question this is about to ask.
            cancelFeedback()
            round = newRound(round.baseNote)
        }

        publish()
    }

    fun onTopArrowButtonClicked() = toggleDirection(ArrowDirection.Up)

    fun onBottomArrowButtonClicked() = toggleDirection(ArrowDirection.Down)

    fun onCenterNoteClicked() {
        useRandomBaseNote = !useRandomBaseNote
    }

    fun pitchClassChosen(chosen: PitchClass) {
        // A right answer is already on its way to the next round. Anything picked in the
        // meantime would be scored against a question that has been answered.
        if (feedback == AnswerFeedback.Correct) return

        val correct = chosen == round.answer
        streak = if (correct) streak + 1u else 0u
        feedback = if (correct) AnswerFeedback.Correct else AnswerFeedback.Incorrect

        pickerState.clearSelection()
        publish()

        feedbackJob?.cancel()
        feedbackJob = viewModelScope.launch {
            delay(FEEDBACK_MILLIS)
            feedback = null
            if (correct) round = newRound(nextBaseNote())
            publish()
        }
    }

    private fun cancelFeedback() {
        feedbackJob?.cancel()
        feedbackJob = null
        feedback = null
    }

    private fun newRound(baseNote: PitchClass) = Round(
        baseNote = baseNote,
        direction = enabledDirections.random(Random.Default),
        interval = INTERVAL_RANGE.random(Random.Default),
    )

    private fun nextBaseNote(): PitchClass =
        if (useRandomBaseNote) randomPitchClass() else round.baseNote

    private fun randomPitchClass(): PitchClass = PitchClass(NoteName.entries.random(Random.Default))

    private fun publish() {
        _uiState.value = currentUiState()
    }

    private fun currentUiState() = IntervalGuessingGameUiState(
        streak = streak,
        baseNoteState = NoteCircleButtonState(round.baseNote, baseNoteRingColors(), null),
        upperNoteState = answerSlot(ArrowDirection.Up),
        bottomNoteState = answerSlot(ArrowDirection.Down),
        topArrowState = arrowState(ArrowDirection.Up),
        bottomArrowState = arrowState(ArrowDirection.Down),
        guessDirection = round.direction,
        feedback = feedback,
    )

    private fun baseNoteRingColors(): List<Color?> =
        if (useRandomBaseNote) emptyList() else listOf(Color.Black)

    private fun answerSlot(direction: ArrowDirection) = NoteCircleButtonState(
        pitchClass = null,
        outerColors = emptyList(),
        textColor = null,
        label = if (direction == round.direction) UNKNOWN_LABEL else "",
    )

    private fun arrowState(direction: ArrowDirection) = IntervalArrowButtonState(
        label = if (direction == round.direction) round.interval.toString() else "",
        isActive = direction in enabledDirections,
    )

    companion object {
        private val INTERVAL_RANGE = 1..11

        private const val UNKNOWN_LABEL = "?"

        /** Long enough to register, short enough not to be in the way of the next guess. */
        private const val FEEDBACK_MILLIS = 300L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                    as NotoLyzardApplication
                IntervalGuessingGameViewModel()
            }
        }
    }
}

private data class Round(
    val baseNote: PitchClass,
    val direction: ArrowDirection,
    val interval: Int,
) {
    val answer: PitchClass
        get() = when (direction) {
            ArrowDirection.Up -> baseNote + interval
            ArrowDirection.Down -> baseNote - interval
        }
}
