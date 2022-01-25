package de.denktmit.textfileutils

sealed interface Token2

open class DATA2(
    val chars: CharArray,
    val rowIndex: Int,
    val columnIndex: Int,
    val startIndex: Int,
    val endIndex: Int
) : Token2 {
    fun isEmpty(): Boolean = startIndex == endIndex
    fun value(): String = chars.sliceArray(startIndex until endIndex).joinToString("")
}

object DELIMITER2 : Token2
object ENCLOSURE2 : Token2
object EOL2 : Token2
object EOF2 : Token2