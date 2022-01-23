package de.denktmit.textfileutils

import java.io.OutputStream
import java.io.Writer
import java.nio.CharBuffer
import java.nio.channels.Channels
import java.nio.charset.Charset

internal class ChannelCharachterOutputWriter(
    override val charset: Charset = Charsets.UTF_8,
    val bufferSize: Int = 8192
): CharacterOutputWriter {

    override fun write(outputStream: OutputStream, chars: Sequence<Char>) {
        val charBuffer = CharBuffer.allocate(bufferSize)
        Channels.newWriter(Channels.newChannel(outputStream), charset.name()).use { writer ->
            chars.forEach { char ->
                if (charBuffer.hasRemaining()) {
                    charBuffer.append(char)
                } else {
                    writeFromBuffer(charBuffer, writer)
                }
            }
            if (charBuffer.isNotEmpty()) {
                writeFromBuffer(charBuffer, writer)
            }
        }
    }

    private fun writeFromBuffer(charBuffer: CharBuffer, writer: Writer) {
        charBuffer.flip()
        val charArray = CharArray(charBuffer.remaining())
        charBuffer.get(charArray)
        charBuffer.clear()
        writer.write(charArray)
    }

}