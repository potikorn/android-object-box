package com.example.potikorn.todoapp

import android.app.Application
import io.objectbox.BoxStore

class ToDoApplication : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder().androidContext(this).build()
    }
}