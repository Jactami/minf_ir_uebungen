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
                            File("./docker/task1/ES_Angabe/mapping.json"),
                            File("./docker/task1/ES_Angabe/shakespeare.json")
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
            val expected = Hash.create(5, 38, 57, 93, 74, -5, 94, 74, 125, 44, -51, -43, 37, -42, -37, -72, 14, -15, -119, 98, -72, 53, -110, 69, -77, 106, -90, 37, 100, 60, -121, -56)
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
                val expected = Hash.create(-70, -20, 33, -28, -13, -11, 21, -81, 116, -54, 67, -73, -56, 1, 60, 91, -62, -16, 63, -123, 50, -80, 126, 24, -75, 81, 90, -11, 96, -86, -3, -115)
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
                val expected = Hash.create(-32, 49, 23, -49, 23, -5, 62, 5, -10, 47, -31, -80, 109, -46, -110, 15, -68, 108, -11, -109, -54, 6, -2, -93, 88, -16, 3, -98, -30, 52, -42, -96)
                val studentHash = pathToHandInExcel.readValueOf("3) NDCG"){
                    val toLoad = from(43,2).to(43, 8)
                    val callsString = toLoad.cellsNotNull
                            .map { it.stringCellValue }
                            .toList()
                    println(callsString.joinToString())
                    hash { updateWithStrings(callsString) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }
        }

        "Task 4" asTest {
            val expected = Hash.create(-57, 34, -93, -4, 126, 71, 5, 56, -18, 71, -58, 93, -118, -68, 30, -67, -40, -23, -111, -70, -125, 2, -41, -27, 48, -112, 24, -64, -54, -20, -105, 109)
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
            val expected = Hash.create(27, -21, -50, 52, 121, 117, -22, -6, -82, 114, 4, 110, -55, 26, 105, 24, 117, -86, -55, 11, 24, 18, -40, 60, 84, 119, 104, 91, -31, 73, 119, 54)
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
                val expected = Hash.create(-19, -76, 55, -20, -49, -37, 85, -19, 67, 31, 98, 64, -4, -97, 50, -56, 9, 84, -47, 19, 87, 53, -59, 61, -64, -70, 94, -76, -108, 111, -86, -117)
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
                val expected = Hash.create(29, 114, 102, -17, -112, 78, 126, -12, -56, -119, -34, 74, 97, 78, -72, 86, -30, 43, 45, -17, 37, 83, -107, 62, 0, 122, -56, 87, 127, 121, -123, 89)
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
                val expected = Hash.create(29, 114, 102, -17, -112, 78, 126, -12, -56, -119, -34, 74, 97, 78, -72, 86, -30, 43, 45, -17, 37, 83, -107, 62, 0, 122, -56, 87, 127, 121, -123, 89)
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
                    "anfrage2" to Hash.create(-25, 12, -20, -4, 37, -99, 10, -65, -88, -51, -61, -92, 4, 42, 111, -80, 94, -36, 123, -43, -99, -59, 42, -73, -85, 52, -43, -128, -106, -8, 78, -58),
                    "anfrage3" to Hash.create(-53, -46, 57, -43, 79, 108, 57, 85, 32, -119, -61, 69, -83, 99, 71, -7, 11, 34, 15, -100, -48, 115, 58, 38, 78, 47, 66, -83, -66, 12, 18, -76),
                    "anfrage4" to Hash.create(-100, 34, 100, -1, -49, -85, -96, 38, 103, 44, -20, 75, -20, -34, -1, -53, 22, 62, -120, 70, -60, 45, -103, 49, -19, 35, -9, 32, 52, -56, 16, -45),
                    "anfrage5" to Hash.create(-99, 97, -116, -118, -104, -121, -53, -59, -15, -13, 95, 112, 74, -5, 37, 78, 57, 43, -25, 6, 76, 73, -109, -79, 104, 46, 77, -117, -88, -18, -66, -75),
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