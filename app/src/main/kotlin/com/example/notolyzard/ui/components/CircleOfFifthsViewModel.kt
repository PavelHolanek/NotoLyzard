import androidx.lifecycle.ViewModel
import com.example.notolyzard.core.theory.NoteName
import com.example.notolyzard.core.theory.PitchClass
import com.example.notolyzard.core.theory.NoteGroup

class CircleOfFifthsViewModel(noteGroupModel NoteGroupsModel) : ViewModel(){

    private val _uiState = MutableStateFlow(initialState())
    private val buttonModels : List<NoteCircleButtonViewModel>

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