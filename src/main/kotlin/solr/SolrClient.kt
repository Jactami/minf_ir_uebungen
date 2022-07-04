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
import solr.toolkit.deserializeCommand
import solr.toolkit.serializeCommand
import java.io.Closeable
import kotlin.io.path.Path
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.readText

val json = Json {
    classDiscriminator = "commandType"
}

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
            json(json)
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
            setBody(json.serializeCommand(command))
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
            client.post(url){ setBody(copyField) }
        }
    }

    suspend fun addCopyField(name: CollectionName, copyField: CopyField) {
        val url = URLBuilder(solrUrlBase).appendPathSegments(name.value, "schema").build()
        client.post(url){
            setBody(json.serializeCommand(copyField))
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

enum class SolrFieldType(val typeName: String, val classType: String){
    TextField("text_general", "solr.TextField"),
    DoubleField("pdouble", "solr.DoublePointField"),
    IntField("pint", "solr.IntPointField"),
    StringField("string", "solr.StrField"),
}


@ExperimentalStdlibApi
fun main() {
    val regex = Regex("""\d+,\d+,\d+,\d+,.*""")
    var docCount = 0
    Path("C:\\Users\\Felix Engl\\Downloads\\expanded_posts_backed.csv").inputStream().bufferedReader().use {
        while(docCount < 50){
            val line = it.readLine()
            if (regex.matchesAt(line, 0)){
                docCount++
            }
            println(line)
        }
    }


//    val elem = json.serializeCommand<FieldCommand>(
//        FieldCommand.add(
//            FieldName("text"),
//            SolrFieldType.TextField.typeName,
//            stored = true,
//            indexed = true,
//            multiValued = false,
//            docValues = true
//        )
//    )
//
//    println(elem)
//
//    println(
//        json.deserializeCommand<FieldCommand>(elem)
//    )

    return
    runBlocking {
        SolrSession().use { solr ->
            println(solr.checkHealth())
            solr.createCollection(
                CollectionName("supertoll")
            )
            solr.fieldAction(
                FieldCommand.add(
                    FieldName("text"),
                    SolrFieldType.TextField.typeName,
                    stored = true,
                    indexed = true,
                    multiValued = false,
                    docValues = true
                )
            )
        }
    }
}
