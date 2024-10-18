package com.rk.fireutils.shell

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

suspend fun executeCommand(command: List<String>): String {
    return withContext(Dispatchers.IO) {
        val process = ProcessBuilder(command).redirectErrorStream(true) // Combine standard error with standard output
            .start()
        
        // Read the output from the process
        val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.readText()
        }
        
        // Wait for the process to complete
        process.waitFor()
        output.trim() // Remove any trailing whitespace
    }
}