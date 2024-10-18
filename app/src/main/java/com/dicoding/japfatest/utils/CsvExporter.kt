package com.dicoding.japfatest.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.dicoding.japfatest.data.model.UserData
import com.dicoding.japfatest.domain.model.UserDto
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CsvExporter {
    fun exportUsersToCSV(context: Context, userList: List<UserDto>, fileName: String): Boolean {
        val folder = File(context.getExternalFilesDir(null), "ExportedCSV")

        var isSuccess = false

        if (!folder.exists()) {
            folder.mkdir()
        }

        val filePath = File(folder, "$fileName.csv")

        try {
            val writer = FileWriter(filePath)
            writer.append("ID;Full Name;Gender;Birth Date;Address;Latitude;Longitude\n")

            for (user in userList) {
                writer.append("${user.id};${user.fullName};${user.gender};${user.birthDate};${user.address};${user.latitude};${user.longitude}\n")
            }

            writer.flush()
            writer.close()

            Toast.makeText(context, "CSV file saved at: ${filePath.path}", Toast.LENGTH_LONG).show()
            isSuccess = true

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving CSV file", Toast.LENGTH_LONG).show()
        }

        return isSuccess
    }
}