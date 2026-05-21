package com.caretail.app.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val DatePattern = "MMM d, yyyy"
private const val InputDatePattern = "yyyy-MM-dd"
private const val TimePattern = "h:mm a"
private const val InputTimePattern = "HH:mm"

fun startOfTodayMillis(nowMillis: Long = System.currentTimeMillis()): Long =
    Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

fun endOfTodayMillis(nowMillis: Long = System.currentTimeMillis()): Long =
    Calendar.getInstance().apply {
        timeInMillis = startOfTodayMillis(nowMillis)
        add(Calendar.DAY_OF_YEAR, 1)
        add(Calendar.MILLISECOND, -1)
    }.timeInMillis

fun isToday(millis: Long, nowMillis: Long = System.currentTimeMillis()): Boolean =
    millis in startOfTodayMillis(nowMillis)..endOfTodayMillis(nowMillis)

fun isOverdue(millis: Long, isCompleted: Boolean, nowMillis: Long = System.currentTimeMillis()): Boolean =
    !isCompleted && millis < nowMillis

fun defaultReminderDueAtMillis(nowMillis: Long = System.currentTimeMillis()): Long =
    Calendar.getInstance().apply {
        timeInMillis = nowMillis
        add(Calendar.HOUR_OF_DAY, 1)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

fun formatDate(millis: Long): String = SimpleDateFormat(DatePattern, Locale.getDefault()).format(millis)

fun formatTime(millis: Long): String = SimpleDateFormat(TimePattern, Locale.getDefault()).format(millis)

fun formatInputDate(millis: Long): String = SimpleDateFormat(InputDatePattern, Locale.getDefault()).format(millis)

fun formatInputTime(millis: Long): String = SimpleDateFormat(InputTimePattern, Locale.getDefault()).format(millis)

fun parseDateTimeMillis(dateText: String, timeText: String): Long? {
    val parser = SimpleDateFormat("$InputDatePattern $InputTimePattern", Locale.getDefault()).apply {
        isLenient = false
    }
    return try {
        parser.parse("${dateText.trim()} ${timeText.trim()}")?.time
    } catch (_: ParseException) {
        null
    }
}
