package de.denktmit.textfileutils

import java.io.OutputStream
import java.nio.charset.Charset

internal class ChannelCharachterOutputWriter(
    override val charset: Charset = Charsets.UTF_8,
    val bufferSize: Int = 8192
): CharachterOutputWriter {

    override fun write(outputStream: OutputStream, characterChunks: Sequence<Array<CharArray>>) {
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