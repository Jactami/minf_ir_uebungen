package misc

import org.apache.poi.ss.util.CellAddress
import org.junit.jupiter.api.Assertions
import toolkit.Hash
import toolkit.SheetAccess
import toolkit.useAsExcel
import java.io.File

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


fun <T> File.readHashOf(taskName: String, hashReader: SheetAccess.() -> T) =
        useAsExcel(this){
            taskName{ hashReader() }
        }


fun Double.toStringForEvalWithNDigits(n: Int) = String.format("%.${n}f", this)
