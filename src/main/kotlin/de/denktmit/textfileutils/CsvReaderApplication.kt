package de.denktmit.textfileutils

class CsvReaderApplication {
        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                val reader = ChannelCharacterInputReader()
                val parser = CsvTokenizer()
                val writer = ChannelCharachterOutputWriter()
                val characterChunks = reader.read(System.`in`)
                val csvRow = parser.tokenize(characterChunks)
                //writer.write(System.out, csvRow)
            }
        }
}