package de.denktmit.textfileutils

class CsvSequencer(
    private val delimiter: Char = ',',
    private val eol: String = System.lineSeparator(),
    private val enclosure: Char? = '"',
) {

    fun toCharSequence(rows: Sequence<CsvRow>): Sequence<Char> =
        rows.flatMap { row -> row.chars(delimiter, eol, enclosure) }

}