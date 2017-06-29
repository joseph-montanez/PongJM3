package com.shabb.pongfinal.objects;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Floor {
    private Spatial obj;

    public Floor(Spatial obj) {
        this.obj = obj;
    }

    public void initGraphics(AssetManager assetManager, Node rootNode) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
//        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Diffuse", new ColorRGBA(0.75f, 0.75f, 0.75f, 1.0f));
        obj.setMaterial(mat);
        obj.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    public void initPhysics(BulletAppState bulletAppState) {
        Vector3f dimensions = ((BoundingBox) obj.getWorldBound()).getExtent(null);
        BoxCollisionShape boxShape = new BoxCollisionShape(dimensions);
        RigidBodyControl control = new RigidBodyControl(boxShape);

        //-- Setup object as static
        control.setMass(0f);
        control.setKinematic(false);
        //-- Do not slow the ball when sliding against the floor.
        control.setFriction(0f);
//        control.setGravity(new Vector3f(0,0,0));
        control.setRestitution(1f);
//        control.setDamping(0 ,0);
        control.setPhysicsRotation(obj.getWorldRotation());
        control.setPhysicsLocation(obj.getWorldTranslation());

        obj.addControl(control);
        bulletAppState.getPhysicsSpace().add(obj);
    }
}
