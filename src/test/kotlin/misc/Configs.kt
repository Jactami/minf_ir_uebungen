/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package misc

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class Test1Config(
        val pathToHandInExcel: String = "TODO: Pfad zur Abgabe xlsx/xslm.",
        // The path where you store your anfrageX.json files
        val pathToHandIn: String ="./docker/task1/ES_Angabe",
        // Set true to reset the index
        val resetIndex: Boolean = false,
        // Configure your elasic search information, usually the data bellow
        val indexName: String = "shakespeare",
        val host: String = "localhost",
        val port: UShort = 9200u,
)

@Serializable
data class Test2Config(
        val pathToHandIn: String = "TODO: Pfad zur Abgabe xlsx/xslm."
)

@Serializable
data class Test3Config(
        val pathToHandIn: String = "TODO: Pfad zur Abgabe xlsx/xslm."
)


inline fun <reified T> File.loadConfigOrFail(default: () -> T): T =
        if (!exists()){
            createNewFile()
            writeText(Json{ prettyPrint=true; encodeDefaults = true }.encodeToString(default()))
            error("Please enter the correct path at ${canonicalPath}!")
        } else Json.decodeFromString(readText())