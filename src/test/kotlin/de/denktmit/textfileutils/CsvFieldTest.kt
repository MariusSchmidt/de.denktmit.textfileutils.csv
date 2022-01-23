package de.denktmit.textfileutils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CsvFieldTest {

    @Test
    fun charsNotEnclosed() {
        val dataAsString = CsvField("Some Data", EnclosureType.NOT_ENCLOSED)
            .chars('\"').fold("") {acc, c -> acc + c }
        assertThat(dataAsString).isEqualTo("Some Data")
    }

    @Test
    fun charsEnclosed() {
        val dataAsString = CsvField("Some Data", EnclosureType.ENCLOSED)
            .chars('\"').fold("") {acc, c -> acc + c }
        assertThat(dataAsString).isEqualTo("\"Some Data\"")
    }
}