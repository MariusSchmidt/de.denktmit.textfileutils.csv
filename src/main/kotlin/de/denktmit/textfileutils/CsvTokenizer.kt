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
        tokenizerState: TokenizerState,
        char: Char
    ): Sequence<Token> {
        return sequence {
            when {
                tokenizerState.enclosed && tokenizerState.escaped -> handleEscapedChar(tokenizerState, char)
                tokenizerState.enclosed && char == escapeSign -> handleEscapeNext(tokenizerState, char)
                tokenizerState.enclosed && char == enclosure -> handleEnclosureClose(tokenizerState)
                tokenizerState.enclosed -> tokenizerState.dataBuffer.append(char)
                char == eol[tokenizerState.eolBuffer.length] -> handleEndOfLine(tokenizerState, char)
                char == delimiter -> handleDelimiter(tokenizerState)
                char == enclosure && tokenizerState.dataBuffer.isEmpty() -> handleEnclosureOpen(tokenizerState)
                else -> handleCharacter(tokenizerState, char)
            }
        }
    }

    private fun handleEscapedChar(tokenizerState: TokenizerState, char: Char) {
        tokenizerState.dataBuffer.append(char)
        tokenizerState.escaped = false
    }

    private fun handleEscapeNext(tokenizerState: TokenizerState, char: Char) {
        tokenizerState.dataBuffer.append(char)
        tokenizerState.escaped = true
    }

    private suspend fun SequenceScope<Token>.handleEnclosureClose(tokenizerState: TokenizerState) {
        yieldField(tokenizerState)
        yield(ENCLOSURE)
        tokenizerState.enclosed = false
        tokenizerState.lastToken = ENCLOSURE
    }

    private suspend fun SequenceScope<Token>.handleEndOfLine(tokenizerState: TokenizerState, char: Char) {
        tokenizerState.eolBuffer.append(char)
        if (tokenizerState.eolBuffer.length == eol.length) {
            if (tokenizerState.lastToken != ENCLOSURE) {
                yieldField(tokenizerState)
            }
            yield(EOL)
            tokenizerState.lastToken = EOL
            clearEolBuffer(tokenizerState, false)
        }
    }

    private suspend fun SequenceScope<Token>.handleDelimiter(tokenizerState: TokenizerState) {
        if (tokenizerState.lastToken != ENCLOSURE) {
            yieldField(tokenizerState)
        }
        yield(DELIMITER)
        tokenizerState.lastToken = DELIMITER
        clearEolBuffer(tokenizerState, true)
    }

    private suspend fun SequenceScope<Token>.handleEnclosureOpen(tokenizerState: TokenizerState) {
        yield(ENCLOSURE)
        tokenizerState.enclosed = true;
        tokenizerState.lastToken = ENCLOSURE
        clearEolBuffer(tokenizerState, true)
    }

    private fun handleCharacter(tokenizerState: TokenizerState, char: Char) {
        tokenizerState.dataBuffer.append(char)
        clearEolBuffer(tokenizerState, true)
    }

    private suspend fun SequenceScope<Token>.handleEndOfFile(tokenizerState: TokenizerState) {
        clearEolBuffer(tokenizerState, true)
        if (tokenizerState.dataBuffer.isNotEmpty() || tokenizerState.lastToken == DELIMITER) {
            yieldField(tokenizerState)
        }
        yield(EOF)
    }

    private fun clearEolBuffer(tokenizerState: TokenizerState, appendToFieldBuffer: Boolean) {
        if (tokenizerState.eolBuffer.isNotEmpty()) {
            if (appendToFieldBuffer) {
                tokenizerState.dataBuffer.append(tokenizerState.eolBuffer)
            }
            tokenizerState.eolBuffer.clear()
        }
    }

    private suspend fun SequenceScope<Token>.yieldField(tokenizerState: TokenizerState) {
        if (tokenizerState.dataBuffer.isEmpty()) {
            yield(EMPTYDATA)
        } else {
            yield(DATA(tokenizerState.dataBuffer.toString()))
            tokenizerState.dataBuffer.clear()
        }
    }

    private class TokenizerState(
        eol: String,
        averageFieldCharCount: Int,
    ) {
        var enclosed: Boolean = false
        var escaped: Boolean = false
        var lastToken: Token? = null
        val dataBuffer: StringBuilder = StringBuilder(averageFieldCharCount)
        val eolBuffer: StringBuilder = StringBuilder(eol.length)
    }

}