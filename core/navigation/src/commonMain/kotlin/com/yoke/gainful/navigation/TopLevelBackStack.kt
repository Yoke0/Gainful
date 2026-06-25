package com.yoke.gainful.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

class TopLevelBackStack<T : Any>(startKey: T) {

    private val topLevelStacks = mutableMapOf<T, SnapshotStateList<T>>()

    init {
        topLevelStacks[startKey] = mutableStateListOf(startKey)
    }

    var activeTab by mutableStateOf(startKey)
        private set

    val backStack = mutableStateListOf(startKey)

    private fun updateBackStack() {
        backStack.clear()
        backStack.addAll(topLevelStacks.flatMap { it.value })
    }

    fun switchTab(key: T) {
        if (topLevelStacks[key] == null) {
            topLevelStacks[key] = mutableStateListOf(key)
        } else {
            topLevelStacks.apply {
                remove(key)?.let { put(key, it) }
            }
        }
        activeTab = key
        updateBackStack()
    }

    fun add(key: T) {
        topLevelStacks[activeTab]?.add(key)
        updateBackStack()
    }

    fun removeLast() {
        val stack = topLevelStacks[activeTab] ?: return
        if (stack.size > 1) {
            stack.removeLast()
            updateBackStack()
        }
    }
}
