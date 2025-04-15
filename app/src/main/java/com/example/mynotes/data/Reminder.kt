package com.example.mynotes.data


data class Reminder(
    var date: Long,
    var hour: Int,
    val minute: Int,
    val context: String
)
