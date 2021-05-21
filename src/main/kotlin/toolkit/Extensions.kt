/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package toolkit


fun String.toHash() = MessageDigestSHA265().apply {
    update(this@toHash.toByteArray(Charsets.UTF_8))
}.digest().asHash()


fun Double.toHash() = hash { update(bytes()) }

