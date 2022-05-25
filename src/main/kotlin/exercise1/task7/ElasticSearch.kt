/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package exercise1.task7

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import toolkit.div
import java.io.Closeable
import java.io.File


/**
 * Creates a [HttpClient] for an ES-Instance running on [host]:[port]
 * for teh given [indexName].
 */
class ESIndexSession(
        indexName: String,
        host: String = "localhost",
        port: UShort = 9200u,
): AutoCloseable {

    //client
    private val client = HttpClient(CIO) {
        expectSuccess = false
        install(ContentNegotiation){
            json()
        }
    }

    /**
     * The url without the index-name
     */
    val baseUrl = URLBuilder(
        URLProtocol("http", 9200),
        host,
        port.toInt(),
    ).apply {
        set {
            encodedPath = "/"
        }
    }.build()

    /**
     * The urls of the index
     */
    val indexUrl = baseUrl / indexName

    // Helper function
    private val HttpResponse.successful get() = status.value in 200..299

    /**
     * Check if the index exists
     */
    suspend fun exists() =
            client.head(indexUrl).successful

    /**
     * Delete the index
     */
    suspend fun delete() =
            client.delete(indexUrl).successful

    /**
     * Create the index
     */
    suspend fun create(mapping: File, data: File): Boolean {

        require(mapping.isFile){
            "${mapping.canonicalPath} has to be a file."
        }

        require(data.isFile){
            "${data.canonicalPath} has to be a file."
        }

        return client.put(indexUrl).successful &&
                client.put(indexUrl/"_mapping"){
                    contentType(ContentType.parse("application/x-ndjson"))
                    setBody(mapping.readText())
                }.successful &&
                client.put(indexUrl/"_bulk"){
                    contentType(ContentType.parse("application/x-ndjson"))
                    setBody(data.readBytes())
                }.successful
    }

    /**
     * Execute a query
     */
    suspend fun query(file: File, size: Int = 10000): Result {
        require(file.isFile){
            "${file.canonicalPath} has to be a file."
        }
        return query(Json.parseToJsonElement(file.readText()), size)
    }

    /**
     * Execute a query
     */
    suspend fun query(query: JsonElement, size: Int = 10000): Result {
        val modifiedQuery = query.jsonObject.toMutableMap().let { bodyAsMutableMap ->
            if ("size" !in bodyAsMutableMap){
                bodyAsMutableMap["size"] = JsonPrimitive(size)
            }
            JsonObject(bodyAsMutableMap)
        }
        return client.get(indexUrl/"_search"){
            contentType(ContentType.Application.Json)
            setBody(modifiedQuery)
        }.body()
    }

    /**
     * A class representing a Point in Time, has to be managed by ES.
     * Therefore a private constructor.
     */
    @Serializable
    class Pit private constructor(val id: String){
        override fun toString(): String =
                "Pit($id)"
    }

    /**
     * Create a pit
     */
    private suspend fun createPit(duration: String) =
            client.post(indexUrl/"_pit"){
                parameter("keep_alive", duration)
            }.body<Pit>()

    /**
     * Delete a pit
     */
    private suspend fun Pit.delete() =
            client.delete(baseUrl/"_pit"){
                contentType(ContentType.Application.Json)
                setBody(this@delete)
            }.successful

    /**
     * Runa a paginated query
     */
    suspend fun queryPaginated(file: File) = queryPaginated(Json.parseToJsonElement(file.readText()))

    /**
     * Runa a paginated query
     */
    suspend fun queryPaginated(query: JsonElement): List<Result> {

        TODO("This function is not finished, neets the resume concept.")

//        val pit = createPit("1m")
//        return try {
//            val modifiedQuery = query.jsonObject.toMutableMap().let { bodyAsMutableMap ->
//                if ("size" !in bodyAsMutableMap){
//                    bodyAsMutableMap["size"] = JsonPrimitive(50)
//                }
//                if ("pit" !in bodyAsMutableMap){
//                    bodyAsMutableMap["pit"] = buildJsonObject {
//                        put("id", pit.id)
//                        put("keep_alive", "1m")
//                    }
//                }
//                if("sort" !in bodyAsMutableMap){
//                    bodyAsMutableMap["sort"] = buildJsonArray {
//                        add(
//                                buildJsonObject {
//                                    put("_shard_doc", "desc")
//                                }
//                        )
//                    }
//                }
//                JsonObject(bodyAsMutableMap)
//            }
//            println(modifiedQuery.toString())
//            client.get<HttpResponse>(indexUrl/"_search"){
//                contentType(ContentType.Application.Json)
//                body = modifiedQuery
//            }.also { println(it.readText()) }
//
//            client.get(indexUrl/"_search"){
//                contentType(ContentType.Application.Json)
//                body = modifiedQuery
//            }
//        } finally {
//            pit.delete()
//        }
    }

    override fun close() {
        client.close()
    }
}
