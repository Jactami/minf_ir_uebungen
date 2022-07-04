/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

@file:OptIn(ExperimentalSerializationApi::class)

package solr

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import solr.toolkit.CommandName
import solr.toolkit.JsonSerializerWrapper
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
@JsonClassDiscriminator("disk")
sealed class CopyField {
    abstract val source: String
    @SerialName("dest") abstract val destination: String
    @EncodeDefault(EncodeDefault.Mode.NEVER) abstract val maxChars: Int?

    @CommandName("delete-copy-field")
    data class DeleteCopyField(
        override val source: String,
        override val destination: String,
        override val maxChars: Int? = null
    ): CopyField()

    @CommandName("add-copy-field")
    data class AddCopyField(
        override val source: String,
        override val destination: String,
        override val maxChars: Int? = null
    ): CopyField()
}

@Suppress("UNCHECKED_CAST")
object FieldCommandSerializerPolyWrapper : JsonContentPolymorphicSerializer<FieldCommand>(FieldCommand::class){

    @OptIn(InternalSerializationApi::class)
    val subclasses = FieldCommand::class.sealedSubclasses.associateBy({ CommandName.getCommandName(it).value }, {
        JsonSerializerWrapper(it.serializer(), "classDiscriminator")
    })

    fun <T: FieldCommand> getSerializerFor(cmd: KClass<T>): KSerializer<T> = subclasses[CommandName.getCommandName(cmd).value] as KSerializer<T>
    fun <T: FieldCommand> getSerializerFor(cmd: T): KSerializer<T> = subclasses[CommandName.getCommandName(cmd::class).value] as KSerializer<T>

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out FieldCommand> =
        when(element){
            is JsonObject -> { subclasses.getValue(element.keys.single()) }
            else -> error("The element has to be a json object!")
        }
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