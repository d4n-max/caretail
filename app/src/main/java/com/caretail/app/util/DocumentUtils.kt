package com.caretail.app.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getDisplayNameFromUri(context: Context, uri: Uri): String =
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else {
            uri.lastPathSegment.orEmpty()
        }
    }.orEmpty().ifBlank { uri.lastPathSegment.orEmpty().ifBlank { "Selected file" } }

fun getMimeTypeFromUri(context: Context, uri: Uri): String =
    context.contentResolver.getType(uri) ?: "*/*"

fun formatDocumentDate(millis: Long): String = formatDate(millis)
