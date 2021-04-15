package toolkit

import java.nio.ByteBuffer


fun Double.bytes() = ByteBuffer.allocate(Double.SIZE_BYTES).putDouble(this).array()


fun String.toHash() = MessageDigestSHA265().apply {
    update(this@toHash.toByteArray(Charsets.UTF_8))
}.digest().asHash()


fun Double.toHash() = hash { update(bytes()) }