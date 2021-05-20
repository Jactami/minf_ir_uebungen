package toolkit

import io.ktor.http.*

operator fun Url.div(pathElement: String) = copy(encodedPath = if(encodedPath.endsWith('/')) "$encodedPath$pathElement" else "$encodedPath/$pathElement")