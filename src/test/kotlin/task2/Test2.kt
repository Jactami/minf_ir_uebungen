package task2

import misc.*
import org.apache.poi.ss.util.CellAddress
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import toolkit.Hash
import toolkit.hash
import toolkit.updateWithStrings
import java.io.File



class Test2 {
    val pathToHandIn: File = File("D:\\NextCloud\\IR\\Übungen\\2021_SS\\Solutions\\2\\Solution_2_Handout.xlsx") //TODO("Pfad zur Abgabe xlsx/xslm.")


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
            val cells =  pathToHandIn.readHashOf("2) BM25"){
                val toLoad = from("B24").to("B26")
                toLoad.cellsNotNull
                        .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                        .toList()
            }
            println(cells)
            val student = hash { updateWithStrings(cells) }

            "Normal" asTest {
                val expected = Hash.create(-71, 112, 127, -69, 58, 98, -59, -80, -69, -56, 53, -13, -125, 93, 32, -85, 120, 11, 72, -73, 50, -77, -4, 59, -79, 19, -120, -118, 82, 98, 90, 28)
                println(student)
                assertHashEquals(expected, student)
            }

            "Alternativ" asTest  {
                val expected = Hash.create(-41, 105, -26, 105, 10, 107, 7, -99, -12, 49, -60, 82, -79, -58, -119, 109, -94, 116, 62, -120, -88, -8, -96, 79, -93, 18, -94, -48, -29, 30, -3, -88)
                println(student)
                assertHashEquals(expected, student)
            }
        }
    }


    // Helper Funktionen

    private fun DynamicTestContainerDefinition.task1Group(
            name: String,
            a: String,
            b: String,
            hashNormal: Hash,
            hashLog: Hash
    ) {
        name asGroup {
            val cells = pathToHandIn.readHashOf("1) IR-Modelle"){
                from(a).to(b).cellsNotNull
                        .map { it.numericCellValue }
                        .toList()
            }

            println(cells)

            "without log" asTest {
                val student = hash {
                    updateWithStrings(cells.map { it.toStringForEvalWithNDigits(10) })
                }
                println(student)
                assertHashEquals(hashNormal, student)
            }

            "with log" asTest {
                val student = hash {
                    updateWithStrings(cells.map { it.toStringForEvalWithNDigits(5) })
                }
                println(student)
                assertHashEquals(hashLog, student)
            }
        }
    }

}
