/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task1

import exercise1.task7.ESIndexSession
import kotlinx.coroutines.*
import misc.*
import org.junit.jupiter.api.*
import toolkit.*
import java.io.File

@DisplayName("Tests for 1")
class TestsFor1 {

    val config = File("./cfg/test1.json").loadConfigOrFail(::Test1Config)

    // The path to your excel, this is a getter-function to ensure that you can run tests even without the file.
    val pathToHandInExcel: File = File(config.pathToHandInExcel)

    // The path where you store your anfrageX.json files
    val pathToHandIn: File =  File(config.pathToHandIn)

    // Set true to reset the index
    val resetIndex: Boolean = config.resetIndex

    // Configure your elasic search information, usually the data bellow
    val indexName: String = config.indexName
    val host: String = config.host
    val port: UShort = config.port

    // The session used for this unit tests
    private val esSession = ESIndexSession(indexName, host, port)

    init {
        try {
            runBlocking {
                val alreadyExists = esSession.exists()

                if (resetIndex || !alreadyExists){
                    if (resetIndex && alreadyExists){
                        println("The index already exists, delete it for reset.")
                        esSession.delete()
                    }
                    print("Start creating the index... ")
                    esSession.create(
                            File(config.pathToHandIn, "mapping.json"),
                            File(config.pathToHandIn, "shakespeare_6.0.json")
                    )
                    println("DONE")
                }
            }
        } catch (e: java.net.ConnectException){
            // Ignore it if it's not running but warn.
            System.err.println("ERROR: Your elastic search is not running on: ${esSession.baseUrl}")
        }


        try {
            require(pathToHandInExcel.isFile){
                "The path ${pathToHandInExcel.canonicalPath} does not point to a file."
            }
            require(pathToHandInExcel.extension in listOf("xlsx", "xlsm")){
                "The path ${pathToHandInExcel.canonicalPath} does not point to a .xlsx or .xlsm file."
            }
        } catch (e: NotImplementedError){
            // Ignore that, only fail early when something is awry
        }

        require(pathToHandIn.isDirectory){
            "The path ${pathToHandIn.canonicalPath} does to point to a folder."
        }
    }

