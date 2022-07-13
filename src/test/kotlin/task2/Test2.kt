/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task2

import de.fengl.ktestfactories.DynamicTestGroup
import de.fengl.ktestfactories.KTestDisplayNames
import misc.*
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import toolkit.*
import java.io.File
import kotlin.test.assertNotNull


/**
 * Für das 2te Übungsblatt. 
 */
@KTestDisplayNames
class Test2 : de.fengl.ktestfactories.KTestFactory(
    {
        val config = File("./cfg/test2.json").loadConfigOrFail(::Test2Config)

        val pathToHandIn = File(config.pathToHandInExcel)
        require(pathToHandIn.isFile){
            "The path ${pathToHandIn.canonicalPath} has to point to a file!"
        }

        fun runTestFor6(a: String, b: String, expected: Hash) {
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

        fun runTestFor6IO(a: String, b: String, expected: Hash) {
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

        fun Cell.valueForTask4() =
            when(cellType){
                CellType.STRING -> {
                    if(stringCellValue.trim() == "-") -1
                    else error("Value '$stringCellValue' not supported. Please use a numeric.")
                }
                CellType.NUMERIC -> numericCellValue.toLong()
                else -> error("A cell of type $cellType was not expected. Please use either a minus (-) for empty fields or a number.")
            }

        fun DynamicTestGroup.task1Group(
            name: String,
            a: String,
            b: String,
            hashNormal: Hash,
            hashLog: Hash
        ) {
            name asGroup {
                val cellsNoLog = pathToHandIn.readValueOf("1) IR-Modelle"){
                    from(a).to(b).cellsNotNull
                        .map { it.numericCellValue }
                        .toList()
                }

                val cellsWithLog = pathToHandIn.readValueOf("1) IR-Modelle"){
                    from(a).to(b).cellsNotNull
                        .map { it.numericCellValue }
                        .toList()
                }

                "without log (10 digits)" asTest {
                    println(cellsNoLog)
                    val student = hash {
                        updateWithStrings(cellsNoLog.map { it.toStringForEvalWithNDigits(10) })
                    }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(hashNormal, student)
                }

                "with log (5 digits)" asTest {
                    println(cellsWithLog)
                    val student = hash {
                        updateWithStrings(cellsWithLog.map { it.toStringForEvalWithNDigits(5) })
                    }
                    println(student.convertToHashDeclaration())
                    assertHashEquals(hashLog, student)
                }
            }
        }

        "Task 1" asGroup {
            task1Group(
                "Maximum Likelihood",
                "B12", "C12",
                Hash.create(-78, 111, -35, 42, -32, 74, 107, -109, -99, -111, -88, 83, 67, 119, -46, 53, -89, -95, 51, 45, -105, -102, 119, -126, 73, -122, 119, 50, -34, -114, -40, -123),
                Hash.create(-17, -47, -123, 50, -99, -128, -18, -127, -120, -35, 108, -20, 109, -32, -71, -107, 30, 50, 105, -116, 66, 6, -83, 124, 37, 111, -115, 60, 123, 98, 67, 124)
            )

            task1Group(
                "Jelinek Mercer",
                "B13", "C13",
                Hash.create(31, -21, 81, -74, -59, -9, 47, 6, 10, 119, -38, -18, -110, 30, -88, 9, -39, 101, 25, -58, -106, -17, -38, 88, -119, 93, 113, 53, 115, -41, -83, -41),
                Hash.create(-102, 33, 8, -36, 83, -48, -27, -41, -7, -91, -38, -42, -108, -55, 22, 86, -49, 2, -102, 29, -81, 105, 18, -72, -66, 101, -76, 95, 31, -27, -109, 118)
            )

            task1Group(
                "Dirichlet",
                "B14", "C14",
                Hash.create(17, -45, 119, -116, 68, 41, -57, 67, 36, -102, -26, 67, 105, 90, 96, -82, -100, 121, -81, 99, -108, -30, 118, -76, 83, -8, 24, -52, -43, 122, 83, 47),
                Hash.create(35, 77, -115, -97, -120, 36, 103, 88, -17, 12, -68, -32, -3, -10, -68, -88, 21, -98, -21, -74, -109, -73, -115, 75, -91, -126, -73, -113, -74, -114, 36, 98)
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
                val expected = Hash.create(31, -119, 103, 100, 125, -119, 103, 11, 71, -9, 30, 12, 59, -105, -81, -96, -107, 5, 105, 34, -42, -73, -12, 17, -85, 63, 100, 109, -1, -61, 44, 104)
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
        }

        "Task 4" asGroup {
            "Part a)" asGroup {
                "Dezimal" asTest {
                    val expected = Hash.create(74, 119, -99, 20, -103, -51, -21, 62, 107, -109, 43, -55, 42, -116, -95, -34, -4, -32, 52, 15, -120, -61, -52, -66, 66, 20, -117, -75, 95, -92, 72, -67)
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
                    val expected = Hash.create(100, -64, 68, 70, 6, -105, 99, -66, 35, -55, 69, 43, 124, -8, -18, -51, -42, -87, -64, -66, -56, 57, -106, 123, 94, 101, -13, 57, -8, -62, -55, -124)
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
                val expected = Hash.create(-35, 115, -86, 114, -10, -127, -66, -110, -127, 109, -2, -1, -123, -110, -26, 73, 41, 113, -10, -97, 13, -62, -10, 66, 113, -28, 10, -126, -88, 21, -77, -70)
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
                    val expected = Hash.create(65, -124, -13, -43, 116, 35, 11, -13, -13, 30, 117, 62, 5, -123, 40, -100, 19, -20, -22, -61, -122, 55, 38, -82, -23, 39, 7, -108, 20, 103, 59, 17)
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
                    val expected = Hash.create(50, 1, -63, 1, -22, 102, 72, 88, -116, -101, 56, 68, -113, 69, 46, -32, -23, 90, -121, -54, -108, 116, -29, 6, -47, -23, 15, 34, 68, -124, -54, -12)
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
                val expected = Hash.create(-25, 89, -106, 15, -113, -76, 6, 44, -92, 121, -12, 72, 10, -17, 28, 64, -35, 100, -127, -127, 18, -46, 100, 58, 38, 50, 4, -26, -12, 42, -14, 28)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("F14").to("J18").cellsNotNull
                        .map { it.stringCellValue.lowercase() }
                        .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }

            "Remaining" asTest {
                val expected = Hash.create(-68, -43, -95, 24, 38, 38, -82, 10, -89, -127, 84, 79, 71, -17, 5, 105, 77, -32, 99, 1, -106, -10, 120, 46, -47, 62, -76, 116, -51, -59, -64, 60)
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
                val expected = Hash.create(58, 104, 12, -72, -120, 91, 127, -63, 60, 83, -1, -52, 104, -32, -45, -35, 4, -36, -109, -55, -80, 116, 35, 81, 78, -72, 1, -28, -9, -3, -21, 123)
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
                val expected = Hash.create(37, 27, -90, -64, 27, 13, 98, -13, 4, 102, 96, -78, 56, -40, 39, -51, -35, 55, 39, 85, 22, 25, -6, -50, -11, -63, 112, -118, -55, 97, 17, -76)
                val cells = pathToHandIn.readValueOf("5) TAAT"){
                    from("Q14").to("Q18").cellsNotNull
                        .map { it.stringCellValue.lowercase() }
                        .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
        }

        "Task 6" asGroup {

            val solutions = object {
                val init = Hash.create(-28, 123, -103, 39, -39, 40, -84, -127, 64, -6, -112, -78, 88, -28, -64, 110, 49, 67, -15, -81, 70, 73, 27, -118, -124, 73, -55, 54, 31, -24, -55, -110)
                val iter1 = Hash.create(2, -82, -39, 85, 119, 37, 94, 62, -127, 1, -31, -94, -10, -126, -106, 6, 26, 88, -36, 66, -57, -62, -123, -92, 37, 47, -3, -90, -18, -32, 33, 21)
                val iter10 = Hash.create(-60, -48, 62, 54, -74, 92, -76, -24, -121, -73, -65, -118, -91, 89, -92, 78, -56, 26, -122, -19, -12, 78, -114, 120, -96, 117, -51, 61, 127, -46, 69, 56)
                val iter50 = Hash.create(32, -17, -117, -63, 51, -31, 102, 18, 126, 40, 73, -36, -6, -96, -54, -111, -2, -121, -60, 104, 55, 52, 60, 93, -109, 108, -40, 70, 11, -99, -121, -115)
                val iter70 = Hash.create(0, -52, 7, -82, 85, -43, 65, 26, -119, -38, 69, 8, -20, -70, 107, 94, 20, -75, 95, 50, -38, -42, -57, 42, 96, 125, 92, -116, 46, 49, 63, 96)
                val iter80 = Hash.create(46, 124, -117, 57, 52, 79, 21, 42, -70, -18, 98, 11, -51, -126, -127, 60, 5, -59, -57, -121, 126, -65, -18, 57, -95, -83, -112, 7, -69, -109, -112, -24)
                val io = Hash.create(-75, 31, 75, 31, 51, 80, -37, -110, -91, 18, -84, 34, 88, 86, 98, -94, 23, 98, -116, -3, -78, -58, -124, 33, 11, 111, -114, -15, -38, -26, -117, 95)
                val ioa = Hash.create(-93, -101, 54, 52, -44, 56, 111, -13, -76, -48, 17, 96, 60, -27, 82, 103, -92, -117, -90, -90, 86, -30, 99, -29, -67, -101, -115, -108, -72, 81, -25, 8)
                val ioi = Hash.create(-22, -37, 83, -120, 90, -31, 62, 20, 28, 55, -23, -10, 0, -61, -38, -1, 89, -6, 103, 112, -29, 105, -26, -21, -126, -77, -84, -123, 69, -34, -26, 18)
            }

            "Initialisierung" asTest {
                runTestFor6("B2", "G2", solutions.init)
            }
            "Iteration 1" asTest {
                runTestFor6("B3", "G3", solutions.iter1)
            }
            "Iteration 10" asTest {
                runTestFor6("B12", "G12", solutions.iter10)
            }

            "Iteration 50" asTest {
                runTestFor6("B52", "G52", solutions.iter50)
            }

            "Iteration 70" asTest {
                runTestFor6("B72", "G72", solutions.iter70)
            }

            "Iteration 80" asTest {
                runTestFor6("B82", "G82", solutions.iter80)
            }

            "IO-Tabelle" asTest {
                runTestFor6IO("M5", "R10", solutions.io)
            }

            "IO-Tabelle Ausgehend" asTest {
                runTestFor6IO("M11", "R11", solutions.ioa)
            }

            "IO-Tabelle Eingehend" asTest {
                runTestFor6IO("S5", "S10", solutions.ioi)
            }
        }
    }
) {






}
