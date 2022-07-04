/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

@file:Suppress("OPT_IN_USAGE")

package solr

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import solr.toolkit.CommandName


@Serializable
sealed class FieldCommand {

    companion object {
        fun delete(fieldName: String) = Delete(FieldName(fieldName))

        fun add(
            name: FieldName,
            type: String,
            stored: Boolean,
            indexed: Boolean,
            multiValued: Boolean,
            docValues: Boolean
        ) = Add(name, type, stored, indexed, multiValued, docValues)

        fun addPrimitive(name: FieldName, type: SolrFieldType) = Add.primitive(name, type)
        fun addKeyword(name: FieldName) = Add.keyword(name)
    }

    @Serializable
    @CommandName("delete-field")
    data class Delete(val name: FieldName): FieldCommand()

    @Serializable
    @CommandName("add-field")
    data class Add(
        val name: FieldName,
        val type: String,
        val stored: Boolean,
        val indexed: Boolean,
        val multiValued: Boolean,
        val docValues: Boolean
    ): FieldCommand() {
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

@Serializable
sealed class FieldTypeCommand {

    @Serializable
    @CommandName("add-field-type")
    data class Add(
        val name: String,
        @SerialName("class") val clazz: String,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val indexed: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val stored: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val docValues: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val sortMissingFirst: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val sortMissingLast: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val multiValued: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val required: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val useDocValuesAsStored: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val large: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val positionIncrementGap: Int? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val autoGeneratePhraseQueries: Boolean? = null,
        @EncodeDefault(EncodeDefault.Mode.NEVER) val enableGraphQueries: Boolean? = false,
//        val docValuesFormat: Any,
//        val postingsFormat: Any
    )
}