    @TestFactory
    @DisplayName("Abgabe 1")
    fun tasks() = testFactoryDefinition {

        "Task 1" asTest {
            val expected = Hash.create(103, 22, -121, 118, -105, 68, -5, 63, 10, 114, -127, 11, -6, 91, -125, 18, 91, 22, 35, -81, 5, 12, 47, 57, 59, 126, -17, 30, -28, 13, 65, 112)
            val studentHash = pathToHandInExcel.readValueOf("1) GMAP"){
                val toLoad = from(8, 1).to(8,2) and from(10, 1).to(10,2)
                val cells = toLoad.cellsNotNull
                        .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                        .toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it.convertToHashDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        "Task 3" asGroup {
            "Werte" asTest {
                val expected = Hash.create(99, -105, 1, -6, -109, -19, -84, -51, 93, 12, -8, -5, 85, -113, 44, 20, -62, -49, 18, 6, -95, -61, -122, 54, 69, 117, 58, -22, -5, 64, -80, 46)
                val studentHash = pathToHandInExcel.readValueOf("3) NDCG"){
                    val toLoad = from(35, 2).to(37, 8) and from(40, 2).to(41, 8)
                    val cells = toLoad.cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

            "Evaluation" asTest {
                val expected = Hash.create(-3, 10, -43, 28, 14, -53, -128, 28, 111, 121, -46, -16, 85, 54, -18, -123, -91, 47, 22, -73, -127, -3, 28, -78, 45, -26, 106, -68, -104, 72, -39, 46)
                val studentHash = pathToHandInExcel.readValueOf("3) NDCG"){
                    val toLoad = from(43,2).to(43, 8)
                    val callsString = toLoad.cellsNotNull
                            .map { it.stringCellValue.lowercase() }
                            .toList()
                    println(callsString.joinToString())
                    hash { updateWithStrings(callsString) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }
        }

        "Task 4" asTest {
            val expected = Hash.create(114, 58, -48, -75, -86, -18, 24, -123, 41, 83, -109, -86, 44, 55, 22, -100, -85, 90, 40, 94, -57, -88, -74, -76, -65, 62, 64, -12, 106, -61, -98, 9)
            val studentHash = pathToHandInExcel.readValueOf("4) Ranking"){
                val toLoad = from(12,1).to(13, 3)
                val cells = toLoad.cellsNotNull
                        .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                        .toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it.convertToHashDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        "Task 5" asTest {
            val expected = Hash.create(-61, -62, -29, 23, -18, -101, -42, 64, -46, 112, 77, 81, 127, 44, 114, 19, -50, 92, -3, 42, -86, -42, 60, -88, 106, 35, 37, -21, -54, 36, 6, 64)
            val studentHash = pathToHandInExcel.readValueOf("5) t-Test"){
                val toLoad = from(10, 1)
                val cell = toLoad.cell?.numericCellValue?.toStringForEvalWithNDigits(5) ?: fail { "The value at $toLoad was not found." }
                println(cell)
                hash { update(cell) }
            }.also { println(it.convertToHashDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        // Hole alt. lösungen
        "Task 6" asGroup {
            "lucene" asTest {
                val expected = Hash.create(-51, 42, -41, 93, 83, -65, -52, 30, 47, 69, 62, -53, -62, -3, -31, 47, -67, 66, 9, -30, 7, -108, 84, -29, -25, -34, 81, 22, -62, 110, -76, 53)
                val studentHash = pathToHandInExcel.readValueOf("6) VSM"){
                    val toLoad = from(20,1).to(21, 1)
                    val cells = toLoad.cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

            "alt_1" asTest {
                val expected = Hash.create(-120, -41, 45, -48, -58, 6, -23, 34, -93, 22, 25, -22, 67, 70, 63, 121, -106, 24, 91, -110, 25, 98, 69, 2, 52, -79, 54, -55, 4, -70, 1, 45)
                val studentHash = pathToHandInExcel.readValueOf("6) VSM"){
                    val toLoad = from(20,1).to(21, 1)
                    val cells = toLoad.cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

            "alt_2" asTest {
                val expected = Hash.create(-120, -41, 45, -48, -58, 6, -23, 34, -93, 22, 25, -22, 67, 70, 63, 121, -106, 24, 91, -110, 25, 98, 69, 2, 52, -79, 54, -55, 4, -70, 1, 45)
                val studentHash = pathToHandInExcel.readValueOf("6) VSM"){
                    val toLoad = from(20,1).to(21, 1)
                    val cells = toLoad.cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

        }
        


        "Task 7" asGroup {
            val results = mapOf(
                    "anfrage1" to Hash.create(43, 55, -14, -108, -16, -1, 38, 46, 79, 81, 94, 41, -55, -40, 125, 56, -81, -96, 71, 45, -4, 43, 56, -73, -65, -123, -63, 13, 63, -8, -68, -93),
                    "anfrage2" to Hash.create(-79, -102, -108, -32, 88, -51, -110, -103, -65, -111, 1, 119, -35, 122, 25, 95, -62, 14, 114, -88, -34, 38, 98, -65, 14, 86, 119, -34, 31, -40, 53, -31),
                    "anfrage3" to Hash.create(41, 40, 78, -31, -70, -92, 80, 54, -3, -73, 111, 36, 57, -65, 18, 37, 52, -7, 37, 8, -10, -95, 79, -104, -95, 52, 39, -62, 94, -67, -89, 14),
                    "anfrage4" to Hash.create(32, 118, 58, 76, 84, -112, 89, 73, 86, 123, -108, 56, -20, 7, -33, -37, 85, -121, 54, -34, -23, 21, -124, -43, 3, 60, 29, -103, 51, 54, -60, -93),
                    "anfrage5" to Hash.create(-43, -36, 88, 56, -119, -84, 49, 98, -113, 11, 52, 69, -16, -15, 13, 3, -69, -75, 76, -26, 91, -46, 95, -86, 57, -51, 19, -46, -51, 69, -46, -81),
            )

            pathToHandIn.walkTopDown().filter { it.extension == "json" && "anfrage" in it.nameWithoutExtension }.forEach { file ->
                file.name asTest {
                    assertDoesNotThrow({"Your elastic search is not running on: ${esSession.baseUrl}"}) { runBlocking { esSession.exists() } }
                    val expected = results.getValue(file.nameWithoutExtension)
                    val student = hash { update(runBlocking { esSession.query(file) }) }.also { println(it.convertToHashDeclaration()) }
                    assertHashEquals(expected, student)
                }
            }
        }
    }

}