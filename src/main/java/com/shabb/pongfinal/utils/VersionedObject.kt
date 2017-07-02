package com.shabb.pongfinal.utils

interface VersionedObject<T> {
    fun getVersion(): Long

    fun getObject(): T

    fun createReference(): VersionedReference<T>
}