package com.example.notolyzard

import android.app.Application
import com.example.notolyzard.data.notegroups.NoteGroupsModel

/**
 * Holds the objects that must outlive a single screen.
 *
 * This is manual dependency injection — deliberately, for now. [NoteGroupsModel] is
 * shared state: several ViewModels have to see the same instance, and this is the
 * simplest place to guarantee that. When a second or third such object appears, replace
 * [AppContainer] with Hilt rather than growing it.
 */
class NotoLyzardApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}

class AppContainer {
    /** The one selection every visualization reads from. */
    val noteGroupsModel: NoteGroupsModel = NoteGroupsModel()
}
