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
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.Closeable
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.reflect.full.findAnnotation

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
            setBody(command.serializeToJsonWithCommandName())
        }
    }

    suspend fun getCopyFields(name: CollectionName): CopyFieldCollection {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "schema", "copyfields").build()
        return client.get(url){
            parameter("wt", "json")
        }.body()
    }

    suspend fun clearCopyFields(name: CollectionName) {
        val copyFields = getCopyFields(name)
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "schema").build()
        copyFields.forEach { copyField ->
            val rule = Json.encodeToJsonElement(copyField)
            client.post(url){
                setBody(buildJsonObject {
                    put("delete-copy-field", rule)
                })
            }
        }
    }

    suspend fun addCopyField(name: CollectionName, copyField: CopyField) {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "schema").build()

        client.post(url){
            setBody(buildJsonObject {
                put("add-copy-field", Json.encodeToJsonElement(copyField))
            })
        }
    }

    suspend fun uploadData(name: CollectionName, path: Path) {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "upload").build()
        client.post(url) {
            parameter("commit", "true")
            setBody(
                path.readText()
            )
        }
    }

    override fun close() {
        client.close()
    }
}

enum class SolrFieldType(val typeName: String){
    TextField("text_general"),
    DoubleField("pdouble"),
    IntField("pint"),
    StringField("string"),
}

@Serializable
@JvmInline
value class CollectionName(val value: String)

@Serializable
@JvmInline
value class FieldName(val name: String)

@Serializable
data class ResponseHeader(
    val status: Int,
    @SerialName("QTime") val qTime: Int
)

@Serializable
data class CopyFieldCollection(val copyFields: List<CopyField>): List<CopyField> by copyFields

@Serializable
data class CopyField(
    val source: String,
    @SerialName("dest") val destination: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val maxChars: Int? = null
)

@Serializable
sealed class FieldCommands {

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    @SerialInfo
    annotation class CommandName(val name: String)

    @Serializable
    @CommandName("delete-field")
    class Delete(val name: FieldName): FieldCommands()



    @Serializable
    @CommandName("add-field")
    class Add(
        val name: FieldName,
        val type: String,
        val stored: Boolean,
        val indexed: Boolean,
        val multiValued: Boolean,
        val docValues: Boolean
    ): FieldCommands() {
        constructor(
            name: FieldName,
            type: SolrFieldType,
            stored: Boolean,
            indexed: Boolean,
            multiValued: Boolean,
            docValues: Boolean
        ): this(
            name,
            type.typeName,
            stored,
            indexed,
            multiValued,
            docValues
        )

        companion object {
            fun primitive(name: FieldName, type: SolrFieldType) =
                Add(name, type, true, true, false, type == SolrFieldType.StringField)

            fun keyword(name: FieldName) =
                Add(name, SolrFieldType.StringField, true, true, true, true)
        }
    }
}

//TODO: Ugly hack, needs better solution.
fun FieldCommands.serializeToJsonWithCommandName(): JsonElement =
    buildJsonObject {
        val kClass = this@serializeToJsonWithCommandName::class
        val commandName = checkNotNull(kClass.findAnnotation<FieldCommands.CommandName>()){
                "The field of $kClass needs a CommandName!"
            }

        val ser = Json.encodeToJsonElement(this@serializeToJsonWithCommandName)
        put(commandName.name, buildJsonObject {
            ser.jsonObject.entries.forEach {
                if (it.key == "type") return@forEach
                put(it.key, it.value)
            }
        })
    }

fun main() {
//    runBlocking {
//        SolrSession().use { solr ->
//            println(solr.checkHealth())
//        }
//    }
}
