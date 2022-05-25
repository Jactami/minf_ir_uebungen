/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package solr

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import java.io.Closeable

class SolrSession(
    host: String = "localhost",
    port: UInt = 8983u,
) : Closeable {

    private val solrUrlBase = URLBuilder(
        URLProtocol.HTTP,
        host,
        port.toInt(),
        pathSegments = listOf("solr")
    ).build()

    private val client = HttpClient(CIO) {
        expectSuccess = false
        developmentMode = true
        install(ContentNegotiation){
            json()
        }
    }

    suspend fun checkHealth() : Boolean {
        val data = client.get(solrUrlBase){
            url { appendPathSegments("admin", "zookeeper", "status") }
        }.body<JsonElement>()

        val responseHeaderData: ResponseHeader = Json.decodeFromJsonElement(checkNotNull(data.jsonObject["responseHeader"]){
            "The response header is missing!"
        })

        return responseHeaderData.status == 0
    }

    suspend fun createCollection(name: CollectionName) {
        val url = URLBuilder(solrUrlBase).appendPathSegments("admin", "collection").build()
        client.post(url) {
            parameter("action", "CREATE")
            parameter("name", name.value)
            parameter("numShards", 1)
            parameter("replicationFactor", 1)
        }
    }

    suspend fun deleteCollection(name: CollectionName){
        val url = URLBuilder(solrUrlBase).appendPathSegments("admin", "collection").build()
        client.post(url){
            parameter("action", "DELETE")
            parameter("name", name.value)
        }
    }

    suspend fun fieldAction(command: FieldCommands) {
        val url = URLBuilder(solrUrlBase).appendPathSegments("schema").build()
        client.post(url){
            setBody(command)
        }
    }

    suspend fun getCopyFields(name: CollectionName): CopyFieldCollection {
        val url = URLBuilder(solrUrlBase).appendPathSegments("schema", "copyfields").build()
        return client.get(url){
            parameter("wt", "json")
        }.body<CopyFieldCollection>()
    }

    override fun close() {
        client.close()
    }
}

@kotlinx.serialization.Serializable
@JvmInline
value class CollectionName(val value: String)

@kotlinx.serialization.Serializable
@JvmInline
value class FieldName(val name: String)

@kotlinx.serialization.Serializable
data class ResponseHeader(
    val status: Int,
    @SerialName("QTime") val qTime: Int
)

@kotlinx.serialization.Serializable
data class CopyFieldCollection(val copyFields: List<CopyField>): List<CopyField> by copyFields

@kotlinx.serialization.Serializable
data class CopyField(
    val source: String,
    @SerialName("dest") val destination: String,
    val maxChars: Int? = null
) {
    fun toRule() = CopyFieldRule(source, destination)
}

@kotlinx.serialization.Serializable
data class CopyFieldRule(
    val source: String,
    @SerialName("dest") val destination: String
)

@kotlinx.serialization.Serializable
sealed class FieldCommands {

    abstract class FieldCommandsActionSerializer<T: FieldCommands>(serializer: KSerializer<T>, val commandName: String):
        JsonTransformingSerializer<T>(serializer){
        override fun transformSerialize(element: JsonElement): JsonElement =
            JsonObject(
                buildMap {
                    put(commandName, element)
                }
            )
    }

    object DeleteJson : FieldCommandsActionSerializer<Delete>(Delete.serializer(), "delete-field")
    object AddJson : FieldCommandsActionSerializer<Add>(Add.serializer(), "add-field")

    companion object {
        val module = SerializersModule {
            contextual(Delete::class, DeleteJson)
            contextual(Add::class, AddJson)
        }
    }


    @kotlinx.serialization.Serializable
    class Delete(val name: FieldName): FieldCommands()

    @kotlinx.serialization.Serializable
    class Add(val name: FieldName, val type: String, val stored: Boolean, val indexed: Boolean, val multiValued: Boolean): FieldCommands()
}




fun main() {
//    runBlocking {
//        SolrSession().use { solr ->
//            println(solr.checkHealth())
//        }
//    }
//
    println(Json { prettyPrint=true; serializersModule=FieldCommands.module }.encodeToString(FieldCommands.Delete(FieldName("bla"))))
}
