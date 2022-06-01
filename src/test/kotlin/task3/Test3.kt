/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task3

import misc.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestFactory
import toolkit.Hash
import toolkit.hash
import toolkit.updateWithStrings
import java.io.File

/**
 * Für das 3te Übungsblatt. 
 */

class Test3 {
    val config = File("./cfg/test3.json").loadConfigOrFail(::Test3Config)

    val pathToHandIn: File = File(config.pathToHandInExcel)

    init {
        require(pathToHandIn.isFile){
            "The path ${pathToHandIn.canonicalPath} has to point to a file!"
        }
    }

    @TestFactory
    @DisplayName("Abgabe 3")
    fun tasks() = testFactoryDefinition {
        "Task 1" asGroup {
            "MIM" asTest {
                val expected = Hash.create(-10, 91, -34, -117, -53, -85, -16, 33, 62, -2, -14, 87, -52, -118, -77, 10, -40, -28, 107, -80, 118, -119, 53, -63, -100, -101, 45, -63, -6, -110, -61, 19)
                val cells = pathToHandIn.readValueOf("1) Einfache Term-Assoziationsma"){
                    from("B9").to("B10").cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
            "EMIM" asTest {
                val expected = Hash.create(-101, 110, 41, -12, -9, -26, 7, -86, -21, 95, -87, -8, 95, -105, -33, -113, 119, -48, 50, 86, -10, -95, -93, 36, 108, 82, 51, 28, -70, -10, 14, 122)
                val cells = pathToHandIn.readValueOf("1) Einfache Term-Assoziationsma"){
                    from("C9").to("C10").cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
            "X²" asTest {
                val expected = Hash.create(29, -71, 65, -43, 89, -120, 71, 5, -95, -90, 84, 102, -68, -60, -58, 31, -122, 118, -56, -38, 116, -81, 96, -29, -118, 4, 91, -5, 71, -5, 20, -50)
                val cells = pathToHandIn.readValueOf("1) Einfache Term-Assoziationsma"){
                    from("D9").to("D10").cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(10) }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
            "Dice" asTest {
                val expected = Hash.create(56, -63, 119, -87, -105, 99, 89, 43, -42, -120, -112, 37, 57, -15, 52, -118, 0, 13, 14, 126, 69, 66, -4, 75, -43, -125, 57, -5, -113, 92, -32, -8)
                val cells = pathToHandIn.readValueOf("1) Einfache Term-Assoziationsma"){
                    from("E9").to("E10").cellsNotNull
                            .map { it.numericCellValue.toStringForEvalWithNDigits(5) }
                            .toList()
                }
                println(cells.joinToString())
                val student = hash { updateWithStrings(cells) }
                println(student.convertToHashDeclaration())
                assertHashEquals(expected, student)
            }
        }
    }
}
