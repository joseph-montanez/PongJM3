package com.shabb.pongfinal.utils

class VersionedHolder<T> : VersionedObject<T> {

    private var value: T? = null
    private var version: Long = 0

    constructor() {}

    constructor(initialValue: T) {
        this.value = initialValue
    }

    override fun getVersion(): Long {
        return version
    }

    fun setObject(value: T) {
        this.value = value
        incrementVersion()
    }

    fun updateObject(value: T): Boolean {
        if (this.value === value)
            return false
        if (this.value != null && this.value == value)
            return false
        setObject(value)
        return true
    }

    fun incrementVersion() {
        version++
    }

    override fun getObject(): T {
        return value!!
    }

    override fun createReference(): VersionedReference<T> {
        return VersionedReference<T>(this)
    }
}