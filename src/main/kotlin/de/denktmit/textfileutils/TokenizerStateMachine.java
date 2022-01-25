package de.denktmit.textfileutils;

import java.util.ArrayDeque;
import java.util.Queue;

public class TokenizerStateMachine {

    private final CsvConfigJava2 config;
    private final Queue<Token> tokenBuffer = new ArrayDeque<>();
    private StringBuilder eolBuffer;
    private TokenizerState state;
    private char[] chars;

    private TokenizerStateMachine(CsvConfigJava2 config) {
        this.config = config;
    }



    private interface TokenizerState {
        TokenizerState transition(char c);
        TokenizerState endOfFile();
    }

    private static final class FIELD_OPENED implements TokenizerState {

        private static final TokenizerState INSTANCE = new FIELD_OPENED();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class FIELD_CLOSED implements TokenizerState {

        private static final TokenizerState INSTANCE = new FIELD_CLOSED();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class ESCAPED implements TokenizerState {

        private static final TokenizerState INSTANCE = new ESCAPED();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class EOL_MATCHING implements TokenizerState {

        private static final TokenizerState INSTANCE = new EOL_MATCHING();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class ENCLOSURE_OPEN implements TokenizerState {

        private static final TokenizerState INSTANCE = new ENCLOSURE_OPEN();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class ENCLOSURE_OPEN_ESCAPED implements TokenizerState {

        private static final TokenizerState INSTANCE = new ENCLOSURE_OPEN_ESCAPED();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class ENCLOSURE_CLOSED implements TokenizerState {

        private static final TokenizerState INSTANCE = new ENCLOSURE_CLOSED();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class ENCLOSURE_CLOSED_EOL_MATCHING implements TokenizerState {

        private static final TokenizerState INSTANCE = new ENCLOSURE_CLOSED_EOL_MATCHING();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }

    private static final class END_OF_FILE implements TokenizerState {

        private static final TokenizerState INSTANCE = new END_OF_FILE();

        @Override
        public TokenizerState transition(char c) {
            return null;
        }

        @Override
        public TokenizerState endOfFile() {
            return null;
        }
    }


}

//enum ParserState {
//    FIELD_OPENED,
//    FIELD_CLOSED,
//    ESCAPED,
//    EOL_MATCHING,
//    ENCLOSURE_OPEN,
//    ENCLOSURE_OPEN_ESCAPED,
//    ENCLOSURE_CLOSED,
//    ENCLOSURE_CLOSED_EOL_MATCHING,
//    EOF
//}