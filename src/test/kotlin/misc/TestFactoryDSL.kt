package misc

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.function.Executable
import kotlin.streams.asStream

@DslMarker
annotation class TestFactoryDSL

interface DynamicTestDefinition {
    fun toDynamicNode(): DynamicNode

    companion object {
        inline fun create(name: String, crossinline execution: () -> Unit) =
                object : DynamicTestDefinitionBase(name){
                    override fun execute() { execution() }
                }
    }
}

abstract class DynamicTestDefinitionBase(private val name: String) : DynamicTestDefinition, Executable {
    override fun toDynamicNode(): DynamicNode {
        return DynamicTest.dynamicTest(name, this)
    }
}

sealed class TestsDefinitionBase {
    protected val tests: MutableList<DynamicTestDefinition> = mutableListOf()

    fun addDynamicTest(toAdd: DynamicTestDefinition) = tests.add(toAdd)

    @TestFactoryDSL
    operator fun DynamicTestDefinition.unaryPlus() = addDynamicTest(this)

    @TestFactoryDSL
    inline infix fun String.asTest(@TestFactoryDSL crossinline execution: () -> Unit) =
            addDynamicTest(
                    object : DynamicTestDefinitionBase(this){
                        override fun execute() { execution() }
                    }
            )

    @TestFactoryDSL
    inline infix fun String.asGroup(@TestFactoryDSL crossinline block: DynamicTestContainerDefinition.() -> Unit) =
            addDynamicTest(DynamicTestContainerDefinition(this).apply(block))

}

fun testFactoryDefinition(@TestFactoryDSL block: DynamicTestsRootDefinition.() -> Unit) = DynamicTestsRootDefinition().apply(block)

class DynamicTestsRootDefinition() : TestsDefinitionBase(), Iterable<DynamicNode> {
    override fun iterator(): Iterator<DynamicNode> = tests.map { it.toDynamicNode() }.iterator()
}

class DynamicTestContainerDefinition(private val name: String) : TestsDefinitionBase(), DynamicTestDefinition {
    override fun toDynamicNode(): DynamicNode {
        return DynamicContainer.dynamicContainer(name, tests.asSequence().map { it.toDynamicNode() }.asStream())
    }
}