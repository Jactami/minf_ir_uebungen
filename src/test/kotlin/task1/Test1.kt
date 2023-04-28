/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task1

import de.fengl.ktestfactories.DynamicTestDefinitionRoot
import exercise1.task7.ESIndexSession
import kotlinx.coroutines.*
import misc.*
import org.junit.jupiter.api.*
import toolkit.*
import java.io.File

/**
 * Für das 1te Übungsblatt. 
 */

@de.fengl.ktestfactories.KTestDisplayNames("Tests for 1")
class TestsFor1 : de.fengl.ktestfactories.KTestFactory() {

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


    override fun DynamicTestDefinitionRoot.init() {
        "Task 1" asTest {
            val expected = Hash.create(-117, 26, 96, 18, -49, 108, 38, -77, 124, 59, 60, -106, -95, 74, -103, -57, -126, -7, -81, -86, -121, -61, 110, -57, 106, -35, 60, 35, -75, 24, 51, 55)
            val studentHash = pathToHandInExcel.readValueOf("1) GMAP"){
                val toLoad = from(1, 5).to(1,6) and from(2, 5).to(2,6)
                val cells = toLoad.cellsNotNull
                    .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                    .toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it.convertToHashDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        val nNDCG = 11

        "Task 3" asGroup {
            "Werte" asTest {
                val expected = Hash.create(-21, -33, -122, -113, 34, 39, -95, -40, -122, 95, 107, 112, 47, -21, 26, -112, 14, -5, -59, 98, -84, 42, -2, 107, -112, 73, 107, -19, 25, 98, 87, 125)
                val studentHash = pathToHandInExcel.readValueOf("3) NDCG"){
                    val toLoad = from(35, 2).to(37, 2+nNDCG) and from(40, 2).to(41, 2+nNDCG)
                    val cells = toLoad.cellsNotNull
                        .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                        .toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it.convertToHashDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

            "Evaluation" asTest {
                val expected = Hash.create(-50, 52, -22, -3, 45, -72, -96, 99, 71, 124, 99, 29, 52, 77, -117, 18, 27, -101, 20, 119, -74, -82, -17, 95, -22, -127, 49, -73, 91, -31, 45, -111)
                val studentHash = pathToHandInExcel.readValueOf("3) NDCG"){
                    val toLoad = from(43,2).to(43, 2+nNDCG)
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
            val expected = Hash.create(101, 14, -48, -128, 29, -9, 47, -21, 18, -35, -21, 15, 83, 75, -19, -62, -124, 113, -23, 42, -77, -5, 99, 38, 40, -37, 4, 26, -20, -107, -55, -66)
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
            val expected = Hash.create(93, -99, -106, -125, 58, 62, -16, 52, 92, 36, 7, 40, 23, 2, -96, -27, -120, 12, 89, 76, 60, 84, 49, -117, -122, 99, 42, -3, 105, 90, -83, 66)
            val studentHash = pathToHandInExcel.readValueOf("5) t-Test"){
                val toLoad = from(10, 1)
                val cell = toLoad.cell?.numericCellValue?.toStringForEvalWithNDigits(5) ?: fail { "The value at $toLoad was not found." }
                println(cell)
                hash { update(cell) }
            }.also { println(it.convertToHashDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        // Hole alt. lösungen
        "Task 6 (Min. 1 von 3 sollte positiv sein.)" asGroup {
            "Lucene (Bester Weg)" asTest {
                val expected = Hash.create(-37, 59, -66, 36, 43, -75, -4, 80, -54, 60, 90, -71, 17, 50, -95, 68, -83, -16, -72, 45, -46, -113, -30, 80, -57, -88, -48, 85, -92, 0, -39, -22)
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

            "Alternative 1" asTest {
                val expected = Hash.create(-37, 59, -66, 36, 43, -75, -4, 80, -54, 60, 90, -71, 17, 50, -95, 68, -83, -16, -72, 45, -46, -113, -30, 80, -57, -88, -48, 85, -92, 0, -39, -22)
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

            "Alternative 2" asTest {
                val expected = Hash.create(-37, 59, -66, 36, 43, -75, -4, 80, -54, 60, 90, -71, 17, 50, -95, 68, -83, -16, -72, 45, -46, -113, -30, 80, -57, -88, -48, 85, -92, 0, -39, -22)
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
