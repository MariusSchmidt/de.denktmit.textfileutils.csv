package de.denktmit.textfileutils

class CsvRow(val fields: List<CsvField>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CsvRow) return false

        if (fields != other.fields) return false

        return true
    }

    override fun hashCode(): Int {
        return fields.hashCode()
    }

    override fun toString(): String {
        return "CsvLine(fields=$fields)"
    }

    fun chars(delimiter: Char, eol: String, enclosure: Char?): Sequence<Char> {
        return sequence {
            for (field in fields) {
                yieldAll(field.chars(enclosure))
                if (field != fields.last()) {
                    yield(delimiter)
                } else {
                    yieldAll(eol.asSequence())
                }
            }
        }
    }

}

class CsvField(
    val data: CharSequence,
    val enclosureType: EnclosureType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CsvField) return false

        if (data != other.data) return false
        if (enclosureType != other.enclosureType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.hashCode()
        result = 31 * result + enclosureType.hashCode()
        return result
    }

    override fun toString(): String {
        return "CsvField(data=$data, enclosureType=$enclosureType)"
    }

    fun chars(enclosure: Char?): Sequence<Char> {
        return if (enclosure != null && enclosureType == EnclosureType.ENCLOSED) {
            sequenceOf(enclosure) + data.asSequence() + sequenceOf(enclosure)
        } else {
            data.asSequence()
        }
    }

}

enum class EnclosureType {
    ENCLOSED, NOT_ENCLOSED
}