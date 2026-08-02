import com.example.notolyzard.core.theory.PitchClass
import androidx.compose.ui.graphics.Color

data class NoteCircleButtonState(
    val pitchClass: PitchClass,
    val outerColors: List<Color?>,
    val textColor: Color?
)
