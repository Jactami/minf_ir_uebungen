package toolkit

import java.nio.ByteBuffer

fun Number.bytes(): ByteArray =
    when(this){
        is Short -> bytes()
        is Int -> bytes()
        is Long -> bytes()
        is Float -> bytes()
        is Double -> bytes()
        else -> error("Unknown number type ${this::class.simpleName}")
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


