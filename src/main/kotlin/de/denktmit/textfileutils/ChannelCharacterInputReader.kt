package de.denktmit.textfileutils

import java.io.InputStream
import java.io.Reader
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.StandardOpenOption

internal class ChannelCharacterInputReader(
    override val charset: Charset = Charsets.UTF_8,
    val bufferSize: Int = 8192
) : CharacterInputReader {

    override fun read(path: Path): Sequence<Pair<CharArray, Int>> = sequence {
        Channels.newReader(FileChannel.open(path, StandardOpenOption.READ), charset.name()).use { reader ->
            readToBufferAndPublish(reader)
        }
    }

    override fun bulkRead(inputStream: InputStream): Sequence<Pair<CharArray, Int>> = sequence {
        Channels.newReader(Channels.newChannel(inputStream), charset.name()).use { reader ->
            readToBufferAndPublish(reader)
        }
    }

    override fun read(inputStream: InputStream): Sequence<Char> = bulkRead(inputStream).flatMap { (chars, charCount) ->
        chars.sliceArray(0..charCount).asSequence()
    }

    private suspend fun SequenceScope<Pair<CharArray, Int>>.readToBufferAndPublish(reader: Reader) {
        var chars = CharArray(bufferSize)
        var bytesRead = reader.read(chars, 0, bufferSize)
        while (bytesRead != -1) {
            yield(chars to bytesRead)
            chars = CharArray(bufferSize)
            bytesRead = reader.read(chars, 0, bufferSize)
        }
    }

}