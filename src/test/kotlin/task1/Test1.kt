package task1

import exercise1.readResult
import misc.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail
import toolkit.*
import java.io.File

@DisplayName("Tests for 1")
class TestsFor1 {
    val pathToHandInExcel: File = File("D:\\NextCloud\\IR\\Übungen\\2021_SS\\Solutions\\1\\Solution_1_Handout - Kopie.xlsm") // TODO("Pfad zur Abgabe xlsx/xslm.")
    val pathToHandIn: File = File("./docker/task1/ES_Angabe")

    @TestFactory
    @DisplayName("Abgabe 1")
    fun tasks() = testFactoryDefinition {
        "Task 1" asTest {
            val expected = Hash.create(5, 38, 57, 93, 74, -5, 94, 74, 125, 44, -51, -43, 37, -42, -37, -72, 14, -15, -119, 98, -72, 53, -110, 69, -77, 106, -90, 37, 100, 60, -121, -56)
            val studentHash = pathToHandInExcel.readHashOf("1) GMAP"){
                val cells = (from(8, 1).to(8,2) and from(10, 1).to(10,2)).cellValues.filterNotNull().map { it.numericCellValue.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it) }
            assertHashEquals(expected, studentHash)
        }

        "Task 3" asGroup {
            "Werte" asTest {
                val expected = Hash.create(-70, -20, 33, -28, -13, -11, 21, -81, 116, -54, 67, -73, -56, 1, 60, 91, -62, -16, 63, -123, 50, -80, 126, 24, -75, 81, 90, -11, 96, -86, -3, -115)
                val studentHash = pathToHandInExcel.readHashOf("3) NDCG"){
                    val toLoad = from(35, 2).to(37, 8) and from(40, 2).to(41, 8)
                    val cells = toLoad.cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                    println(cells.joinToString())
                    hash { updateWithStrings(cells) }
                }.also { println(it) }
                assertHashEquals(expected, studentHash)
            }

            "Evaluation" asTest {
                val expected = Hash.create(-32, 49, 23, -49, 23, -5, 62, 5, -10, 47, -31, -80, 109, -46, -110, 15, -68, 108, -11, -109, -54, 6, -2, -93, 88, -16, 3, -98, -30, 52, -42, -96)
                val studentHash = pathToHandInExcel.readHashOf("3) NDCG"){
                    val callsString = from(43,2).to(43, 8).cellValues.mapNotNull { it?.stringCellValue }.toList()
                    println(callsString.joinToString())
                    hash { updateWithStrings(callsString) }
                }.also { println(it) }
                assertHashEquals(expected, studentHash)
            }
        }

        "Task 4" asTest {
            val expected = Hash.create(-108, 121, 85, -110, -3, 110, 24, 18, -29, -107, -118, -117, -98, -123, 115, -61, 121, 40, 57, 103, -33, 74, 78, 116, -5, -17, -65, 53, -72, 66, -6, -3)
            val studentHash = pathToHandInExcel.readHashOf("4) Ranking"){
                val cells = from(12,1).to(13, 3).cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it) }
            assertHashEquals(expected, studentHash)
        }

        "Task 5" asTest {
            val expected = Hash.create(27, -21, -50, 52, 121, 117, -22, -6, -82, 114, 4, 110, -55, 26, 105, 24, 117, -86, -55, 11, 24, 18, -40, 60, 84, 119, 104, 91, -31, 73, 119, 54)
            val studentHash = pathToHandInExcel.readHashOf("5) t-Test"){
                val cell = from(10, 1).cell!!.numericCellValue.roundForEvaluation()
                println(cell)
                hash { update(cell) }
            }.also { println(it) }
            assertHashEquals(expected, studentHash)
        }

        "Task 6" asTest {
            val expected = Hash.create(113, 116, -34, 105, -47, 81, 84, 18, -11, -41, 6, 25, -97, -123, -96, 93, 28, 106, 19, -49, -101, -82, -97, -7, 103, 50, -24, 41, 45, -6, -26, -16)
            val studentHash = pathToHandInExcel.readHashOf("6) VSM"){
                val cells = from(20,1).to(21, 3).cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }.also { println(it) }
            assertHashEquals(expected, studentHash)
        }
        
        val results = mapOf(
                "ergebnis1" to Hash.create(18, -95, 39, -9, 101, -74, 90, 49, -119, -108, -5, -29, -97, -54, 13, -34, 113, 76, -73, -21, 101, -88, -79, -107, 77, -53, -100, 34, -120, -44, -72, -62),
                "ergebnis2" to Hash.create(-115, 13, 27, -120, 85, -38, 73, -62, -1, -31, 45, 47, -43, -76, 9, 120, -45, -4, 41, -25, -69, 123, -97, 107, -84, -69, 88, 48, 91, 119, -8, -47),
                "ergebnis3" to Hash.create(-18, 71, -89, -54, -2, 121, -5, -19, 107, -72, 82, -4, 45, 81, -104, -76, -10, -20, 36, -66, 120, -38, 108, 6, 95, -32, -37, -109, -76, 61, 125, 19),
                "ergebnis4" to Hash.create(78, 82, -23, 73, 120, 109, -23, -21, 115, -27, 10, 40, -43, -73, -34, -103, 104, 1, -116, 35, 100, -81, 81, 1, 19, 125, -8, 124, 89, -91, -121, -4),
                "ergebnis5" to Hash.create(-18, 43, 96, 46, -15, -26, -108, -111, -70, 44, 124, -36, 125, 27, -13, -36, 90, 34, -68, -74, -39, -96, 101, -62, -36, 41, -24, -73, 43, -76, 86, 22),
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