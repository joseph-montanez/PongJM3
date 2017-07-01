package com.shabb.pongfinal.objects

import com.jme3.asset.AssetManager
import com.jme3.bounding.BoundingBox
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.SphereCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.material.Material
import com.jme3.renderer.queue.RenderQueue
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.shabb.pongfinal.controls.BallControl

class Ball(private val obj: Spatial) {

    fun initGraphics(assetManager: AssetManager, rootNode: Node) {
        val mat = assetManager.loadMaterial("Materials/Gold.j3m")
        obj.setMaterial(mat)
        obj.shadowMode = RenderQueue.ShadowMode.CastAndReceive
    }

    fun initPhysics(bulletAppState: BulletAppState) {
        val radius = (obj.worldBound as BoundingBox).xExtent
        val sphereShape = SphereCollisionShape(radius)
        val control = BallControl(sphereShape, radius)
        obj.addControl(control)
        bulletAppState.physicsSpace.add(obj)
    }
}