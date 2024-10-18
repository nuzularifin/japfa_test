package com.dicoding.japfatest.utils

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.model.UserDto
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.N)
fun getCurrentDateTime() : String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun convertDateString(inputDate: String): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm.ss", Locale("id"))

    return try {
        val date: Date? = inputFormatter.parse(inputDate)
        date?.let { outputFormatter.format(it) } ?: "Format Tanggal Tidak Valid"
    } catch (e: Exception) {
        e.printStackTrace()
        "Error: ${e.message}"
    }
}

fun View.show(show: Boolean){
    visibility = if (show) View.VISIBLE else View.GONE
}

fun showToastMessage(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun getFileFromUri(context: Context, uri: Uri?): File? {
    val fileName = getFileName(context, uri) ?: return null
    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    if (uri != null){
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    } else {
        return null
    }

    return tempFile
}

fun getFileName(context: Context, uri: Uri?): String? {
    var result: String? = null
    if (uri?.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri?.path?.substring(uri.path?.lastIndexOf('/') ?: (0 + 1))
    }
    return result
}

fun UserDto.toUserData() : UserData {
    return UserData(
        id = id ?: 0,
        fullName = fullName,
        gender = gender,
        birthDate = birthDate,
        address = address,
        dateTime = dateTime,
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude,
    )
}

fun UserData.toUserDto() : UserDto {
    return UserDto(
        id = id,
        fullName = fullName,
        gender = gender,
        birthDate = birthDate,
        address = address,
        dateTime = dateTime,
        photoUri = photoUri,
        latitude = latitude,
        longitude = longitude
    )
}