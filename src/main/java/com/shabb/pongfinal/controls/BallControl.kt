package com.shabb.pongfinal.controls

import com.jme3.bullet.PhysicsSpace
import com.jme3.bullet.PhysicsTickListener
import com.jme3.bullet.collision.PhysicsCollisionEvent
import com.jme3.bullet.collision.PhysicsCollisionListener
import com.jme3.bullet.collision.shapes.CollisionShape
import com.jme3.bullet.collision.shapes.SphereCollisionShape
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.bullet.objects.PhysicsGhostObject
import com.jme3.bullet.objects.PhysicsRigidBody
import com.jme3.math.Vector3f
import com.jme3.scene.Geometry
import com.jme3.scene.Spatial
import com.shabb.pongfinal.collisions.PaddleCollision

class BallControl(shape: CollisionShape, var radius: Float, val player1Scored: () -> Unit, val player2Scored: () -> Unit) : RigidBodyControl(shape), PhysicsCollisionListener, PhysicsTickListener {
    private var ghostObject: PhysicsGhostObject? = null
    private val vector = Vector3f()

    init {

        //-- Set initial ball velocity
        linearVelocity = Vector3f(20f, 0f, 0f)
        //-- Do not slow down the ball when sliding
        friction = 0f
        //-- Apply as little mass as possible to prevent the ball from stopping
        setMass(0.001f)
        //-- Restitution is how bounce the object is
        restitution = 1.0f
        //-- Prevent the ball from flying through paddles
        ccdMotionThreshold = 1.0f

        createGhostObject()
    }

    override fun setPhysicsSpace(space: PhysicsSpace?) {
        super.setPhysicsSpace(space)
        space?.addCollisionListener(this)
    }

    protected fun createGhostObject() {
        ghostObject = PhysicsGhostObject(SphereCollisionShape(radius + 0.5f))
    }

    override fun collision(event: PhysicsCollisionEvent) {
        val objA = event.objectA
        val objB = event.objectB

        //-- Test for collision against ball and paddle
        val collisionWithBall = objA === this || objB === this
        val collisionWithPaddle = objA is PaddleControl || objB is PaddleControl
        if (collisionWithBall && collisionWithPaddle) {
            //-- Ball and paddle has collided lets add a ghost object to detect any other collisions
            PaddleCollision(this, radius, space)
        }


        val collisionWithWall = objA is WallControl || objB is WallControl
        if (collisionWithBall && collisionWithWall) {
            val wallControl = objA as? WallControl ?: objB as WallControl
            val wallName = (objB.userObject as Spatial).name

            if (wallName == "WallRight1") {
                //-- Player one has scored, reset ball and give player a point
                println("Player 1 Scored!")
                player1Scored()
            } else if (wallName == "WallLeft1") {
                println("Player 2 Scored!")
                player2Scored()
            }
        }

    }

    override fun prePhysicsTick(space: PhysicsSpace, tpf: Float) {
        val location = physicsLocation
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
        this.applyImpulse(impulse, Vector3f.ZERO)
        println(impulse)
    }

    override fun physicsTick(space: PhysicsSpace, tpf: Float) {
        //-- Remove ghost object when we are done testing the ghost object for other collisions
        space.removeTickListener(this)
        space.remove(ghostObject)
    }
}
