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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChannelCharacterInputReaderJavaTest {

    private ChannelCharacterInputReaderJava reader = new ChannelCharacterInputReaderJava(StandardCharsets.UTF_8, 8192);
    private CsvTokenizerJava2 tokenizer = new CsvTokenizerJava2();


    @Test
    void bulkRead() throws IOException {
        URL resource = getClass().getClassLoader().getResource("SmallSample.txt");
        InputStream inputStream = resource.openStream();
        Stream<char[]> stream = reader.bulkRead(inputStream);
        long count = stream.count();
        assertEquals(10, count);
    }

    @Test
    void bulkReadSmallFile() throws IOException {
        tokenizer = new CsvTokenizerJava2(';', '\n', '"', '\\', 100, 100);
        URL resource = getClass().getClassLoader().getResource("SmallSample.txt");
        InputStream inputStream = resource.openStream();
        Stream<char[]> charStream = reader.bulkRead(inputStream);
        List<Token2[]> collect = tokenizer.tokenize(charStream).collect(Collectors.toList());
        //long count = charStream.map(tokenizer::tokenize).count();
        //List<Token2[]> collect = reader.bulkRead(inputStream).map(tokenizer::tokenize).collect(Collectors.toList());
        //Stream<Token[]> csvTokens = tokenizer.tokenize(charChunks);
        assertEquals(76173, collect);
    }

    @Test
    void bulkReadBiggerFile() throws IOException {
        InputStream inputStream = Files.newInputStream(Paths.get("/home/mariuss/Downloads/5m-sales-records.csv"));
        Stream<char[]> chars = reader.bulkRead(inputStream);
        Stream<String> tokens = tokenizer.tokenize(chars)
                .flatMap(Stream::of)
                .filter(token -> token instanceof DATA2)
                .map(token -> (DATA2) token)
                //.filter(token -> token.getRowIndex() > 4999900)
                .filter(token -> token.getColumnIndex() == 0)
                .map(DATA2::value)
                .distinct();
        //DATA2 last = (DATA2) tokens.reduce((first, second) -> second).get();
        //char[] copy = Arrays.copyOfRange(last.getChars(), last.getChars().length - 100, last.getChars().length);

        List<String> collect = tokens.collect(Collectors.toList());

        //long count = chars.count();
//        List<Token2[]> collect = tokens.collect(Collectors.toList());
        //long count = tokens.count();
        //long count = tokens.limit(1000).count();
        assertEquals(76173, 23);
    }

    @Test
    void bulkReadBiggerFileOld() throws IOException {
        CsvTokenizerJava tokenizer = new CsvTokenizerJava();
        InputStream inputStream = Files.newInputStream(Paths.get("/home/mariuss/Downloads/5m-sales-records.csv"));
        Stream<char[]> chars = reader.bulkRead(inputStream);
        Stream<Token[]> tokens = tokenizer.tokenize(chars);
        //long count = chars.count();
        long count = tokens.count();
        //long count = tokens.limit(1000).count();
        assertEquals(76173, count);
    }
}