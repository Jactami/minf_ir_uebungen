package toolkit

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

inline class Hash(val value: ByteArray) {
    constructor(value: String) : this(Base64.getDecoder().decode(value))
    override fun toString(): String = Base64.getEncoder().encodeToString(value)
    fun convertToArrayDeclaration(): String = value.joinToString(", ", "Hash.create(", ")")

    companion object {
        fun create(vararg values: Byte) = Hash(values)
    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun ByteArray.asHash() = Hash(this)


interface Hashable {
    /**
     * Update [this] with the
     */
    fun MessageDigest.update()
}

fun hash(block: MessageDigest.() -> Unit) =
        MessageDigestSHA265().apply(block).digest().asHash()

fun MessageDigestSHA265() = MessageDigest.getInstance("SHA-256")

fun MessageDigest.updateWithDoubles(values: Collection<Double>) {
    val buffer = ByteBuffer.allocate(Double.SIZE_BYTES*values.size)
    values.forEach { buffer.putDouble(it) }
    update(buffer)
}

fun MessageDigest.update(s: String, charset: Charset = Charsets.UTF_8){
    update(s.toByteArray(charset))
}

fun MessageDigest.updateWithStrings(values: Iterable<String>) {
    values.forEach { update(it) }
}

fun MessageDigest.update(values: Collection<Number>) {
    update(values.bytes())
}

fun MessageDigest.update(hashable: Hashable) {
    hashable.apply { update() }
}

fun MessageDigest.update(number: Number) {
    update(number.bytes())
}

fun MessageDigest.update(char: Char) {
    update(char.bytes())
}

fun MessageDigest.update(number: UInt) {
    update(number.bytes())
}

fun MessageDigest.update(number: UShort) {
    update(number.bytes())
}

fun MessageDigest.update(number: ULong) {
    update(number.bytes())
}