package de.denktmit.textfileutils;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.stream.Stream;

public class CsvTokenizerJava {

    private final char delimiter;
    private final String eol ;
    private final char enclosure;
    private final char escapeSign;
    private final int averageFieldCharCount;
    private final int tokenQueueMinCapacity;

    public CsvTokenizerJava() {
        this(',', System.lineSeparator(), '"', '\\', 100, 20);
    }

    public CsvTokenizerJava(char delimiter, String eol, char enclosure, char escapeSign, int averageFieldCharCount, int tokenQueueMinCapacity) {
        this.delimiter = delimiter;
        this.eol = eol;
        this.enclosure = enclosure;
        this.escapeSign = escapeSign;
        this.averageFieldCharCount = averageFieldCharCount;
        this.tokenQueueMinCapacity = tokenQueueMinCapacity;
    }

    public Stream<Token[]> tokenize(Stream<char[]> charChunks) {
        TokenizerState state = new TokenizerState();
        return charChunks.map(charChunk -> tokenize(state, charChunk));
        //Stream<Token[]> endOfFileStream = Stream.generate(() -> handleEndOfFile(state));
        //return Stream.concat(tokenizedStream, endOfFileStream);
    }

    private Token[] tokenize(TokenizerState state , char[] charChunk) {
        for (char c : charChunk) {
            tokenize(state, c);
        }
        return state.getTokens();
    }

    private void tokenize(TokenizerState state, char c) {
        if (state.enclosed && state.escaped) { handleEscapedChar(state, c); }
        else if (state.enclosed && c == escapeSign) { handleEscapeNext(state, c); }
        else if (state.enclosed && c == enclosure) { handleEnclosureClose(state, c); }
        else if (state.enclosed) { state.appendChar(c); }
        else if (c == eol.charAt(state.eolBuffer.length()) ) { handleEndOfLine(state, c); }
        else if (c == delimiter) { handleDelimiter(state, c); }
        else if (c == enclosure && state.dataBuffer.length() == 0) { handleEnclosureOpen(state, c); }
        else { handleCharacter(state, c);}
    }

    private void handleEscapedChar(TokenizerState state, char c) {
        state.appendChar(c);
        state.escaped = false;
    }

    private void handleEscapeNext(TokenizerState state, char c) {
        state.appendChar(c);
        state.escaped = true;
    }

    private void handleEnclosureClose(TokenizerState state, char c) {
        state.yieldField();
        state.yield(ENCLOSURE.INSTANCE);
    }

    private void handleEndOfLine(TokenizerState state, char c) {
        state.appendCharEOL(c);
        if (state.eolBuffer.length() == eol.length()) {
            if (state.previousTokenAdded != ENCLOSURE.INSTANCE) {
                state.yieldField();
            }
            state.yield(EOL.INSTANCE);
            state.clearEolBuffer(false);
        }
    }

    private void handleDelimiter(TokenizerState state, char c) {
        if (state.previousTokenAdded != ENCLOSURE.INSTANCE) {
            state.yieldField();
        }
        state.yield(DELIMITER.INSTANCE);
        state.clearEolBuffer(true);
    }

    private void handleEnclosureOpen(TokenizerState state, char c) {
        state.yield(ENCLOSURE.INSTANCE);
        state.enclosed = true;
        state.previousTokenAdded = ENCLOSURE.INSTANCE;
        state.clearEolBuffer(true);
    }

    private void handleCharacter(TokenizerState state, char c) {
        state.appendChar(c);
        state.clearEolBuffer(true);
    }

    private Token[] handleEndOfFile(TokenizerState state) {
        if (state.dataBuffer.length() > 0) {
            state.tokenBuffer.add(new DATA(state.dataBuffer));
        }
        state.tokenBuffer.add(EOF.INSTANCE);
        return state.tokenBuffer.toArray(new Token[0]);
    }

    private class TokenizerState {
        private boolean enclosed = false;
        private boolean escaped = false;
        private Token previousTokenAdded = null;
        private StringBuilder eolBuffer;
        private StringBuilder dataBuffer;
        private final Queue<Token> tokenBuffer;

        private TokenizerState() {
            this.eolBuffer = new StringBuilder(eol.length());
            this.dataBuffer = new StringBuilder(averageFieldCharCount);
            this.tokenBuffer = new ArrayDeque<>(tokenQueueMinCapacity);
        }

        private void appendChar(char c) {
            dataBuffer.append(c);
        }

        private void appendCharEOL(char c) {
            eolBuffer.append(c);
        }

        private void clearEolBuffer(boolean appendToFieldBuffer) {
            if (eolBuffer.length() > 0) {
                if (appendToFieldBuffer) {
                    dataBuffer.append(eolBuffer);
                }
                eolBuffer = new StringBuilder(eol.length());
            }
        }

        private void clearDataBuffer() {
            dataBuffer = new StringBuilder(averageFieldCharCount);
        }

        private void yield(Token token) {
            tokenBuffer.add(token);
            previousTokenAdded = token;
        }

        private void yieldField() {
            if (dataBuffer.length() == 0) {
                tokenBuffer.add(EMPTYDATA.INSTANCE);
                previousTokenAdded = EMPTYDATA.INSTANCE;
            } else {
                DATA dataToken = new DATA(dataBuffer);
                tokenBuffer.add(dataToken);
                previousTokenAdded = EMPTYDATA.INSTANCE;
                clearDataBuffer();
            }
        }

        public Token[] getTokens() {
            return tokenBuffer.toArray(new Token[0]);
        }
    }


}
