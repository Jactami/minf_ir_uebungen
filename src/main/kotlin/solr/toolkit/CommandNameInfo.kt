/*
 * Copyright (c) 2022 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

@file:Suppress("OPT_IN_USAGE")

package solr.toolkit

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModuleBuilder
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

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

@ExperimentalSerializationApi
class JsonSerializerWrapper<T: Any>(
    private val serializer: KSerializer<T>,
    private val classDiscriminator: String,
    private val commandName: String = serializer.descriptor.annotations.filterIsInstance<CommandName>().first().value
) : JsonTransformingSerializer<T>(serializer){
    override fun transformDeserialize(element: JsonElement): JsonElement =
        when(val wrapped = element.jsonObject[commandName]){
            null -> {
                element
            }
            else -> {
                JsonObject(wrapped.jsonObject.toMap())
            }
        }

    override fun transformSerialize(element: JsonElement): JsonElement = buildJsonObject {
        put(commandName, JsonObject(element.jsonObject.filterKeys { classDiscriminator != it }))
    }
}



@Suppress("UNCHECKED_CAST")
abstract class CommandNameSerializerPolyWrapper<T: Any> private constructor(kClass: KClass<T>, classDiscriminator: String) : JsonContentPolymorphicSerializer<T>(kClass){
    companion object {
        private val cache = ConcurrentHashMap<String, CommandNameSerializerPolyWrapper<out Any>>()

        @Suppress("UNCHECKED_CAST")
        operator fun <T: Any> invoke(kClass: KClass<T>, classDiscriminator: String): CommandNameSerializerPolyWrapper<T> =
            cache.getOrPut(kClass.qualifiedName + classDiscriminator){
                object : CommandNameSerializerPolyWrapper<T>(kClass, classDiscriminator){}
            } as CommandNameSerializerPolyWrapper<T>
    }


    init {
        require(kClass.isSealed){
            "This can only consume sealed classes!"
        }
    }


    @OptIn(InternalSerializationApi::class)
    private val subclasses = kClass.sealedSubclasses.associateBy({ CommandName.getCommandName(it).value }, {
        JsonSerializerWrapper(it.serializer(), classDiscriminator)
    })

    fun <V: T> getSerializerForKClass(cmd: KClass<V>): KSerializer<V> = subclasses[CommandName.getCommandName(cmd).value] as KSerializer<V>

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out T> =
        when(element){
            is JsonObject -> {
                subclasses.getValue(element.keys.single())
            }
            else -> error("The element has to be a json object!")
        }
}

fun <T: Any> Json.serializeCommand(command: T): JsonElement {
    val seri = CommandNameSerializerPolyWrapper(
        command::class.superclasses.single { it.isSealed },
        this.configuration.classDiscriminator
    ) as CommandNameSerializerPolyWrapper<Any>
    return encodeToJsonElement(
        seri.getSerializerForKClass(command::class) as KSerializer<T>,
        command
    )
}

inline fun <reified T: Any> Json.deserializeCommand(jsonElement: JsonElement): T = decodeFromJsonElement(
    CommandNameSerializerPolyWrapper(
        if (T::class.isSealed) T::class else T::class.superclasses.single { it.isSealed },
        this.configuration.classDiscriminator
    ) as KSerializer<T>,
    jsonElement
)