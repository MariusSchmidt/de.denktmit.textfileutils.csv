package de.denktmit.textfileutils

import java.io.File

object FileHelper {

    fun testfile(day: Int): File = File("src/test/resources/${dayPadded(day)}-sample.txt")
    fun datafile(day: Int): File = File("src/main/resources/${dayPadded(day)}.txt")
    private fun dayPadded(day: Int) = day.toString().padStart(2, '0')
}