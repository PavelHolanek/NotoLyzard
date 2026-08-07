package com.example.notolyzard

import android.app.Application
import com.example.notolyzard.data.notegroups.NoteGroupsModel

class NotoLyzardApplication : Application() {
    val container: AppContainer by lazy { AppContainer() }
}

class AppContainer {
    val noteGroupsModel: NoteGroupsModel = NoteGroupsModel()
}
