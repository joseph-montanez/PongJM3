package com.shabb.pongfinal.collisions

import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.PhysicsTickListener
import com.jme3.bullet.collision.shapes.SphereCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.bullet.objects.PhysicsGhostObject
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f
import com.shabb.pongfinal.controls.PaddleControl

class PaddleCollision (val control: RigidBodyControl,
                       radius: Float,
                       space: PhysicsSpace
                       ) : PhysicsTickListener {
    private var ghostObject: PhysicsGhostObject? = null

    init {
        ghostObject = PhysicsGhostObject(SphereCollisionShape(radius + 0.5f))
        ghostObject!!.physicsLocation = control.getPhysicsLocation(null)
        space.add(ghostObject!!)
        space.addTickListener(this)
    }

    override fun prePhysicsTick(space: PhysicsSpace, tpf: Float) {
        val location = control.getPhysicsLocation(null)
        var impulse = Vector3f.ZERO

        for (collisionObj in ghostObject!!.overlappingObjects) {
            if (collisionObj is PhysicsRigidBody && collisionObj is PaddleControl) {
                val vector2 = collisionObj.physicsLocation
                //-- Get the local location from ball and paddle
                vector2.subtractLocal(location)
                vector2.normalizeLocal()
                //-- Get the up / down position to decide on the force to apply
                val z = vector2.getZ()
                val zForce = -1 * z / 100
                val xForce = if (vector2.getX() > 0) -0.0005f else 0.0005f
                impulse = Vector3f(xForce, 0f, zForce)
            }
        }
        control.applyImpulse(impulse, Vector3f.ZERO)
    }

    override fun physicsTick(space: PhysicsSpace, tpf: Float) {
        //-- Remove ghost object when we are done testing the ghost object for other collisions
        space.removeTickListener(this)
        space.remove(ghostObject)
    }
}