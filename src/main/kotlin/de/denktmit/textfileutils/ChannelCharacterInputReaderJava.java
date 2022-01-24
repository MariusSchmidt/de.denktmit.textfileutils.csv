package de.denktmit.textfileutils;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ChannelCharacterInputReaderJava {

    private final Charset charset;
    private final int bufferSize;

    public ChannelCharacterInputReaderJava(Charset charset, int bufferSize) {
        this.charset = charset;
        this.bufferSize = bufferSize;
    }

    private static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public Stream<char[]> bulkRead(InputStream inputStream) {
        Reader reader = Channels.newReader(Channels.newChannel(inputStream), charset.name());
        try {
            Iterator<char[]> iter = iterator(reader);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                            iter, Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE), false);
                    //.onClose(asUncheckedRunnable(reader));
        } catch (Error | RuntimeException e) {
            try {
                reader.close();
            } catch (IOException ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {
                }
            }
            throw e;
        }
    }

    private Iterator<char[]> iterator(Reader reader) {
        return new Iterator<char[]>() {
            final CharBuffer charBuffer = CharBuffer.allocate(bufferSize);
            int bytesLastRead = -1;

            @Override
            public boolean hasNext() {
                if (bytesLastRead >= 0) {
                    return true;
                } else {
                    try {
                        bytesLastRead = reader.read(charBuffer);
                        charBuffer.flip();
                        return bytesLastRead != -1;
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }

            @Override
            public char[] next() {
                if (hasNext()) {
                    char[] charsRead = Arrays.copyOf(charBuffer.array(), bytesLastRead);
                    charBuffer.clear();
                    bytesLastRead = -1;
                    return charsRead;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }


}
