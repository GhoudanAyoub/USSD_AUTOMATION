package com.gws.common.utils

import android.content.Context
import dagger.Reusable
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import javax.inject.Inject

const val APP_USSD_DIRECTORY_NAME = "Ussd"

@Reusable
class FileUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun saveDataToInternalStorage(filename: String, data: String) {
        val dir = getFilesDir()
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val fileToSave = File(dir, filename)
            val writer = FileWriter(fileToSave)
            writer.append(data)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readFileFromInternalStorage(fileName: String): String {
        var fileContent = ""
        if (fileName.isNotEmpty()) {
            val filesDir = getFilesDir()
            if (!filesDir.exists()) {
                filesDir.mkdir()
            }
            val file = File(getFilesDir(), fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileInputStream = FileInputStream(file)
            fileContent = fileInputStream.bufferedReader().use(BufferedReader::readText)
        }
        return fileContent
    }

    fun deleteInternalStorageFile(fileName: String) {
        File(getFilesDir(), fileName).delete()
    }

    private fun getFilesDir(): File {
        return File(context.filesDir, APP_USSD_DIRECTORY_NAME)
    }
}
