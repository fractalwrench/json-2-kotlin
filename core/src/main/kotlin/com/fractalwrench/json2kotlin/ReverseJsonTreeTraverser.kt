package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*


/**
 * Traverses a JSON tree from the bottom up in level order.
 */
internal class ReverseJsonTreeTraverser(delegate: TraversalDelegate) : TraversalDelegate by delegate {

    private val bfsStack: Stack<TypedJsonElement> = Stack()

    fun traverse(element: JsonElement, rootName: String) {
        buildStack(element, rootName)
        validateStackOrder()
        processQueue()
    }

    private fun validateStackOrder() {
        var maxLevel = 0
        bfsStack.forEach {
            if (maxLevel > it.level) {
                throw IllegalStateException("Stack is not in correct order: $it")
            }
            maxLevel = it.level
        }
    }

    /**
     * Adds the JSON nodes to a stack using BFS. This permits a reverse level order traversal, which is used to build
     * class types from the bottom up.
     */
    private fun buildStack(parent: JsonElement, key: String?) {
        val queue = LinkedList<TypedJsonElement>()
        queue.add(TypedJsonElement(parent, key!!, 0))

        while (queue.isNotEmpty()) {
            val element = queue.poll()
            bfsStack.push(element)

            val complexChildren = with(element) {
                when {
                    isJsonObject -> convertParent(asJsonObject, level + 1)
                    isJsonArray -> convertParent(asJsonArray, jsonKey, level + 1)
                    else -> Collections.emptyList()
                }
            }
            queue.addAll(complexChildren)
        }
    }

    /**
     * Processes JSON nodes in a reverse level order traversal,
     * by building class types for each level of the tree.
     */
    private fun processQueue() {
        var level = -1
        val levelQueue = LinkedList<TypedJsonElement>()

        while (bfsStack.isNotEmpty()) {
            val pop = bfsStack.pop()

            if (level != -1 && pop.level != level) {
                handleLevel(level, levelQueue)
            }
            levelQueue.add(pop)
            level = pop.level
        }
        handleLevel(level, levelQueue)
    }

    private fun convertParent(jsonArray: JsonArray, key: String?, depth: Int): List<TypedJsonElement> {
        val identifier = key ?:
                throw IllegalStateException("Expected generated identifier for array element")
        return jsonArray
                .filter(this::shouldAddToStack)
                .mapIndexed { index, element ->
                    TypedJsonElement(element, nameForArrayField(index, identifier), depth)
                }
    }

    private fun convertParent(jsonObject: JsonObject, depth: Int): List<TypedJsonElement> {
        return jsonObject.entrySet()
                .filter { shouldAddToStack(it.value) }
                .map { TypedJsonElement(it.value, it.key, depth) }
    }

    private fun nameForArrayField(index: Int, identifier: String): String =
            if (index == 0) identifier else "$identifier${index + 1}"

    private fun shouldAddToStack(element: JsonElement) = element.isJsonArray || element.isJsonObject

    private fun handleLevel(level: Int, levelQueue: LinkedList<TypedJsonElement>) {
        println("Processing level $level")
        processTreeLevel(levelQueue)
    }

}