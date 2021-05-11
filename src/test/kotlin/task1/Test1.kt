package task1

import exercise1.readResult
import misc.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import toolkit.*
import java.io.File

@DisplayName("Tests for 1")
class TestsFor1 {
    val pathToHandInExcel: File get() = File("D:\\NextCloud\\IR\\Übungen\\2021_SS\\Solutions\\1\\Solution_1_Handout.xlsm")
    val pathToHandIn: File get() =  File("./docker/task1/ES_Angabe")


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
            }.also { println(it.convertToArrayDeclaration()) }
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
                }.also { println(it.convertToArrayDeclaration()) }
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
                }.also { println(it.convertToArrayDeclaration()) }
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
            }.also { println(it.convertToArrayDeclaration()) }
            assertHashEquals(expected, studentHash)
        }

        "Task 5" asTest {
            val expected = Hash.create(27, -21, -50, 52, 121, 117, -22, -6, -82, 114, 4, 110, -55, 26, 105, 24, 117, -86, -55, 11, 24, 18, -40, 60, 84, 119, 104, 91, -31, 73, 119, 54)
            val studentHash = pathToHandInExcel.readValueOf("5) t-Test"){
                val toLoad = from(10, 1)
                val cell = toLoad.cell?.numericCellValue?.toStringForEvalWithNDigits(5) ?: fail { "The value at $toLoad was not found." }
                println(cell)
                hash { update(cell) }
            }.also { println(it.convertToArrayDeclaration()) }
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
                }.also { println(it.convertToArrayDeclaration()) }
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
                }.also { println(it.convertToArrayDeclaration()) }
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
                }.also { println(it.convertToArrayDeclaration()) }
                assertHashEquals(expected, studentHash)
            }

        }
        
        val results = mapOf(
                "ergebnis1" to Hash.create(18, -95, 39, -9, 101, -74, 90, 49, -119, -108, -5, -29, -97, -54, 13, -34, 113, 76, -73, -21, 101, -88, -79, -107, 77, -53, -100, 34, -120, -44, -72, -62),
                "ergebnis2" to Hash.create(-40, 65, -124, 48, -9, 8, -116, -118, -23, 111, -94, 127, -86, 66, 65, -112, 13, 67, -17, 108, 104, -18, -83, 87, -93, -103, 44, 115, -74, -92, 66, -90),
                "ergebnis3" to Hash.create(-53, -46, 57, -43, 79, 108, 57, 85, 32, -119, -61, 69, -83, 99, 71, -7, 11, 34, 15, -100, -48, 115, 58, 38, 78, 47, 66, -83, -66, 12, 18, -76),
                "ergebnis4" to Hash.create(-85, 48, 72, 40, 97, -107, 35, 0, 2, -108, -94, -36, 48, -18, -59, 82, 90, 24, 28, -29, 118, 19, 109, -3, 125, -28, -19, -101, 14, -42, 113, -64),
                "ergebnis5" to Hash.create(-18, -74, -89, -5, -56, -72, -7, -73, 30, 107, -106, -119, -99, -59, 67, -40, -3, 71, 13, -11, -12, 12, 18, -114, -46, -51, -38, 127, -120, -64, 66, 115),
        )

        "Task 7" asGroup {
            pathToHandIn.walkTopDown().filter { println(it); it.extension == "json" && "ergebnis" in it.nameWithoutExtension }.forEach { file ->
                file.name asTest {
                    val expected = results.getValue(file.nameWithoutExtension)
                    val student = hash { update(readResult(file)) }.also { println(it.convertToArrayDeclaration()) }
                    assertHashEquals(expected, student)
                }
            }
        }
    }

}