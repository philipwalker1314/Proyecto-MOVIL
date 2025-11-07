package com.bwalker.presupex.manager

// This object ensures all activities share the same in-memory data
object DataProvider {
    val sharedDataManager: MemoryDataManager by lazy {
        MemoryDataManager()
    }
}
