/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package csv

import org.apache.commons.csv.CSVRecord
import org.apache.solr.client.solrj.beans.Field


class StackOverflowEntry(wrapped: CSVRecord) {

    @CSVStringHeader("id_community")
    val idCommunity by wrapped.delegate(String::toUByte)

    val id by wrapped.delegate(String::toLong)

    @CSVStringHeader("post_type_id")
    val postTypeId by wrapped.delegate(String::toInt)

    @CSVStringHeader("parent_id")
    val parentId by wrapped.delegate(String::toLongOrNull)

    val body by wrapped.delegate()

    val title by wrapped.delegate { it.ifBlank { null } }

    @CSVStringHeader("community_name")
    val communityName by wrapped.delegate{it.trim()}

    @CSVIgnore
    val tags by wrapped.delegate { value ->
        regexPattern.matcher(value).let { matcher ->
            buildList<String> {
                while (matcher.find()){
                    add(matcher.group(1))
                }
            }.ifEmpty { null }
        }
    }
    @CSVStringHeader("tags") val tagsPresented by lazy {
        tags?.joinToString(" ") { "<$it>" }
    }

    companion object {
        private val regexPattern = Regex("""<([^>]+)>""").toPattern()
        val extractor = StackOverflowEntry::class.generateCSVColumnExtractorEntry()
    }

    override fun toString(): String {
        return "StackOverflowEntry(idCommunity=$idCommunity, id=$id, postTypeId=$postTypeId, parentId=$parentId, body='$body', title=$title, communityName='$communityName', tags=$tags)"
    }

    fun toRecord(): Iterable<*> = extractor.extract(this)

    @kotlinx.serialization.Serializable
    data class SolrBean(
        @Field var title: String,
        @Field var body: String,
        @Field var tags: List<String>
    )

    fun toSolrBean() = SolrBean(title ?: "", body, tags ?: emptyList())
}