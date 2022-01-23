package de.denktmit.textfileutils

import de.denktmit.textfileutils.EnclosureType.ENCLOSED
import de.denktmit.textfileutils.EnclosureType.NOT_ENCLOSED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class RowAssemblerTest {

    @Test
    fun keepAllFields() {
        val tokens = sequenceOf(
            // First row
            ENCLOSURE, DATA("Row"), ENCLOSURE, DELIMITER,
            ENCLOSURE, DATA("No"), ENCLOSURE, DELIMITER,
            DATA("1"), EOL,
            // Second row
            DATA("Second"), DELIMITER,
            DATA("Row"), DELIMITER,
            EMPTYDATA, EOL,
            // Third row
            DATA("Third"), DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"), DELIMITER,
            ENCLOSURE, DATA("4th"), ENCLOSURE, DELIMITER,
            DATA("Row"), EOL, EOF
        )
        val lines = RowAssembler().toRows(tokens).toList()
        assertThat(lines).containsExactly(
            CsvRow(listOf(CsvField("Row", ENCLOSED), CsvField("No", ENCLOSED), CsvField("1", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Second", NOT_ENCLOSED), CsvField("Row", NOT_ENCLOSED), CsvField("", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Third", NOT_ENCLOSED), CsvField("Row", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("The", NOT_ENCLOSED), CsvField("4th", ENCLOSED), CsvField("Row", NOT_ENCLOSED)))
        )
    }

    @Test
    fun keepFirstAndThird() {
        val tokens = sequenceOf(
            // First row
            ENCLOSURE, DATA("Row"), ENCLOSURE, DELIMITER,
            ENCLOSURE, DATA("No"), ENCLOSURE, DELIMITER,
            DATA("1"), EOL,
            // Second row
            DATA("Second"), DELIMITER,
            DATA("Row"), DELIMITER,
            EMPTYDATA, EOL,
            // Third row
            DATA("Third"), DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"), DELIMITER,
            ENCLOSURE, DATA("4th"), ENCLOSURE, DELIMITER,
            DATA("Row"), EOL, EOF
        )
        val lines = RowAssembler(Keep(0,2)).toRows(tokens).toList()
        assertThat(lines).containsExactly(
            CsvRow(listOf(CsvField("Row", ENCLOSED), CsvField("1", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Second", NOT_ENCLOSED), CsvField("", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Third", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("The", NOT_ENCLOSED), CsvField("Row", NOT_ENCLOSED)))
        )
    }

    @Test
    fun dropFirstAndThird() {
        val tokens = sequenceOf(
            // First row
            ENCLOSURE, DATA("Row"), ENCLOSURE, DELIMITER,
            ENCLOSURE, DATA("No"), ENCLOSURE, DELIMITER,
            DATA("1"), EOL,
            // Second row
            DATA("Second"), DELIMITER,
            DATA("Row"), DELIMITER,
            EMPTYDATA, EOL,
            // Third row
            DATA("Third"), DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"), DELIMITER,
            ENCLOSURE, DATA("4th"), ENCLOSURE, DELIMITER,
            DATA("Row"), EOL, EOF
        )
        val lines = RowAssembler(Drop(0,2)).toRows(tokens).toList()
        assertThat(lines).containsExactly(
            CsvRow(listOf(CsvField("No", ENCLOSED))),
            CsvRow(listOf(CsvField("Row", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Row", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("4th", ENCLOSED)))
        )
    }

    @Test
    fun dropFirstAndSecond() {
        val tokens = sequenceOf(
            // First row
            ENCLOSURE, DATA("Row"), ENCLOSURE, DELIMITER,
            ENCLOSURE, DATA("No"), ENCLOSURE, DELIMITER,
            DATA("1"), EOL,
            // Second row
            DATA("Second"), DELIMITER,
            DATA("Row"), DELIMITER,
            EMPTYDATA, EOL,
            // Third row
            DATA("Third"), DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"), DELIMITER,
            ENCLOSURE, DATA("4th"), ENCLOSURE, DELIMITER,
            DATA("Row"), EOL, EOF
        )
        val lines = RowAssembler(Drop(0,1)).toRows(tokens).toList()
        assertThat(lines).containsExactly(
            CsvRow(listOf(CsvField("1", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Row", NOT_ENCLOSED)))
        )
    }

    @Test
    fun dropSecondAndThird() {
        val tokens = sequenceOf(
            // First row
            ENCLOSURE, DATA("Row"), ENCLOSURE, DELIMITER,
            ENCLOSURE, DATA("No"), ENCLOSURE, DELIMITER,
            DATA("1"), EOL,
            // Second row
            DATA("Second"), DELIMITER,
            DATA("Row"), DELIMITER,
            EMPTYDATA, EOL,
            // Third row
            DATA("Third"), DELIMITER,
            DATA("Row"),
            EOL,
            // Fourth row
            DATA("The"), DELIMITER,
            ENCLOSURE, DATA("4th"), ENCLOSURE, DELIMITER,
            DATA("Row"), EOL, EOF
        )
        val lines = RowAssembler(Drop(1,2)).toRows(tokens).toList()
        assertThat(lines).containsExactly(
            CsvRow(listOf(CsvField("Row", ENCLOSED))),
            CsvRow(listOf(CsvField("Second", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("Third", NOT_ENCLOSED))),
            CsvRow(listOf(CsvField("The", NOT_ENCLOSED)))
        )
    }
}