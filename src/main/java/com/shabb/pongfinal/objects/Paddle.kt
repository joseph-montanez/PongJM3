package com.shabb.pongfinal.objects

import com.jme3.asset.AssetManager
import com.jme3.bounding.BoundingBox
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.math.Vector3f
import com.jme3.renderer.queue.RenderQueue
import com.jme3.scene.Node
import com.jme3.scene.Spatial
import com.shabb.pongfinal.controls.PaddleControl
import com.shabb.pongfinal.utils.VersionedHolder
import com.shabb.pongfinal.utils.VersionedReference

class Paddle(private val name: String, val obj: Spatial) {
    private var control: RigidBodyControl? = null
    private val pendingMove: VersionedHolder<Vector3f> = VersionedHolder<Vector3f>()
    private val pendingMoveRef: VersionedReference<Vector3f> = pendingMove.createReference()
    val score = VersionedHolder<Int>(0)
    val scoreRef = score.createReference()

    init {
        obj.setUserData("score", score.getObject())
    }

    fun initGraphics(assetManager: AssetManager, rootNode: Node) {
        val mat = Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
        mat.setBoolean("UseMaterialColors", true)
        mat.setColor("Diffuse", ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f))
        obj.setMaterial(mat)
        obj.shadowMode = RenderQueue.ShadowMode.CastAndReceive
    }

    fun update(tpf: Float, bulletAppState: BulletAppState) {
        //-- Review the pending move to see if the paddle has hit a wall
        if (pendingMoveRef.update()) {
            val hitTopWall = obj.getUserData<Boolean>("hitTopWall")
            val hitBottomWall = obj.getUserData<Boolean>("hitBottomWall")

            //-- If the paddle has hit the wall, then zero out the direction it hit
            val pos = pendingMove.getObject()
            if (hitTopWall != null && hitTopWall) {
                pos.setZ(Math.min(0.0f,pos.getZ()))
                obj.setUserData("hitTopWall", false)
            } else if (hitBottomWall != null && hitBottomWall) {
                pos.setZ(Math.max(0.0f, pos.getZ()))
                obj.setUserData("hitBottomWall", false)
            }

            //-- Apply the movement
            val v = obj.localTranslation.add(pos)
            obj.setLocalTranslation(v.x, v.y, v.z)
        }

        //-- Update control (physics object) with current location and rotation
        if (control != null) {
            control!!.physicsRotation = obj.worldRotation
            control!!.physicsLocation = obj.worldTranslation
        }
    }

    fun addLocalTranslation(x: Float, y: Float, z: Float) {
        pendingMove.setObject(Vector3f(x, y, z))
    }

    fun initPhysics(bulletAppState: BulletAppState) {
        val dimensions = (obj.worldBound as BoundingBox).getExtent(null)
        val boxShape = BoxCollisionShape(dimensions)
        control = PaddleControl(boxShape)
        control!!.physicsRotation = obj.worldRotation
        control!!.physicsLocation = obj.worldTranslation
        obj.addControl(control!!)
        bulletAppState.physicsSpace.add(obj)
    }
}
