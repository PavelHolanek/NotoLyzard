package com.example.notolyzard.ui.components.tonnetz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

@Stable
class TonnetzState(initialPan: Offset = Offset.Zero) {

    var pan: Offset by mutableStateOf(initialPan)
        private set

    fun panBy(delta: Offset) {
        pan += delta
    }
    fun reset() {
        pan = Offset.Zero
    }

    companion object {
        val Saver: Saver<TonnetzState, *> = listSaver(
            save = { listOf(it.pan.x, it.pan.y) },
            restore = { TonnetzState(Offset(it[0], it[1])) },
        )
    }
}

@Composable
fun rememberTonnetzState(): TonnetzState =
    rememberSaveable(saver = TonnetzState.Saver) { TonnetzState() }
