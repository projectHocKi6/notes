package com.example.mynotes

import android.app.Application
import com.example.mynotes.data.AppContainer
import com.example.mynotes.data.AppDataContainer

class NoteApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}