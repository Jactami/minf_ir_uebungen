/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

@file:UseContextualSerialization(FieldCommand.Add::class, FieldCommand.Delete::class)

package solr

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.Closeable
import java.nio.file.Path
import kotlin.io.path.readText

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

    suspend fun fieldAction(command: FieldCommand) {
        val url = URLBuilder(solrUrlBase).appendPathSegments("schema").build()
        client.post(url){
            setBody(command.toSolrCommandJson())
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
            client.post(url){ setBody(copyField.toDeleteSolrCommand()) }
        }
    }

    suspend fun addCopyField(name: CollectionName, copyField: CopyField) {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "schema").build()
        client.post(url){
            setBody(copyField.toAddSolrCommand())
        }
    }

    suspend fun uploadData(name: CollectionName, path: Path) {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "upload").build()
        client.post(url) {
            parameter("commit", "true")
            setBody(path.readText())
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


fun main() {
    runBlocking {
        SolrSession().use { solr ->
            println(solr.checkHealth())
            solr.
        }
    }
}
