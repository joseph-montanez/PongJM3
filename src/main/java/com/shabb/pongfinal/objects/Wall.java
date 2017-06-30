package com.shabb.pongfinal.objects;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.shabb.pongfinal.controls.WallControl;

public class Wall extends Floor {
    private String name;

    public Wall(Spatial obj) {
        super(obj);
    }

    public void initPhysics(BulletAppState bulletAppState) {
        Vector3f dimensions = ((BoundingBox) obj.getWorldBound()).getExtent(null);
        BoxCollisionShape boxShape = new BoxCollisionShape(dimensions);
        WallControl control = new WallControl(boxShape);
        control.setPhysicsRotation(obj.getWorldRotation());
        control.setPhysicsLocation(obj.getWorldTranslation());

        obj.addControl(control);
        bulletAppState.getPhysicsSpace().add(obj);
    }
}
