package com.shabb.pongfinal.utils

class VersionedReference<T>(val obj: VersionedObject<T> ) {
    private var lastVersion: Long = -1

    init {
        this.lastVersion = obj.getVersion()
    }

    fun getLastVersion(): Long {
        return lastVersion
    }

    fun getObjectVersion(): Long {
        return obj.getVersion()
    }

    fun needsUpdate(): Boolean {
        return lastVersion != obj.getVersion()
    }

    fun update(): Boolean {
        if (lastVersion == obj.getVersion())
            return false
        lastVersion = obj.getVersion()
        return true
    }

    fun get(): T {
        return obj.getObject()
    }
}