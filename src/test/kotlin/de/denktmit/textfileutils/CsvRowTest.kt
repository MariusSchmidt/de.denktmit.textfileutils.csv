package de.denktmit.textfileutils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CsvRowTest {

    @Test
    fun charsTextNumber() {
        val csvRow = CsvRow(
            listOf(
                CsvField("Some Data", EnclosureType.ENCLOSED),
                CsvField("1986", EnclosureType.NOT_ENCLOSED)
            )
        )
        val rowData = csvRow.chars(',', "\r\n", '\'').fold("") { acc, c -> acc + c }
        assertThat(rowData).isEqualTo("'Some Data',1986\r\n")
    }

    @Test
    fun charsNumberText() {
        val csvRow = CsvRow(
            listOf(
                CsvField("1986", EnclosureType.NOT_ENCLOSED),
                CsvField("Some Data", EnclosureType.ENCLOSED),
            )
        )
        val rowData = csvRow.chars(',', "\r\n", '\'').fold("") { acc, c -> acc + c }
        assertThat(rowData).isEqualTo("1986,'Some Data'\r\n")
    }

    @Test
    fun charsTextNumberEmptyNotEnclosed() {
        val csvRow = CsvRow(
            listOf(
                CsvField("Some Data", EnclosureType.ENCLOSED),
                CsvField("1986", EnclosureType.NOT_ENCLOSED),
                CsvField("", EnclosureType.NOT_ENCLOSED)
            )
        )
        val rowData = csvRow.chars(';', "\r\n", '\'').fold("") { acc, c -> acc + c }
        assertThat(rowData).isEqualTo("'Some Data';1986;\r\n")
    }

    @Test
    fun charsTextNumberEmptyEnclosed() {
        val csvRow = CsvRow(
            listOf(
                CsvField("Some Data", EnclosureType.ENCLOSED),
                CsvField("1986", EnclosureType.NOT_ENCLOSED),
                CsvField("", EnclosureType.ENCLOSED)
            )
        )
        val rowData = csvRow.chars(';', "\r\n", '\'').fold("") { acc, c -> acc + c }
        assertThat(rowData).isEqualTo("'Some Data';1986;''\r\n")
    }

}