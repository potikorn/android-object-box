package com.example.potikorn.todoapp

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
data class TodoModel(
        @Id var id: Long = 0,
        var topic: String? = null,
        var detail: String? = null,
        var date: Date? = null
)
