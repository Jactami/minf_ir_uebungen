/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package misc

import org.apache.poi.ss.util.CellAddress
import org.junit.jupiter.api.Assertions
import toolkit.Hash
import toolkit.SheetAccess
import toolkit.useAsExcel
import java.io.File
import java.util.*

@Suppress("NOTHING_TO_INLINE")
inline fun assertHashEquals(expected: Hash, actual: Hash) =
        Assertions.assertArrayEquals(expected.value, actual.value)

inline fun assertHashEquals(expected: Hash, actual: Hash, crossinline messageSupplier: ()->String) =
        Assertions.assertArrayEquals(expected.value, actual.value) { messageSupplier() }


/**
 * Converts this pair into a array.
 */
inline fun <reified T> Pair<T, T>.toArray(): Array<T> = arrayOf(first, second)

/**
 * Converts this triple into a array.
 */
inline fun <reified T> Triple<T, T, T>.toArray(): Array<T> = arrayOf(first, second, third)


fun <T> File.readValueOf(taskName: String, hashReader: SheetAccess.() -> T) =
        useAsExcel(this){
            taskName { hashReader() }
        }


fun Double.toStringForEvalWithNDigits(n: Int) = "%.${n}f".format(Locale.GERMAN, this)
