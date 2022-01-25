package de.denktmit.textfileutils;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Stream;

public class CsvTokenizerJava2 {

    private final char delimiter;
    private final char eol ;
    private final char enclosure;
    private final char escapeSign;
    private final int averageFieldCharCount;
    private final int tokenQueueMinCapacity;

    public CsvTokenizerJava2() {
        this(',', '\n', '"', '\\', 100, 20);
    }

    public CsvTokenizerJava2(char delimiter, char eol, char enclosure, char escapeSign, int averageFieldCharCount, int tokenQueueMinCapacity) {
        this.delimiter = delimiter;
        this.eol = eol;
        this.enclosure = enclosure;
        this.escapeSign = escapeSign;
        this.averageFieldCharCount = averageFieldCharCount;
        this.tokenQueueMinCapacity = tokenQueueMinCapacity;
    }

    public Stream<Token2[]> tokenize(Stream<char[]> charChunks) {
        TokenizerState2 state = new TokenizerState2();
        return charChunks.map(chars -> tokenize(state, chars));
    }

    public Token2[] tokenize(TokenizerState2 state, char[] chars) {
        state.prepareNextChunk(chars);
        for (char c : chars) {
            tokenize(state, c);
        }
        handleEndOfChunk(state);
        return state.getTokens();
    }

    private void tokenize(TokenizerState2 state, char c) {
        if (state.enclosed && state.escaped) { handleEscapedChar(state, c); }
        else if (state.enclosed && c == escapeSign) { handleEscapeNext(state, c); }
        else if (state.enclosed && c == enclosure) { handleEnclosureClose(state, c); }
        else if (state.enclosed) { state.appendChar(c); }
        else if (c == eol) { handleEndOfLine(state, c); }
        else if (c == delimiter) { handleDelimiter(state, c); }
        else if (c == enclosure && state.dataBufferLength() == 0) { handleEnclosureOpen(state, c); }
        else { handleCharacter(state, c);}
    }

    private void handleEscapedChar(TokenizerState2 state, char c) {
        state.appendChar(c);
        state.escaped = false;
    }

    private void handleEscapeNext(TokenizerState2 state, char c) {
        state.appendChar(c);
        state.escaped = true;
    }

    private void handleEnclosureClose(TokenizerState2 state, char c) {
        state.yieldField();
        state.yield(ENCLOSURE2.INSTANCE);
    }

    private void handleEndOfLine(TokenizerState2 state, char c) {
        if (state.previousTokenAdded != ENCLOSURE2.INSTANCE) {
            state.yieldField();
        }
        state.yield(EOL2.INSTANCE);
        ++state.rowIndex;
        state.columnIndex = 0;
    }

    private void handleDelimiter(TokenizerState2 state, char c) {
        if (state.previousTokenAdded != ENCLOSURE2.INSTANCE) {
            state.yieldField();
        }
        state.yield(DELIMITER2.INSTANCE);
        ++state.columnIndex;
    }

    private void handleEnclosureOpen(TokenizerState2 state, char c) {
        state.yield(ENCLOSURE2.INSTANCE);
        state.enclosed = true;
    }

    private void handleCharacter(TokenizerState2 state, char c) {
        state.appendChar(c);
    }

    private void handleEndOfChunk(TokenizerState2 state) {
        if (state.dataIndexStart < state.dataIndexEnd) {
            state.yieldField();
        }
    }

    private class TokenizerState2 {
        private Queue<Token2> tokenBuffer;
        private boolean enclosed = false;
        private boolean escaped = false;
        private Token2 previousTokenAdded = null;

        private char[] chars = null;
        private int rowIndex = 0;
        private int columnIndex = 0;
        private int dataIndexStart = 0;
        private int dataIndexEnd = 0;

        private TokenizerState2() {
            this.tokenBuffer = new ArrayDeque<>(tokenQueueMinCapacity);
        }

        private void prepareNextChunk(char[] chars) {
            if (this.chars == null) {
                this.chars = chars;
                return;
            }
            this.chars = chars;
            tokenBuffer = new ArrayDeque<>(tokenQueueMinCapacity);
            dataIndexStart = 0;
            dataIndexEnd = 0;
        }

        private void appendChar(char c) {
            ++dataIndexEnd;
        }

        private void yield(Token2 token) {
            tokenBuffer.add(token);
            previousTokenAdded = token;
            dataIndexStart = ++dataIndexEnd;
        }

        private void yieldField() {
            DATA2 token = new DATA2(chars, rowIndex, columnIndex, dataIndexStart, dataIndexEnd);
            tokenBuffer.add(token);
            previousTokenAdded = token;
            dataIndexStart = dataIndexEnd;
        }

        private int dataBufferLength() {
            return dataIndexEnd - dataIndexStart;
        }

        public Token2[] getTokens() {
            return tokenBuffer.toArray(new Token2[0]);
        }
    }


}
