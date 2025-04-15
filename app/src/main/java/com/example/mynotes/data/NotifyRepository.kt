package com.example.mynotes.data

interface NotifyRepository {
    fun scheduleReminder(date: Long, hour: Int, minute: Int, context: String)
}