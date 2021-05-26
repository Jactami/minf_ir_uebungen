/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task2

import misc.*
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import toolkit.*
import java.io.File
import kotlin.test.assertNotNull




class Test2 {

    val config = File("./cfg/test2.json").loadConfigOrFail{
        Test2Config("TODO: Pfad zur Abgabe xlsx/xslm.")
    }

    val pathToHandIn: File = File(config.pathToHandIn)

    init {
        require(pathToHandIn.isFile){
            "The path ${pathToHandIn.canonicalPath} has to point to a file!"
        }
    }

    @TestFactory
    @DisplayName("Abgabe 2")
    fun tasks() = testFactoryDefinition {
        "Task 1" asGroup {
            task1Group(
                    "Maximum Likelihood",
                    "B12", "C12",
                    Hash.create(42, 51, -101, -8, 53, -19, -45, 72, 116, 94, 120, 19, 107, -58, -37, 30, -53, -78, 37, 9, 110, 80, 41, 10, -97, 82, 125, 3, 14, 3, -125, 2),
                    Hash.create(-58, -97, -14, -33, 44, -63, 90, -81, -60, 69, 10, -62, 20, -51, 94, -3, -34, 127, 49, -117, -116, -28, 33, 9, 80, 19, 108, 116, -104, -123, 29, -44)
            )

            task1Group(
                    "Jelinek Mercer",
                    "B13", "C13",
                    Hash.create(-16, 97, 78, 75, 28, -37, 43, -52, -17, -105, -116, -127, 79, -36, -10, 33, -56, -81, 40, 21, 81, -119, -114, 40, 40, 106, -66, -105, -123, -7, -111, 3),
                    Hash.create(22, -107, 89, -110, -29, 114, 47, -48, 92, -52, 50, -123, -118, 35, -99, -6, 79, -20, 112, -127, 47, -36, -15, 49, -69, 9, 35, -10, 100, 126, -47, -40)
            )

            task1Group(
                    "Dirichlet",
                    "B14", "C14",
                    Hash.create(-86, 103, -28, -56, 38, -86, 94, -12, -82, -23, 111, 17, 2, 120, -30, 90, 49, 103, 86, -6, -18, 85, 67, -22, 118, 122, -117, 63, 56, -120, -25, -17),
                    Hash.create(-31, 10, -76, -43, -22, -36, -93, 14, 96, -46, 86, 126, 78, -81, 24, 83, -102, -83, -97, -49, -8, 99, 88, -4, 93, 74, -34, -107, 106, -6, -121, -31)
            )
        }

        "Task 2" asGroup {

            val cells =  pathToHandIn.readValueOf("2) BM25"){
                val toLoad = from("B24").to("B26")
                toLoad.cellsNotNull
                        .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                        .toList()
            }

            val student = hash { updateWithStrings(cells) }

            "Normal" asTest {
                println(cells)
                val expected = Hash.create(-71, 112, 127, -69, 58, 98, -59, -80, -69, -56, 53, -13, -125, 93, 32, -85, 120, 11, 72, -73, 50, -77, -4, 59, -79, 19, -120, -118, 82, 98, 90, 28)
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "Alternativ" asTest  {
                println(cells)
                val expected = Hash.create(-41, 105, -26, 105, 10, 107, 7, -99, -12, 49, -60, 82, -79, -58, -119, 109, -94, 116, 62, -120, -88, -8, -96, 79, -93, 18, -94, -48, -29, 30, -3, -88)
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
        }

        "Task 4" asGroup {
            "Part a)" asGroup {
                "Dezimal" asTest {
                    val expected = Hash.create(58, 105, 15, -92, 91, 104, -6, -117, -10, -38, 121, -14, 109, -72, 9, -67, 6, -96, 123, 26, -100, 34, -50, 115, 20, 123, -34, -101, -1, 115, -113, -80)
                    val cells = pathToHandIn.readValueOf("4) Kodierung"){
                        from("I6").to("L11").cellsNotNull
                                .map { it.valueForTask4() }
                                .toList()
                    }
                    println(cells.joinToString())
                    val student = hash { update(cells) }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(expected, student)
                }

                "Binär" asTest {
                    val expected = Hash.create(-26, -37, 14, -24, -120, -49, -48, -18, 83, -28, -67, 94, -51, -123, -72, 9, -83, -83, 62, 99, 22, -4, -75, -107, 76, 77, -44, -39, 45, -6, -3, 29)
                    val cells = pathToHandIn.readValueOf("4) Kodierung"){
                        from("N6").to("Q11").cellsNotNull
                                .map { it.valueForTask4() }
                                .toList()
                    }
                    println(cells.joinToString())
                    val student = hash { update(cells) }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(expected, student)
                }
            }

            "Part b)" asTest {
                val expected = Hash.create(103, -86, 17, -127, -21, 17, 10, -102, 76, -97, -124, -43, 108, 86, -97, -53, -73, -86, -42, 94, 43, 30, 109, 0, 9, 23, 40, 109, 101, -127, -44, -128)
                val cells = pathToHandIn.readValueOf("4) Kodierung"){
                    from("B16").to("D16").cellsNotNull
                            .map { it.numericCellValue.toLong() }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { update(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "Part c)" asGroup  {
                "Binär" asTest {
                    val expected = Hash.create(-18, 122, 22, -69, 33, -119, 112, -85, 11, -61, -2, -69, -89, -56, -69, 75, 94, 84, -97, 103, -25, -104, -80, 42, 28, -82, -90, -114, 101, 110, 126, -81)
                    val cells = pathToHandIn.readValueOf("4) Kodierung"){
                        from("N23").to("R23").cellsNotNull
                                .map { it.numericCellValue.toLong() }
                                .toList()
                    }
                    println(cells.joinToString())
                    val student = hash { update(cells) }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(expected, student)
                }
                "Dezimal" asTest {
                    val expected = Hash.create(4, -96, 94, 97, 82, 43, 31, -5, 11, -68, -64, 38, -58, -74, 26, -45, 0, 47, 32, 23, 121, -41, -77, -128, -7, -62, -34, 82, -40, 107, -28, -36)
                    val cell = pathToHandIn.readValueOf("4) Kodierung"){
                        val cell = "S23".cell
                        assertNotNull(cell){
                            "The cell at ${it.address} has no value!"
                        }
                        cell.numericCellValue.toLong()
                    }
                    println(cell)
                    val student = hash { update(cell) }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(expected, student)
                }
            }
        }

        "Task 5" asGroup {
            "Document Ranking" asTest {
                val expected = Hash.create(-6, 77, -107, -47, -56, -126, -17, -28, 14, 102, -126, 74, 90, -21, 62, -103, 36, -84, 43, -92, 86, -99, 70, -69, 71, 100, -17, -95, 28, -70, 98, -16)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("F14").to("J18").cellsNotNull
                            .map { it.stringCellValue }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "Remaining" asTest {
                val expected = Hash.create(-32, 126, -79, -94, 25, 32, -75, -100, -99, 21, -116, -36, 12, -46, -1, 100, -74, 106, 119, -104, -12, 41, 120, -54, -126, -110, -86, -10, -22, -20, 27, 16)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("O14").to("O18").cellsNotNull
                            .map { it.numericCellValue.toInt() }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { update(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "GAP" asTest {
                val expected = Hash.create(-81, -68, 10, -45, 70, -60, -26, 20, -75, -5, 68, 37, -109, 113, 116, 41, 112, 26, -21, -62, 103, -41, -114, 86, 107, -16, -32, -107, -102, -124, -95, -85)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("P14").to("P18").cellsNotNull
                            .map { it.numericCellValue.toInt() }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { update(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "Stabil" asTest {
                val expected = Hash.create(-4, 39, 38, 78, 51, 92, 78, 127, -73, 5, -104, -103, -128, -34, -109, -53, -4, 101, 55, -45, -26, 61, -116, 127, 49, 49, 88, 99, -128, -110, 92, -19)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("Q14").to("Q18").cellsNotNull
                            .map { it.stringCellValue }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
        }

        "Task 6" asGroup {
            "Initialisierung" asTest {
                runTestFor6("B2", "G2", Hash.create(-28, 123, -103, 39, -39, 40, -84, -127, 64, -6, -112, -78, 88, -28, -64, 110, 49, 67, -15, -81, 70, 73, 27, -118, -124, 73, -55, 54, 31, -24, -55, -110))
            }
            "Iteration 1" asTest {
                runTestFor6("B3", "G3", Hash.create(-82, 17, 77, -119, -3, -70, 105, -72, 126, -128, -101, -60, -4, -109, 102, -5, -59, -92, -8, -69, 10, 100, -35, 14, -73, -12, -32, -103, 53, -97, 34, 120))
            }
            "Iteration 10" asTest {
                runTestFor6("B12", "G12", Hash.create(98, 1, 13, 14, -41, -64, -11, -48, -81, 28, 48, -3, -66, 126, 117, -123, -26, -101, 89, -100, -110, -30, 97, -109, -79, -12, 3, -121, -46, -91, -77, -62))
            }

            "Iteration 50" asTest {
                runTestFor6("B52", "G52", Hash.create(95, 46, 23, -15, 29, 38, 26, -41, 6, 105, -89, -120, -73, -57, 94, -105, -17, -55, -62, -1, -25, 62, 46, -64, 41, 74, -100, 78, 92, -53, 75, -2))
            }

            "Iteration 70" asTest {
                runTestFor6("B72", "G72", Hash.create(-16, 55, 101, -123, -71, 125, -42, 6, -120, -117, -78, -82, -28, 95, 50, 44, -71, 85, -92, 28, -17, 69, -37, 45, -122, 78, 83, 113, -40, -6, 120, 78))
            }

            "Iteration 80" asTest {
                runTestFor6("B82", "G82", Hash.create(-22, 86, -38, -95, 8, 31, -49, -65, -3, -99, -34, -42, 27, -66, 75, -53, -70, 33, -14, -12, 61, 31, -9, 109, 94, 68, -126, 21, 21, -13, 29, -85))
            }

            "IO-Tabelle" asTest {
                runTestFor6IO("M5", "R10", Hash.create(38, -45, -19, -90, 80, 63, 40, -29, -27, -92, -24, 2, 48, -1, -90, 50, 46, -86, 30, 32, -57, -16, -77, -55, -106, -54, 89, 7, 20, 68, -26, 3))
            }

            "IO-Tabelle Ausgehend" asTest {
                runTestFor6IO("M11", "R11", Hash.create(29, 116, -28, -31, -115, -52, -11, -32, -89, 93, 80, -56, -122, 15, -72, 89, -40, -76, 47, -70, 7, 90, -31, -114, -120, -103, 46, -72, 73, -38, -26, -10))
            }

            "IO-Tabelle Eingehend" asTest {
                runTestFor6IO("S5", "S10", Hash.create(104, -116, -68, 45, -55, -76, -120, 12, -37, 91, 3, -100, 19, -67, 109, 127, -110, -62, -97, -27, 73, 89, 16, -2, -47, -99, -73, -32, -122, 44, -49, 107))
            }
        }
    }


    // Helper-Functions

    private fun runTestFor6(a: String, b: String, expected: Hash) {
        val cells = pathToHandIn.readValueOf("6) PageRank"){
            from(a).to(b).cellsNotNull
                    .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                    .toList()
        }
        println(cells.joinToString())
        val student = hash { updateWithStrings(cells) }
        println(student.convertToHashDeclaration())
        assertHashEquals(expected, student)
    }

    private fun runTestFor6IO(a: String, b: String, expected: Hash) {
        val cells = pathToHandIn.readValueOf("6) PageRank"){
            from(a).to(b).cellsNotNull
                    .map { it.numericCellValue.toInt() }
                    .toList()
        }
        println(cells.joinToString())
        val student = hash { update(cells) }
        println(student.convertToHashDeclaration())
        assertHashEquals(expected, student)
    }

    private fun Cell.valueForTask4() =
            when(cellType){
                CellType.STRING -> {
                    if(stringCellValue.trim() == "-") -1
                    else error("Value '$stringCellValue' not supported.")
                }
                CellType.NUMERIC -> numericCellValue.toLong()
                else -> error("A cell of type $cellType was not expected.")
            }

    private fun DynamicTestContainerDefinition.task1Group(
            name: String,
            a: String,
            b: String,
            hashNormal: Hash,
            hashLog: Hash
    ) {
        name asGroup {
            val cells = pathToHandIn.readValueOf("1) IR-Modelle"){
                from(a).to(b).cellsNotNull
                        .map { it.numericCellValue }
                        .toList()
            }

            "without log" asTest {
                println(cells)
                val student = hash {
                    updateWithStrings(cells.map { it.toStringForEvalWithNDigits(10) })
                }
                println(student.convertToHashDeclaration())
                assertHashEquals(hashNormal, student)
            }

            "with log" asTest {
                println(cells)
                val student = hash {
                    updateWithStrings(cells.map { it.toStringForEvalWithNDigits(5) })
                }
                println(student.convertToHashDeclaration())
                assertHashEquals(hashLog, student)
            }
        }
    }

}
