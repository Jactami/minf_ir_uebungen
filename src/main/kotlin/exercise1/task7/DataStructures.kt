package exercise1.task7

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import toolkit.Hashable
import toolkit.update
import java.io.File
import java.security.MessageDigest

@Serializable
data class Result(
        val took: Int,
        @SerialName("timed_out") val timedOut: Boolean,
        @SerialName("_shards") val shards: Shards,
        val hits: Hits,
        @SerialName("pit_id") val pitId: String? = null
) : Hashable {

    companion object {
        fun fromJson(file: File) = Json.decodeFromString(serializer(), file.readText())
    }

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
        hits.sortedBy { it.id }.forEach { update(it) }
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
        val sort: IntArray? = null,
) : Hashable {
    override fun MessageDigest.update() {
        update(source)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hit

        if (index != other.index) return false
        if (type != other.type) return false
        if (id != other.id) return false
        if (score != other.score) return false
        if (source != other.source) return false
        if (sort != null) {
            if (other.sort == null) return false
            if (!sort.contentEquals(other.sort)) return false
        } else if (other.sort != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + source.hashCode()
        result = 31 * result + (sort?.contentHashCode() ?: 0)
        return result
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
