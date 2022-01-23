package de.denktmit.textfileutils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CsvTokenizerTest {

    @Test
    fun emptyStream() {
        val csv = ""
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(EOF)
    }

    @Test
    fun linuxEOL() {
        val csv = "\n"
        val tokens = CsvTokenizer(eol = "\n").tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(EMPTYDATA, EOL, EOF)
    }

    @Test
    fun windowsEOL() {
        val csv = "\r\n"
        val tokens = CsvTokenizer(eol = "\r\n").tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(EMPTYDATA, EOL, EOF)
    }

    @Test
    fun classicMacEOL() {
        val csv = "\r"
        val tokens = CsvTokenizer(eol = "\r").tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(EMPTYDATA, EOL, EOF)
    }

    @Test
    fun singleRowIntegerLinuxEOL() {
        val csv = "0\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(DATA("0"),EOL, EOF)
    }

    @Test
    fun singleRowEnclosedTextLinuxEOL() {
        val csv = "\"Test\"\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(ENCLOSURE, DATA("Test"),ENCLOSURE,EOL, EOF)
    }

    @Test
    fun singleRowTextIntegerEmpty() {
        val csv = "\"Test\",0,\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            ENCLOSURE, DATA("Test"),ENCLOSURE,DELIMITER,
            DATA("0"),DELIMITER,
            EMPTYDATA,
            EOL, EOF
        )
    }

    @Test
    fun singleRowTextWhitespaceText() {
        val csv = "\"Test\", \"Suite\"\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            ENCLOSURE, DATA("Test"),ENCLOSURE,DELIMITER,
            DATA(" \"Suite\""),
            EOL, EOF
        )

    }

    @Test
    fun twoRows() {
        val csv = "Test\nSuite\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            DATA("Test"), EOL, DATA("Suite"), EOL, EOF
        )
    }

    @Test
    fun twoRowsSuddenEOF() {
        val csv = "Test\nSuite"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            DATA("Test"), EOL, DATA("Suite"), EOF
        )
    }

    @Test
    fun fourRowsTextWhitespaceText() {
        val csv = "\"Row\",\"No\",1\n" +
                "Second,Row,\n" +
                "Third,Row\n" +
                "The,\"4th\",Row\n"
        val tokens = CsvTokenizer(eol = "\n").tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            // First row
            ENCLOSURE, DATA("Row"),ENCLOSURE,DELIMITER,
            ENCLOSURE, DATA("No"),ENCLOSURE,DELIMITER,
            DATA("1"),EOL,
            // Second row
            DATA("Second"),DELIMITER,
            DATA("Row"),DELIMITER,
            EMPTYDATA,EOL,
            // Third row
            DATA("Third"),DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"),DELIMITER,
            ENCLOSURE, DATA("4th"),ENCLOSURE,DELIMITER,
            DATA("Row"),EOL, EOF
        )
    }

    @Test
    fun multilineField() {
        val csv = "Test\nSuite\n" +
                "\"Test\nSuite\"\n"
        val tokens = CsvTokenizer().tokenize(csv.asSequence()).toList()
        assertThat(tokens).containsExactly(
            DATA("Test"),EOL,
            DATA("Suite"),EOL,
            ENCLOSURE,DATA("Test\nSuite"),ENCLOSURE,
            EOL, EOF
        )
    }
}