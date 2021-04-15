package misc

import org.junit.jupiter.api.Assertions
import toolkit.Hash
import toolkit.SheetAccess
import toolkit.readExcel
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


fun File.readHashOf(taskName: String, hashReader: SheetAccess.() -> Hash) =
        readExcel(this){
            taskName{ hashReader() }
        }


fun Double.roundForEvaluation(n: Int = 5) = String.format("%.${n}f", this)