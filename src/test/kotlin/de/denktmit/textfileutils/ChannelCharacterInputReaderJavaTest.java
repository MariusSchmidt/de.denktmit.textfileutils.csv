package de.denktmit.textfileutils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChannelCharacterInputReaderJavaTest {

    private final ChannelCharacterInputReaderJava reader = new ChannelCharacterInputReaderJava(StandardCharsets.UTF_8, 8192);
    private final CsvTokenizerJava tokenizer = new CsvTokenizerJava();


    @Test
    void bulkRead() throws IOException {
        URL resource = getClass().getClassLoader().getResource("SmallSample.txt");
        InputStream inputStream = resource.openStream();
        Stream<char[]> stream = reader.bulkRead(inputStream);
        long count = stream.count();
        assertEquals(10, count);
    }

    @Test
    void bulkReadBiggerFile() throws IOException {
        InputStream inputStream = Files.newInputStream(Paths.get("/home/mariuss/Downloads/5m-sales-records.csv"));
//        Stream<char[]> charChunks = reader.bulkRead(inputStream);

//        URL resource = getClass().getClassLoader().getResource("SmallSample.txt");
//        InputStream inputStream = resource.openStream();
        Stream<Token[]> tokens = reader.bulkRead(inputStream).map(tokenizer::tokenize);
        //Stream<Token[]> csvTokens = tokenizer.tokenize(charChunks);
        long count = tokens.limit(1000).count();
        assertEquals(76173, count);
    }
}