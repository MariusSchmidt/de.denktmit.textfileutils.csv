package de.denktmit.textfileutils

import java.io.InputStream
import java.nio.charset.Charset
import java.nio.file.Path

interface CharacterInputReader {
    val charset: Charset
        get() = Charsets.UTF_8
    fun read(path: Path): Sequence<Pair<CharArray, Int>>
    fun bulkRead(inputStream: InputStream): Sequence<Pair<CharArray, Int>>
    fun read(inputStream: InputStream): Sequence<Char>
}