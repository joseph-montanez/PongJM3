package com.shabb.pongfinal.controls

import com.jme3.bullet.collision.shapes.CollisionShape
import com.jme3.bullet.control.RigidBodyControl

class WallControl(shape: CollisionShape) : RigidBodyControl(shape) {
    init {
        //-- Setup object as static
        setMass(0f)
        isKinematic = false
        //-- Do not slow the ball when sliding against the floor.
        friction = 0f
        restitution = 1f
    }
}
