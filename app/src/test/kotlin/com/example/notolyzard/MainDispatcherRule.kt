package com.example.notolyzard

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Swaps `Dispatchers.Main` for a test dispatcher.
 *
 * Needed by any test touching a ViewModel that uses `viewModelScope`, because that scope
 * runs on `Dispatchers.Main`, which does not exist in a plain JVM unit test.
 */
class MainDispatcherRule(
    /**
     * Exposed so a test can pass it to `runTest`. Both must share one scheduler, or work
     * launched in `viewModelScope` never runs on the scheduler the test advances.
     */
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
