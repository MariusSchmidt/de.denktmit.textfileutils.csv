package de.denktmit.textfileutils

import java.io.OutputStream
import java.nio.charset.Charset

interface CharachterOutputWriter {

    val charset: Charset
        get() = Charsets.UTF_8

    fun write(outputStream: OutputStream, characterChunks: Sequence<Array<CharArray>>) {
        TODO()
//        Channels.newWriter(Channels.newChannel(outputStream), Charsets.UTF_8.name()).use { writer ->
//            val outputBuffer = CharBuffer.allocate(bufferSize)
//            characterChunks.forEach { charSequence ->
//                if (outputBuffer.remaining() >= charSequence.length) {
//                    outputBuffer.put()
//                }
//                val (charArray, bytesAvailable) = chunk
//                writer.write(charArray, 0, bytesAvailable)
//            }
//        }
    }

}