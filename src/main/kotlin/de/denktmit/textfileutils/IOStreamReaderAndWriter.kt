package de.denktmit.textfileutils

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

internal class IOStreamReaderAndWriter {

    private fun bufferedReader() {
        val bufferSize = 50000// 64 MB
        BufferedReader(InputStreamReader(System.`in`), bufferSize).use { reader ->
            BufferedWriter(OutputStreamWriter(System.out), bufferSize).use { writer ->
                reader.lineSequence().forEach { row -> processRow(writer, row) }
                writer.flush()
            }
        }
    }

    private fun processRow(writer: BufferedWriter, row: String) {
        writer.write(row); writer.appendLine()
    }


}