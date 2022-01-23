package de.denktmit.textfileutils

class RowAssembler(
    private val columnSelector: ColumnSelector = KeepAll
) {

    fun toRows(tokens: Sequence<Token>): Sequence<CsvRow> {
        val rowAssemblerState = RowAssemblerState(columnSelector)
        return tokens.flatMap { token ->
            handleEachToken(rowAssemblerState, token)
        }
    }

    private fun handleEachToken(
        rowAssemblerState: RowAssemblerState,
        token: Token
    ): Sequence<CsvRow> {
        return sequence {
            when(token) {
                DELIMITER -> handleDelimiter(rowAssemblerState)
                ENCLOSURE -> handleEnclosure(rowAssemblerState)
                is DATA -> handleData(rowAssemblerState, token)
                EOL, EOF -> handleEndOfLine(rowAssemblerState)
            }
        }
    }

    private fun handleDelimiter(rowAssemblerState: RowAssemblerState) {
        rowAssemblerState.nextFieldEnclosureType = EnclosureType.NOT_ENCLOSED
    }

    private fun handleEnclosure(rowAssemblerState: RowAssemblerState) {
        rowAssemblerState.nextFieldEnclosureType = EnclosureType.ENCLOSED
    }

    private fun handleData(assemblerState: RowAssemblerState, token: DATA) {
        if (assemblerState.columnSelector.keepField(assemblerState.currentRowsFieldTokenCount)) {
            assemblerState.fieldsToKeep.add(CsvField(token.charSequence, assemblerState.nextFieldEnclosureType))
        }
        assemblerState.currentRowsFieldTokenCount++
    }

    private suspend fun SequenceScope<CsvRow>.handleEndOfLine(assemblerState: RowAssemblerState) {
        if (assemblerState.fieldsToKeep.isNotEmpty()) {
            yield(CsvRow(assemblerState.fieldsToKeep.toList()))
            assemblerState.fieldsToKeep.clear()
        }
        assemblerState.currentRowsFieldTokenCount = 0
    }

    private class RowAssemblerState(
        val columnSelector: ColumnSelector
    ) {
        val fieldsToKeep: MutableList<CsvField> = mutableListOf()
        var currentRowsFieldTokenCount = 0
        var nextFieldEnclosureType: EnclosureType = EnclosureType.NOT_ENCLOSED
    }

}