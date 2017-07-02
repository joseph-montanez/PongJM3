package com.shabb.pongfinal.utils

import org.junit.Assert.*
import org.junit.Test

class VersionedReferenceTest {

    @Test
    fun canIVersion() {
        val vObj = VersionedHolder<Int>(3)
        val ref = vObj.createReference()
        assertEquals(vObj.getVersion(), 0);
        vObj.setObject(4)
        assertEquals(vObj.getVersion(), 1);
        assertEquals(vObj.getObject(), 4);
        assertEquals(ref.update(), true)
        assertEquals(ref.update(), false)
    }
}