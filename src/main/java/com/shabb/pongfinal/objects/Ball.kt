package com.shabb.pongfinal.objects

import com.jme3.asset.AssetManager
import com.jme3.bounding.BoundingBox
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.SphereCollisionShape
import com.jme3.math.Vector3f
import com.jme3.renderer.queue.RenderQueue
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.shabb.pongfinal.controls.BallControl

class Ball(private val obj: Spatial, val player1: Paddle, val player2: Paddle) {
    var control: BallControl? = null

    fun initGraphics(assetManager: AssetManager, rootNode: Node) {
        val mat = assetManager.loadMaterial("Materials/Gold.j3m")
        obj.setMaterial(mat)
        obj.shadowMode = RenderQueue.ShadowMode.CastAndReceive
    }

    fun initPhysics(bulletAppState: BulletAppState) {
        val radius = (obj.worldBound as BoundingBox).xExtent
        val sphereShape = SphereCollisionShape(radius)
        control = BallControl(sphereShape, radius, player1Scored = { ->
            player1.score.setObject(player1.score.getObject() + 1)
            resetBall()
        }, player2Scored = { ->
            player2.score.setObject(player2.score.getObject() + 1)
            resetBall()
        })
        obj.addControl(control)
        bulletAppState.physicsSpace.add(obj)
    }

    private fun resetBall() {
        val currentPos = obj.localTranslation
        //-- Reset the position but leave the Y-axis as before
        obj.setLocalTranslation(0f, currentPos.y, 0f)
        //-- Update physics world of new position
        control?.physicsRotation = obj.worldRotation
        control?.physicsLocation = obj.worldTranslation
        //-- Reset Forces
        control?.clearForces()
        //-- Randomly decide which direction to move the object
        control?.linearVelocity = if (Math.random() < 0.5) Vector3f(20f, 0f, 0f) else Vector3f(-20f, 0f, 0f)
    }
}