package com.fractalwrench.json2kotlin

import java.util.*

interface TraversalDelegate {
    fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>)
}