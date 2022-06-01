/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package solr

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.KClass


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
) {
    companion object {
        val delete = CommandNameJsonSerializerWrapper.create<CopyField>("delete-copy-field")
        val add = CommandNameJsonSerializerWrapper.create<CopyField>("add-copy-field")
    }

    fun toDeleteSolrCommand(json: Json = Json) = json.encodeToJsonElement(delete, this)
    fun toAddSolrCommand(json: Json = Json) = json.encodeToJsonElement(delete, this)
}

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
    class Delete(val name: FieldName): FieldCommand()

    @Serializable
    @CommandName("add-field")
    class Add(
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

    fun toSolrCommandJson() = Json.encodeToJsonElement(FieldCommandSerializerPolyWrapper.getSerializerFor(this), this)
}


inline fun <reified T: FieldCommand> JsonElement.fromSolrCommandJson(): T =
    Json.decodeFromJsonElement(FieldCommandSerializerPolyWrapper.getSerializerFor(T::class), this)

object FieldCommandSerializerPolyWrapper : JsonContentPolymorphicSerializer<FieldCommand>(FieldCommand::class){

    @OptIn(InternalSerializationApi::class)
    val subclasses = FieldCommand::class.sealedSubclasses.associateBy({ CommandName.getCommandName(it).value }, {
        CommandNameJsonSerializerWrapper(it as KClass<FieldCommand>, it.serializer())
    })

    fun <T: FieldCommand> getSerializerFor(cmd: KClass<T>): KSerializer<T> = subclasses[CommandName.getCommandName(cmd::class).value] as KSerializer<T>
    fun <T: FieldCommand> getSerializerFor(cmd: T): KSerializer<T> = subclasses[CommandName.getCommandName(cmd::class).value] as KSerializer<T>

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out FieldCommand> =
        element.jsonObject.firstNotNullOf { subclasses[it.key] }
}


@Serializable
data class Request(
    val query: String,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val params: Map<String, String>? = null,
    @EncodeDefault(EncodeDefault.Mode.NEVER) val facet: Map<String, String>? = null
)

data class Facet(
    val body: Map<String, String>
)