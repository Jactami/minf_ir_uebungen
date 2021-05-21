/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

@file:Suppress("unused", "FunctionName")

package toolkit

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

@JvmInline
value class Hash(val value: ByteArray): Hashable {
    constructor(value: String) : this(Base64.getDecoder().decode(value))

    companion object {
        fun create(vararg values: Byte) = Hash(values)
    }

    override fun toString(): String = Base64.getEncoder().encodeToString(value)

    fun convertToArrayDeclaration(): String = value.joinToString(", ", "Hash.create(", ")")

    override fun MessageDigest.update() {
        update(value)
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

fun MessageDigestSHA265(): MessageDigest = MessageDigest.getInstance("SHA-256")

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