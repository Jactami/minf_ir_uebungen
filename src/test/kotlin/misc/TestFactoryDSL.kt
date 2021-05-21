/*
 * Copyright (c) 2021 Felix Engl
 * Felix Engl licenses this file to you under the MIT license.
 */

package misc

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.function.Executable
import kotlin.streams.asStream
import kotlin.test.Ignore
import kotlin.test.fail

@DslMarker
annotation class TestFactoryDSL

interface DynamicTestDefinition {
    val name: String
    fun toDynamicNode(): DynamicNode
}

abstract class DynamicTestDefinitionBase(override val name: String) : DynamicTestDefinition, Executable {
    override fun toDynamicNode(): DynamicNode {
        return DynamicTest.dynamicTest(name, this)
    }

    companion object {
        inline fun create(name: String, crossinline execution: () -> Unit): DynamicTestDefinitionBase =
                object : DynamicTestDefinitionBase(name){
                    override fun execute() { execution() }
                }
    }
}

abstract class TestsDefinition {

    @TestFactoryDSL
    abstract operator fun DynamicTestDefinition.unaryPlus(): DynamicTestDefinition

    @TestFactoryDSL
    inline infix fun String.asTest(crossinline execution: () -> Unit) =
            + object : DynamicTestDefinitionBase(this@asTest){
                override fun execute() { execution() }
            }

    @TestFactoryDSL
    inline infix fun String.asGroup(@TestFactoryDSL crossinline block: DynamicTestContainerDefinition.() -> Unit) =
            + DynamicTestContainerDefinition(this@asGroup).apply(block)

}

sealed class TestsDefinitionBase : TestsDefinition() {
    protected val tests: MutableList<DynamicTestDefinition> = mutableListOf()

    private fun addDynamicTest(toAdd: DynamicTestDefinition) = tests.add(toAdd)

    @TestFactoryDSL
    override operator fun DynamicTestDefinition.unaryPlus(): DynamicTestDefinition =
            also { addDynamicTest(it) }
}

@TestFactoryDSL
fun testFactoryDefinition(@TestFactoryDSL block: DynamicTestsRootDefinition.() -> Unit) = DynamicTestsRootDefinition().apply(block)

class DynamicTestsRootDefinition : TestsDefinitionBase(), Iterable<DynamicNode> {
    override fun iterator(): Iterator<DynamicNode> = tests.map {
        it.toDynamicNode()
    }.iterator()
}

class DynamicTestContainerDefinition(override val name: String) : TestsDefinitionBase(), DynamicTestDefinition {
    override fun toDynamicNode(): DynamicNode {
        return DynamicContainer.dynamicContainer(name, tests.asSequence().map { it.toDynamicNode() }.asStream())
    }
}