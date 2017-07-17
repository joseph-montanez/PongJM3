package com.shabb.pongfinal.controls

import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.collision.PhysicsCollisionEvent
import com.jme3.bullet.collision.PhysicsCollisionListener
import com.jme3.bullet.collision.shapes.CollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.bullet.objects.PhysicsGhostObject
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial

class PaddleControl(shape: CollisionShape) : RigidBodyControl(shape), PhysicsCollisionListener {
    private val ghostObject: PhysicsGhostObject? = null
    private val vector = Vector3f()

    init {

        //-- Setup object as Kinematic
        isKinematic = true
        //-- Apply zero friction to not slow down the ball
        friction = 0f
        //-- Allow the ball to bounce off
        restitution = 1f
        setMass(1000f)
    }

    override fun setPhysicsSpace(space: PhysicsSpace?) {
        super.setPhysicsSpace(space)
        space?.addCollisionListener(this)
    }

    override fun collision(event: PhysicsCollisionEvent) {
        val objA = event.objectA
        val objB = event.objectB
        val wallCtrlClass = WallControl::class.java

        //-- Test for collision against ball and paddle
        val collisionWithPaddle = objA === this || objB === this
        val collisionWithWall = objA.javaClass == wallCtrlClass || objB.javaClass == wallCtrlClass
        if (collisionWithPaddle && collisionWithWall) {
            //-- Figure out which object is the paddle
            val paddle = (if (objA === this) objA.getUserObject() else objB.userObject) as Spatial

            //-- Look at the lateral friction to determine the direction of the y-axis to limit
            val lateralFriction = event.lateralFrictionDir1
            if (lateralFriction.getY() > 0.0f) {
                paddle.setUserData("hitTopWall", true)
                paddle.setUserData("hitBottomWall", false)
            } else {
                paddle.setUserData("hitTopWall", false)
                paddle.setUserData("hitBottomWall", true)
            }
        }

    }
}
