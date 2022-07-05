/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package csv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.bufferedWriter


private const val BUFFER_SIZE = 128 * 1024

fun Path.readCSV(format: CSVFormat) = sequence {
    bufferedReader(bufferSize = BUFFER_SIZE, options = arrayOf(StandardOpenOption.READ)).use { reader  ->
        CSVParser(reader, format).use { csvParser ->
            yieldAll(csvParser)
        }
    }
}

fun Path.readCSVAsFlow(format: CSVFormat) = flow<CSVRecord> {
    bufferedReader(bufferSize = BUFFER_SIZE, options = arrayOf(StandardOpenOption.READ)).use { reader  ->
        CSVParser(reader, format).use { csvParser ->
            for (entry in csvParser){
                emit(entry)
            }
        }
    }
}.flowOn(Dispatchers.IO)

fun Sequence<Iterable<*>>.writeCSV(target: Path, format: CSVFormat) {
    target.bufferedWriter(bufferSize = BUFFER_SIZE, options = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)).use { writer ->
        CSVPrinter(writer, format).use { printer ->
            printer.printRecord(*format.header)
            forEach { value -> printer.printRecord(value) }
            printer.flush()
        }
    }
}

suspend fun Flow<Iterable<*>>.writeCSV(target: Path, format: CSVFormat) {
    target.bufferedWriter(bufferSize = BUFFER_SIZE, options = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)).use { writer ->
        CSVPrinter(writer, format).use { printer ->
            printer.printRecord(format.header.toList())
            collect { printer.printRecord(it) }
            printer.flush()
        }
    }
}

private val stackoverflowFormat = CSVFormat.DEFAULT.builder().apply {
    setHeader()
    setSkipHeaderRecord(true)
}.build()

fun Path.readStackoverflowCSV() = readCSV(stackoverflowFormat)


fun Path.readStackoverflowCSVAsFlow() = readCSVAsFlow(stackoverflowFormat).map(::StackOverflowEntry).buffer(10_000)


fun main() {
    runBlocking {
        Path("C:\\Users\\Felix Engl\\Downloads\\expanded_posts_backed_stackoverflow_sample.csv").readStackoverflowCSVAsFlow().take(500).map { it.toRecord() }.writeCSV(
            Path("C:\\Users\\Felix Engl\\Downloads\\expanded_posts_backed_stackoverflow_sample_small.csv"),
            CSVFormat.DEFAULT.builder().apply {
                setHeader(*StackOverflowEntry.extractor.header.toTypedArray())
                setSkipHeaderRecord(true)
            }.build()
        )
    }
}