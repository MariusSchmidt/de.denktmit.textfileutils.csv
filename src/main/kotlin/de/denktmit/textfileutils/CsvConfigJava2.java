package de.denktmit.textfileutils;

import org.jetbrains.annotations.NotNull;

public class CsvConfigJava2 {

    private final char delimiter;
    private final String eol ;
    private final char enclosure;
    private final char escapeSign;
    private final int averageFieldCharCount;
    private final int tokenQueueMinCapacity;


    private CsvConfigJava2(char delimiter, String eol, char enclosure, char escapeSign, int averageFieldCharCount, int tokenQueueMinCapacity) {
        this.delimiter = delimiter;
        this.eol = eol;
        this.enclosure = enclosure;
        this.escapeSign = escapeSign;
        this.averageFieldCharCount = averageFieldCharCount;
        this.tokenQueueMinCapacity = tokenQueueMinCapacity;
    }

    public class Builder {
        private char delimiter = ',';
        private String eol = "\n";
        private char enclosure = '"';
        private char escapeSign = '\\';
        private int averageFieldCharCount = 100;
        private int tokenQueueMinCapacity = 100;

        public CsvConfigJava2 build() {
            return new CsvConfigJava2(delimiter, eol, enclosure, escapeSign, averageFieldCharCount, tokenQueueMinCapacity);
        }

        public char getDelimiter() {
            return delimiter;
        }

        public void setDelimiter(char delimiter) {
            this.delimiter = delimiter;
        }

        public String getEol() {
            return eol;
        }

        public void setEol(String eol) {
            this.eol = eol;
        }

        public char getEnclosure() {
            return enclosure;
        }

        public void setEnclosure(char enclosure) {
            this.enclosure = enclosure;
        }

        public char getEscapeSign() {
            return escapeSign;
        }

        public void setEscapeSign(char escapeSign) {
            this.escapeSign = escapeSign;
        }

        public int getAverageFieldCharCount() {
            return averageFieldCharCount;
        }

        public void setAverageFieldCharCount(int averageFieldCharCount) {
            this.averageFieldCharCount = averageFieldCharCount;
        }

        public int getTokenQueueMinCapacity() {
            return tokenQueueMinCapacity;
        }

        public void setTokenQueueMinCapacity(int tokenQueueMinCapacity) {
            this.tokenQueueMinCapacity = tokenQueueMinCapacity;
        }
    }
}
