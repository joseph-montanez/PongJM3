package com.shabb.pongfinal.objects

import com.jme3.bounding.BoundingBox
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial
import com.shabb.pongfinal.controls.WallControl

class Wall(obj: Spatial) : Floor(obj) {
    private val name: String? = null

    override fun initPhysics(bulletAppState: BulletAppState) {
        val dimensions = (obj.worldBound as BoundingBox).getExtent(null)
        val boxShape = BoxCollisionShape(dimensions)
        val control = WallControl(boxShape)
        control.physicsRotation = obj.worldRotation
        control.physicsLocation = obj.worldTranslation

        obj.addControl(control)
        bulletAppState.physicsSpace.add(obj)
    }
}
