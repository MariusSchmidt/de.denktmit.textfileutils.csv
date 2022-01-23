package de.denktmit.textfileutils

sealed interface Token
open class DATA(val charSequence: CharSequence) : Token {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DATA) return false

        if (charSequence != other.charSequence) return false

        return true
    }

    override fun hashCode(): Int {
        return charSequence.hashCode()
    }

    override fun toString(): String {
        return "FIELD(charSequence=$charSequence)"
    }
}
object EMPTYDATA: DATA("") {
    override fun toString(): String = EMPTYDATA::class.simpleName!!
}
object DELIMITER : Token
object ENCLOSURE : Token
object EOL : Token
object EOF : Token