/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package toolkit

import io.ktor.http.*

operator fun Url.div(pathElement: String) = copy(encodedPath = if(encodedPath.endsWith('/')) "$encodedPath$pathElement" else "$encodedPath/$pathElement")