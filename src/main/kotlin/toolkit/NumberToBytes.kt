package toolkit

import java.nio.ByteBuffer

fun Number.bytes(): ByteArray =
    when(this){
        is Byte -> byteArrayOf(this)
        is Short -> bytes()
        is Int -> bytes()
        is Long -> bytes()
        is Float -> bytes()
        is Double -> bytes()
        else -> error("Unknown number type ${this::class.simpleName}")
    }


inline fun <reified T: Number> Iterable<T>.bytes(): ByteArray =
        if (this !is Collection<T>){ toList() } else { this }.bytes()


inline fun <reified T: Number> Collection<T>.bytes(): ByteArray {
    if (isEmpty()) return byteArrayOf()
    return when(T::class){
        Byte::class -> (this as Collection<Byte>).toByteArray()
        Short::class -> {
            ByteBuffer.allocate(size*Short.SIZE_BYTES).also { buffer ->
                (this as Collection<Short>).forEach { buffer.putShort(it) }
            }.array()
        }
        Int::class -> {
            ByteBuffer.allocate(size*Int.SIZE_BYTES).also { buffer ->
                (this as Collection<Int>).forEach { buffer.putInt(it) }
            }.array()
        }
        Long::class -> {
            ByteBuffer.allocate(size*Long.SIZE_BYTES).also { buffer ->
                (this as Collection<Long>).forEach { buffer.putLong(it) }
            }.array()
        }
        Float::class -> {
            ByteBuffer.allocate(size*Float.SIZE_BYTES).also { buffer ->
                (this as Collection<Float>).forEach { buffer.putFloat(it) }
            }.array()
        }
        Double::class -> {
            ByteBuffer.allocate(size*Double.SIZE_BYTES).also { buffer ->
                (this as Collection<Double>).forEach { buffer.putDouble(it) }
            }.array()
        }
        Number::class -> {
            map { it.bytes() }
                    .asSequence()
                    .flatMap { sequence { yieldAll(it.iterator()) } }
                    .toList()
                    .toByteArray()
        }
        else -> error("Unknown number type ${T::class.simpleName}")
    }
}

fun Char.bytes() = ByteBuffer.allocate(Char.SIZE_BYTES).putChar(this).array()
fun Short.bytes() = ByteBuffer.allocate(Short.SIZE_BYTES).putShort(this).array()
fun Int.bytes() = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this).array()
fun Long.bytes() = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(this).array()
fun Float.bytes() = ByteBuffer.allocate(Float.SIZE_BYTES).putFloat(this).array()
fun Double.bytes() = ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(this).array()

fun UShort.bytes() = ByteBuffer.allocate(UShort.SIZE_BYTES).putShort(this.toShort()).array()
fun UInt.bytes() = ByteBuffer.allocate(Int.SIZE_BYTES).putInt(this.toInt()).array()
fun ULong.bytes() = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(this.toLong()).array()


