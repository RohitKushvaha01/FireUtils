package com.rk.fireutils.file

import android.os.Build
import androidx.annotation.RequiresApi
import com.rk.fireutils.shell.executeCommand
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.util.Locale

enum class SizeUnit {
    BYTE, MB, GB
}

fun File.size(unit: SizeUnit = SizeUnit.BYTE): Long {
    val size = length()
    return when (unit) {
        SizeUnit.BYTE -> size
        SizeUnit.MB -> size / (1024 * 1024)
        SizeUnit.GB -> size / (1024 * 1024 * 1024)
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun File.readTextAsync(onCompleted: (String) -> Unit){
    GlobalScope.launch(Dispatchers.IO) {
        onCompleted(readText())
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun File.writeAsync(content: String){
    GlobalScope.launch(Dispatchers.IO) {
        writeText(content)
    }
}


@OptIn(DelicateCoroutinesApi::class)
fun File.deleteRecursivelyAsync(){
    GlobalScope.launch(Dispatchers.IO) { deleteRecursively() }
}

fun File.sizeReadable(): String {
    val size = this.length()
    return when {
        size >= 1024 * 1024 * 1024 -> String.format(Locale.ROOT,"%.2f GB", size / (1024.0 * 1024 * 1024))
        size >= 1024 * 1024 -> String.format(Locale.ROOT,"%.2f MB", size / (1024.0 * 1024))
        size >= 1024 -> String.format(Locale.ROOT,"%.2f KB", size / 1024.0)
        else -> "$size bytes"
    }
}

fun File.createDirIfNotExists(): Boolean {
    return if (!this.exists()) {
        this.mkdirs()
    } else {
        false
    }
}

fun File.createFileIfNotExists(): Boolean {
    return if (!this.exists()) {
        this.parentFile?.mkdirs()
        this.createNewFile()
    } else {
        false
    }
}


suspend fun File.realPath(): String {
    return withContext(Dispatchers.IO) {
        executeCommand(listOf("realpath", absolutePath))
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun File.chmod(int: Int){
    GlobalScope.launch(Dispatchers.IO) { executeCommand(listOf("chmod", "$int", absolutePath)) }
}

@RequiresApi(Build.VERSION_CODES.O)
fun File.getMimeType(): String? {
    return try {
        Files.probeContentType(this.toPath())
    } catch (e: Exception) {
        null
    }
}

fun File.listFilesNonNull(): Array<File> {
    return listFiles() ?: emptyArray()
}