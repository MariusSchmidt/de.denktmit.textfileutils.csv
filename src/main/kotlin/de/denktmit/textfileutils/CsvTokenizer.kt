package de.denktmit.textfileutils

internal class CsvTokenizer(
    private val delimiter: Char = ',',
    private val eol: String = System.lineSeparator(),
    private val enclosure: Char? = '"',
    private val escapeSign: Char? = '\\',
    private val averageFieldCharCount: Int = 50
) {

    fun tokenize(characters: Sequence<Char>): Sequence<Token> {
        val tokenizerState = TokenizerState(eol, averageFieldCharCount)
        return characters.flatMap { char ->
            handleEachCharacter(tokenizerState, char)
        } + sequence { handleEndOfFile(tokenizerState) }
    }

    private fun handleEachCharacter(
        state: TokenizerState,
        c: Char
    ): Sequence<Token> {
        return sequence {
            when {
                state.enclosed && state.escaped -> handleEscapedChar(state, c)
                state.enclosed && c == escapeSign -> handleEscapeNext(state, c)
                state.enclosed && c == enclosure -> handleEnclosureClose(state)
                state.enclosed -> state.dataBuffer.append(c)
                c == eol[state.eolBuffer.length] -> handleEndOfLine(state, c)
                c == delimiter -> handleDelimiter(state)
                c == enclosure && state.dataBuffer.isEmpty() -> handleEnclosureOpen(state)
                else -> handleCharacter(state, c)
            }
        }
    }

    private fun handleEscapedChar(state: TokenizerState, c: Char) {
        state.dataBuffer.append(c)
        state.escaped = false
    }

    private fun handleEscapeNext(state: TokenizerState, c: Char) {
        state.dataBuffer.append(c)
        state.escaped = true
    }

    private suspend fun SequenceScope<Token>.handleEnclosureClose(state: TokenizerState) {
        yieldField(state)
        yield(ENCLOSURE)
        state.enclosed = false
        state.previousTokenAdded = ENCLOSURE
    }

    private suspend fun SequenceScope<Token>.handleEndOfLine(state: TokenizerState, c: Char) {
        state.eolBuffer.append(c)
        if (state.eolBuffer.length == eol.length) {
            if (state.previousTokenAdded != ENCLOSURE) {
                yieldField(state)
            }
            yield(EOL)
            state.previousTokenAdded = EOL
            clearEolBuffer(state, false)
        }
    }

    private suspend fun SequenceScope<Token>.handleDelimiter(state: TokenizerState) {
        if (state.previousTokenAdded != ENCLOSURE) {
            yieldField(state)
        }
        yield(DELIMITER)
        state.previousTokenAdded = DELIMITER
        clearEolBuffer(state, true)
    }

    private suspend fun SequenceScope<Token>.handleEnclosureOpen(state: TokenizerState) {
        yield(ENCLOSURE)
        state.enclosed = true;
        state.previousTokenAdded = ENCLOSURE
        clearEolBuffer(state, true)
    }

    private fun handleCharacter(state: TokenizerState, c: Char) {
        state.dataBuffer.append(c)
        clearEolBuffer(state, true)
    }

    private suspend fun SequenceScope<Token>.handleEndOfFile(state: TokenizerState) {
        clearEolBuffer(state, true)
        if (state.dataBuffer.isNotEmpty() || state.previousTokenAdded == DELIMITER) {
            yieldField(state)
        }
        yield(EOF)
    }

    private fun clearEolBuffer(state: TokenizerState, appendToFieldBuffer: Boolean) {
        if (state.eolBuffer.isNotEmpty()) {
            if (appendToFieldBuffer) {
                state.dataBuffer.append(state.eolBuffer)
            }
            state.eolBuffer.clear()
        }
    }

    private suspend fun SequenceScope<Token>.yieldField(state: TokenizerState) {
        if (state.dataBuffer.isEmpty()) {
            yield(EMPTYDATA)
        } else {
            yield(DATA(state.dataBuffer.toString()))
            state.dataBuffer.clear()
        }
    }

    private class TokenizerState(
        eol: String,
        averageFieldCharCount: Int,
    ) {
        var enclosed: Boolean = false
        var escaped: Boolean = false
        var previousTokenAdded: Token? = null
        val dataBuffer: StringBuilder = StringBuilder(averageFieldCharCount)
        val eolBuffer: StringBuilder = StringBuilder(eol.length)
    }

}