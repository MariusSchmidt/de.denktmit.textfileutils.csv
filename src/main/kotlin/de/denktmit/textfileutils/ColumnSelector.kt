package de.denktmit.textfileutils

interface ColumnSelector {
    fun keepField(fieldIndex: Int): Boolean
}

object KeepAll: ColumnSelector {
    override fun keepField(fieldIndex: Int): Boolean = true
}

class Keep(private vararg val indices: Int): ColumnSelector {
    override fun keepField(fieldIndex: Int): Boolean = indices.contains(fieldIndex)
}

class Drop(private vararg val indices: Int): ColumnSelector {
    override fun keepField(fieldIndex: Int): Boolean = !indices.contains(fieldIndex)
}