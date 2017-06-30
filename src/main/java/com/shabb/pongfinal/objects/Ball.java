package com.shabb.pongfinal.objects;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.shabb.pongfinal.controls.BallControl;

public class Ball {
    private Spatial obj;

    public Ball(Spatial obj) {
        this.obj = obj;
    }

    public void initGraphics(AssetManager assetManager, Node rootNode) {
        Material mat = assetManager.loadMaterial("Materials/Gold.j3m");
        obj.setMaterial(mat);
        obj.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void initPhysics(BulletAppState bulletAppState) {
        float radius = ((BoundingBox)obj.getWorldBound()).getXExtent();
        SphereCollisionShape sphereShape = new SphereCollisionShape(radius);
        RigidBodyControl control = new BallControl(sphereShape, radius);
        obj.addControl(control);
        bulletAppState.getPhysicsSpace().add(obj);
    }
}