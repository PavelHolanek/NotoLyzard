import androidx.lifecycle.ViewModel
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass

class NoteCircleButtonViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<NoteCircleButtonState> = _uiState.asStateFlow()

    fun onButtonClicked(note: PitchClass) {
    }

    fun onScaleTypeSelected(type: ScaleType) {
        setState { it.copy(scaleType = type) }
    }

    private companion object {
        val DEFAULT_PITCH_CLASS = PitchClass(NoteName.A)
        val DEFAULT_TEXT_COLOR = Color.Black
        val DEFAULT_OUTER_COLORS = listOf(null, null, null)

        fun initialState() = NoteCircleButtonState(
            pitchClass = DEFAULT_PITCH_CLASS
            textColor = DEFAULT_TEXT_COLOR
            outerColors = DEFAULT_OUTER_COLORS     
        )
    }
}