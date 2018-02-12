package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement
import java.util.*


interface TraversalDelegate {
    fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>)
}

/**
 * Traverses a JSON tree from the bottom up in level order.
 */
internal class ReverseJsonTreeTraverser(delegate: TraversalDelegate): TraversalDelegate by delegate {

    private val bfsStack: Stack<TypedJsonElement> = Stack()

    fun traverse(element: JsonElement, rootName: String) {
        buildQueue(element, rootName)
        processQueue()
    }

    /**
     * Adds the JSON nodes to a stack using BFS. This permits a reverse level order traversal, which is used to build
     * class types from the bottom up.
     */
    private fun buildQueue(element: JsonElement, key: String?, depth: Int = 0) {
        if (depth == 0) {
            bfsStack.add(TypedJsonElement(element, key!!, 0))
        }
        val newDepth = depth + 1

        val complexFields = when {
            element.isJsonObject -> {
                element.asJsonObject.entrySet()
                        .filter { shouldAddToStack(it.value) }
                        .map { TypedJsonElement(it.value, it.key, newDepth) }
            }
            element.isJsonArray -> {
                val identifier = key ?: throw IllegalStateException("Expected generated identifier for array element")
                element.asJsonArray
                        .filter { shouldAddToStack(it) }
                        .mapIndexed { index, jsonElement ->
                            TypedJsonElement(jsonElement, nameForArrayField(index, identifier), newDepth)
                        }
            }
            else -> Collections.emptyList()
        }

        complexFields.forEach { bfsStack += it }
        complexFields.forEach { buildQueue(it.jsonElement, it.name, newDepth) }
    }

    private fun nameForArrayField(index: Int, identifier: String): String =
            if (index == 0) identifier else "$identifier${index + 1}"

    private fun shouldAddToStack(element: JsonElement) = element.isJsonArray || element.isJsonObject

    /**
     * Processes JSON nodes in a reverse level order traversal, by building class types for each level of the tree.
     */
    private fun processQueue() {
        var depth = -1
        val levelQueue = LinkedList<TypedJsonElement>()

        while (bfsStack.isNotEmpty()) {
            val pop = bfsStack.pop()

            if (depth != -1 && pop.depth != depth) {
                println("Processing level $depth")
                processTreeLevel(levelQueue)
            }
            levelQueue.add(pop)
            depth = pop.depth
        }

        println("Processing level $depth")
        processTreeLevel(levelQueue)
    }

}