package task1

import misc.assertHashEquals
import misc.readHashOf
import misc.roundForEvaluation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import toolkit.*
import java.io.File
import kotlin.test.assertNotNull

class Test1 {
    val pathToHandIn: File = File("D:\\NextCloud\\IR\\Übungen\\2021_SS\\Solutions\\1\\Solution_1_Handout - Kopie.xlsm") //TODO("Pfad zur Abgabe xlsx/xslm.")


    @Nested
    inner class Task1 {

        val solutionToTask1 = Hash.create(5, 38, 57, 93, 74, -5, 94, 74, 125, 44, -51, -43, 37, -42, -37, -72, 14, -15, -119, 98, -72, 53, -110, 69, -77, 106, -90, 37, 100, 60, -121, -56)

        @Test
        fun test(){
            val studentHash = pathToHandIn.readHashOf("1) GMAP"){
                val cells = (from(8, 1).to(8,2) and from(10, 1).to(10,2)).cellValues.filterNotNull().map { it.numericCellValue.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask1, studentHash)
        }
    }



    @Nested
    inner class Task3 {
        val solutionToTask3_values = Hash.create(-70, -20, 33, -28, -13, -11, 21, -81, 116, -54, 67, -73, -56, 1, 60, 91, -62, -16, 63, -123, 50, -80, 126, 24, -75, 81, 90, -11, 96, -86, -3, -115)

        val solutionToTask3_evals = Hash.create(-32, 49, 23, -49, 23, -5, 62, 5, -10, 47, -31, -80, 109, -46, -110, 15, -68, 108, -11, -109, -54, 6, -2, -93, 88, -16, 3, -98, -30, 52, -42, -96)

        @Test
        fun test_values(){
            val studentHash = pathToHandIn.readHashOf("3) NDCG"){
                val toLoad = from(35, 2).to(37, 8) and from(40, 2).to(41, 8)
                val cells = toLoad.cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask3_values, studentHash)
        }

        @Test
        fun test_eval(){
            val studentHash = pathToHandIn.readHashOf("3) NDCG"){
                val callsString = from(43,2).to(43, 8).cellValues.mapNotNull { it?.stringCellValue }.toList()
                println(callsString.joinToString())
                hash { updateWithStrings(callsString) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask3_evals, studentHash)
        }
    }

    @Nested
    inner class Task4 {

        val solutionToTask4 = Hash.create(-108, 121, 85, -110, -3, 110, 24, 18, -29, -107, -118, -117, -98, -123, 115, -61, 121, 40, 57, 103, -33, 74, 78, 116, -5, -17, -65, 53, -72, 66, -6, -3)

        @Test
        fun test(){
            val studentHash = pathToHandIn.readHashOf("4) Ranking"){
                val cells = from(12,1).to(13, 3).cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask4, studentHash)
        }
    }

    @Nested
    inner class Task5 {

        val solutionToTask5 = Hash.create(27, -21, -50, 52, 121, 117, -22, -6, -82, 114, 4, 110, -55, 26, 105, 24, 117, -86, -55, 11, 24, 18, -40, 60, 84, 119, 104, 91, -31, 73, 119, 54)


        @Test
        fun test(){
            val studentHash = pathToHandIn.readHashOf("5) t-Test"){
                val cell = from(10, 1).cell!!.numericCellValue.roundForEvaluation()
                println(cell)
                hash { update(cell) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask5, studentHash)
        }
    }


    @Nested
    inner class Task6 {

        val solutionToTask6 = Hash.create(113, 116, -34, 105, -47, 81, 84, 18, -11, -41, 6, 25, -97, -123, -96, 93, 28, 106, 19, -49, -101, -82, -97, -7, 103, 50, -24, 41, 45, -6, -26, -16)

        @Test
        fun test(){
            val studentHash = pathToHandIn.readHashOf("6) VSM"){
                val cells = from(20,1).to(21, 3).cellValues.mapNotNull { it?.numericCellValue?.roundForEvaluation() }.toList()
                println(cells.joinToString())
                hash { updateWithStrings(cells) }
            }
            println(studentHash.convertToArrayDeclaration())
            assertNotNull(studentHash)
            assertHashEquals(solutionToTask6, studentHash)
        }
    }


}