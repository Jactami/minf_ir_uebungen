/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package csv

import org.apache.commons.csv.CSVRecord
import kotlin.properties.PropertyDelegateProvider
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class CSVStringHeader(
    val name: String
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class CSVIndex(
    val index: Int
)

/**
 * Ignore for header generation
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class CSVIgnore

fun KProperty<*>.getCSVColumnName(): String {
    val nameByAnnotation = findAnnotation<CSVStringHeader>()
    return when {
        nameByAnnotation != null -> nameByAnnotation.name
        else -> name
    }
}

class CSVExtractor<T>(
    private val entryMapper: List<Pair<String, KProperty1<T, *>>>
) {
    val header: List<String> = entryMapper.map { it.first }
    fun extract(value: T): Iterable<*> = entryMapper.map { it.second(value) }

}

private val csvCache = mutableMapOf<KClass<*>, CSVExtractor<*>>()

@Suppress("UNCHECKED_CAST")
fun <T: Any> KClass<T>.generateCSVColumnExtractorEntry(): CSVExtractor<T> {
    if (this in csvCache){
        return csvCache.getValue(this) as CSVExtractor<T>
    }
    return synchronized(csvCache){
        csvCache.getOrPut(this){
            CSVExtractor(memberProperties.filter { it.findAnnotation<CSVIgnore>() == null }.map { it.getCSVColumnName() to it }.sortedBy { it.first })
        } as CSVExtractor<T>
    }
}


fun CSVRecord.delegate() =
    PropertyDelegateProvider<Any?, Lazy<String>> { _, property ->
        val nameByAnnotation = property.findAnnotation<CSVStringHeader>()
        val indexByAnnotation = property.findAnnotation<CSVIndex>()
        lazy {
            when {
                nameByAnnotation != null -> get(nameByAnnotation.name)
                indexByAnnotation != null -> get(indexByAnnotation.index)
                else -> get(property.name)
            }
        }
    }

inline fun <T> CSVRecord.delegate(crossinline converter: (value: String) -> T) =
    PropertyDelegateProvider<Any?, Lazy<T>> { _, property ->
        val nameByAnnotation = property.findAnnotation<CSVStringHeader>()
        val indexByAnnotation = property.findAnnotation<CSVIndex>()
        lazy {
            val value = when {
                nameByAnnotation != null -> get(nameByAnnotation.name)
                indexByAnnotation != null -> get(indexByAnnotation.index)
                else -> get(property.name)
            }
            converter(value)
        }
    }
