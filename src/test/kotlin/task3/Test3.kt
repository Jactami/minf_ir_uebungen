/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package task3

import de.fengl.ktestfactories.KTestDisplayNames
import de.fengl.ktestfactories.KTestFactory
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
@KTestDisplayNames
class Test3 : KTestFactory({
    val config = File("./cfg/test3.json").loadConfigOrFail(::Test3Config)
    val pathToHandIn: File = File(config.pathToHandInExcel)
    require(pathToHandIn.isFile){
        "The path ${pathToHandIn.canonicalPath} has to point to a file!"
    }
    "Task 1" asGroup {
        "MIM" asTest {
            val expected = Hash.create(-66, 71, 44, 112, 26, -109, -95, 91, 59, 40, -29, -113, 118, 3, -1, -69, -125, -16, 35, -51, 34, -100, 23, 117, 84, 23, 104, 126, 19, 90, 50, 57)
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
            val expected = Hash.create(-13, 47, -40, -19, -21, -29, 65, -86, -2, -63, -10, -31, 109, -90, -123, -105, -20, -7, 65, 61, -54, 72, 118, 121, -25, 42, 118, 96, -61, 101, 75, 91)
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
            val expected = Hash.create(-2, -80, -26, -91, -60, 123, 47, 103, 56, -99, -116, 104, 110, -87, 29, 91, -8, -42, 110, 87, 50, 91, -54, 111, 0, 54, 3, 32, -34, 122, -44, -43)
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
            val expected = Hash.create(1, -62, 20, 115, 120, -97, 121, -17, 67, 69, 121, 105, 125, 20, -61, 46, -14, -15, 16, -120, 59, 22, 10, -118, -52, 119, -62, 75, -86, -23, 1, -57)
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
})