/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package solr

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SerialInfo
annotation class CommandName(val value: String) {
    companion object {
        fun getCommandName(target: KClass<*>): CommandName {
            for (ann in target.annotations){
                if (ann is CommandName) return ann
            }
            error("The class $target is missing the CommandName annotation!")
        }
    }
}
inline fun <reified T: Any> commandNameOf() = CommandName.getCommandName(T::class)
operator fun JsonObject.contains(fieldCommand: CommandName): Boolean = contains(fieldCommand.value)


open class CommandNameJsonSerializerWrapper<T: Any>(
    serializer: KSerializer<T>,
    private val commandName: String
) : JsonTransformingSerializer<T>(serializer){

    constructor(kClass: KClass<T>, serializer: KSerializer<T>): this(serializer, CommandName.getCommandName(kClass).value)

    companion object {
        inline fun <reified T: Any> create(commandName: String? = null) =
            if (commandName == null)
                CommandNameJsonSerializerWrapper<T>(T::class, serializer())
            else
                CommandNameJsonSerializerWrapper<T>(serializer(), commandName)
    }

    override fun transformDeserialize(element: JsonElement): JsonElement = element.jsonObject[commandName] ?: element

    override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
        put(commandName, JsonObject(element.jsonObject.filterNot { (k, v) -> k == ""}))
    }
}
