/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package solr

import csv.StackOverflowEntry
import csv.readStackoverflowCSV
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.solr.client.solrj.impl.Http2SolrClient
import kotlin.io.path.Path


fun main() {
//    val json = Json { prettyPrint = true }
//
//    val dat = Path("C:\\Users\\Felix Engl\\Downloads\\expanded_posts_backed_stackoverflow_sample_small.csv").readStackoverflowCSV().map(::StackOverflowEntry).map { it.toSolrBean() }.toList()
//
//    println(
//        json.encodeToString(dat)
//    )

    Http2SolrClient.Builder(
        "http://localhost:8983/solr"
    ).apply {

    }.build().use { solr ->
        Path("C:\\Users\\Felix Engl\\Downloads\\expanded_posts_backed_stackoverflow_sample.csv").readStackoverflowCSV().map(::StackOverflowEntry).map { it.toSolrBean() }.forEach {
            solr.addBean("stackoverflow_v1", it)
        }
        solr.commit("stackoverflow_v1")
    }
}