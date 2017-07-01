package com.shabb.pongfinal.objects

import com.jme3.asset.AssetManager
import com.jme3.bounding.BoundingBox
import com.jme3.bullet.BulletAppState
import com.jme3.bullet.collision.shapes.BoxCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.material.Material
import com.jme3.math.ColorRGBA
import com.jme3.renderer.queue.RenderQueue
import com.jme3.scene.Node
import com.jme3.scene.Spatial

open class Floor(protected var obj: Spatial) {

    fun initGraphics(assetManager: AssetManager, rootNode: Node) {
        val mat = Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
        mat.setBoolean("UseMaterialColors", true)
        //        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Diffuse", ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f))
        obj.setMaterial(mat)
        obj.shadowMode = RenderQueue.ShadowMode.CastAndReceive
    }

    open fun initPhysics(bulletAppState: BulletAppState) {
        val dimensions = (obj.worldBound as BoundingBox).getExtent(null)
        val boxShape = BoxCollisionShape(dimensions)
        val control = RigidBodyControl(boxShape)

        //-- Setup object as static
        control.mass = 0f
        control.isKinematic = false
        //-- Do not slow the ball when sliding against the floor.
        control.friction = 0f
        //        control.setGravity(new Vector3f(0,0,0));
        control.restitution = 1f
        //        control.setDamping(0 ,0);
        control.physicsRotation = obj.worldRotation
        control.physicsLocation = obj.worldTranslation

        obj.addControl(control)
        bulletAppState.physicsSpace.add(obj)
    }
}
