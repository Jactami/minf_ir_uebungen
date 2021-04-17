package exercise1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import toolkit.Hashable
import toolkit.update
import java.io.File
import java.security.MessageDigest

fun readResult(resultFile: File) =
        Json.decodeFromString(Result.serializer(), resultFile.readText())

@Serializable
data class Result(
        val took: Int,
        @SerialName("timed_out") val timedOut: Boolean,
        @SerialName("_shards") val shards: Shards,
        val hits: Hits,
) : Hashable {
    override fun MessageDigest.update() {
        update(hits)
    }
}

@Serializable
data class Shards(
        val total: Int,
        val successful: Int,
        val skipped: Int,
        val failed: Int,
)

@Serializable
data class Hits(
    val total: Total,
    @SerialName("max_score") val maxScore: Float?,
    val hits: List<Hit>,
) : Hashable {
    override fun MessageDigest.update() {
        update(total)
        hits.forEach { update(it) }
    }
}

@Serializable
data class Total(
        val value: Int,
        val relation: String
) : Hashable {
    override fun MessageDigest.update() {
        update(value)
        update(relation)
    }
}

@Serializable
data class Hit(
        @SerialName("_index") val index: String,
        @SerialName("_type") val type: String,
        @SerialName("_id") val id: String,
        @SerialName("_score") val score: Float,
        @SerialName("_source") val source: ShakespeareEntry,
) : Hashable {
    override fun MessageDigest.update() {
        update(source)
    }
}

@Serializable
data class ShakespeareEntry(
        val type: String,
        @SerialName("line_id") val lineId: Int,
        @SerialName("play_name") val playName: String,
        @SerialName("speech_number") val speechNumber: Int,
        @SerialName("line_number") val lineNumber: String,
        val speaker: String,
        @SerialName("text_entry") val textEntry: String,
) : Hashable {
    override fun MessageDigest.update() {
        update(type)
        update(lineId)
        update(playName)
        update(speechNumber)
        update(lineNumber)
        update(speaker)
        update(textEntry)
    }
}
