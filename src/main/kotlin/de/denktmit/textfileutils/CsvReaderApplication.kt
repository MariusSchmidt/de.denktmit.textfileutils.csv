package de.denktmit.textfileutils

class CsvReaderApplication {
        companion object {
            @JvmStatic
            fun main(args: Array<String>) {
                val reader = ChannelCharacterInputReader()
                val tokenizer = CsvTokenizer(';', "\n")
                val rowAssembler = RowAssembler(Keep(29))
                val sequencer = CsvSequencer(';', "\n")
                val writer = ChannelCharachterOutputWriter()

                val inputCharacters = reader.read(System.`in`)
                val tokens = tokenizer.tokenize(inputCharacters)
                val rows = rowAssembler.toRows(tokens)
                val outputCharacters = sequencer.toCharSequence(rows)
                writer.write(System.out, outputCharacters)



                //writer.write(System.out, csvRow)
            }
        }
}