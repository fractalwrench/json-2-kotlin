package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*


/**
 * Traverses a JSON tree from the bottom up in level order.
 */
internal class ReverseJsonTreeTraverser {

    fun traverse(element: JsonElement, rootName: String): Stack<TypedJsonElement> {
        val bfsStack: Stack<TypedJsonElement> = Stack()
        buildStack(bfsStack, element, rootName)
        return bfsStack
    }

    /**
     * Adds the JSON nodes to a stack using BFS. This permits a reverse level order traversal, which is used to build
     * class types from the bottom up.
     */
    private fun buildStack(bfsStack: Stack<TypedJsonElement>, parent: JsonElement, key: String?) {
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

    private fun shouldAddToStack(element: JsonElement) = element.isJsonArray || element.isJsonObject

}