/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomActor;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Maestro
 */
public class ActorControl {
        Application app;
        //Skeleton nodes
        public Node handle;
        public Node root;
        public Node body;
        public Node rLeg;
        public Node lLeg;
        public Node rArm;
        public Node lArm;
        public Node head;
        
        //r2 -- new Camera posNode and lookNode
        Node camPos;
        Node camLook;
        Boolean ownCamera;
        
        //Box members for actors
        Box legBox;
        Box bodyBox;
        Box armBox;
        Box headBox;
        
        //Geometies for visuals
        Geometry lLegGeom;
        Geometry rLegGeom;
        Geometry bodyGeom;
        Geometry lArmGeom;
        Geometry rArmGeom;
        Geometry headGeom;
        
        //Material data
        Material legMat;
        Material bodyMat;
        Material armMat;
        Material headMat;
        
        //AnimControl and channel
        public AnimChannel channel;
        public AnimControl control;
        
        //r2 -- new Weapons Node, Box, and Geometry
        public Node weapon;
        Box weaponBox;
        Geometry weaponGeom;
        
        public Node target_pivot;
                
        ActorControl(Application app) {
            this.legMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            this.bodyMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            this.armMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            this.headMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        
            this.legMat.setColor("Color", ColorRGBA.DarkGray);
            this.bodyMat.setColor("Color", ColorRGBA.Blue);
            this.armMat.setColor("Color", ColorRGBA.Brown);
            this.headMat.setColor("Color", ColorRGBA.Brown);
            this.app = app;
            this.initActor();
        }
        
        public void initActor(){
            //Create the nodes that are used for joints
            this.root = new Node("root");
            this.body = new Node("body");
            this.rLeg = new Node("rLeg");
            this.lLeg = new Node("lLeg");
            this.rArm = new Node("rArm");
            this.lArm = new Node("lArm");
            this.head = new Node("head");
            this.handle = new Node("handle");
                                    
            //r2 -- Add a weapons node and create empty target node
            this.weapon = new Node("weapon");
            this.target_pivot = new Node("target");
            
            //Create the relationships between the joint nodes
            this.root.attachChild(this.body);
            this.handle.attachChild(this.root);
            this.body.attachChild(this.lLeg);
            this.body.attachChild(this.rLeg);
            this.body.attachChild(this.lArm);
            this.body.attachChild(this.rArm);
            this.body.attachChild(this.head);
            this.rArm.attachChild(this.weapon);
        
            //Move joints to local positions
            this.body.move(0,3f,0);
            this.lLeg.move(0.5f,0,0);
            this.rLeg.move(-0.5f,0,0);
            this.lArm.move(1.5f,3f,0);
            this.rArm.move(-1.5f,3f,0);
            this.head.move(0,3f,0);
            this.weapon.move(0,-3f,0.75f);
        
        
            //Create the physical dimensions of the actor 'minecraft-style'
            this.legBox = new Box(0.5f, 1.5f, 0.5f);
            this.bodyBox = new Box(1, 1.5f, 0.5f);
            this.armBox = new Box(0.5f, 1.5f, 0.5f);
            this.headBox = new Box(1, 1, 1);
            this.weaponBox = new Box(0.25f,0.75f,0.25f);
        
            //Create the visual elements and add materials
            this.lLegGeom = new Geometry("lLeg", this.legBox);
            this.rLegGeom = new Geometry("rLeg", this.legBox);
            this.bodyGeom = new Geometry("Body", this.bodyBox);
            this.lArmGeom = new Geometry("lArm", this.armBox);
            this.rArmGeom = new Geometry("rArm", this.armBox);
            this.headGeom = new Geometry("Head", this.headBox);
            this.weaponGeom = new Geometry("Weapon", this.weaponBox);
            
            //Set materials
            this.lLegGeom.setMaterial(this.legMat);
            this.rLegGeom.setMaterial(this.legMat);
            this.bodyGeom.setMaterial(this.bodyMat);
            this.lArmGeom.setMaterial(this.armMat);
            this.rArmGeom.setMaterial(this.armMat);
            this.headGeom.setMaterial(this.headMat);
            
            //TODO: Give weapons their own material
            this.weaponGeom.setMaterial(this.legMat);
        
            //Set the local transforms of geometry to align with joints properly
            this.lLegGeom.move(0,-1.5f,0);
            this.rLegGeom.move(0,-1.5f,0);
            this.bodyGeom.move(0,1.5f,0);
            this.lArmGeom.move(0,-1.5f,0);
            this.rArmGeom.move(0,-1.5f,0);
            this.headGeom.move(0,1f,0);
            this.weaponGeom.move(0,0,0);
        
            //Attach geometries to nodes
            this.body.attachChild(this.bodyGeom);
            this.lLeg.attachChild(this.lLegGeom);
            this.rLeg.attachChild(this.rLegGeom);
            this.lArm.attachChild(this.lArmGeom);
            this.rArm.attachChild(this.rArmGeom);
            this.head.attachChild(this.headGeom);
            this.weapon.attachChild(this.weaponGeom);
        
            this.root.scale(1f, 1f, 1f);
            
            //this.handle.setLocalTranslation(10f, 5f, 15f);
            
            // we will need a way to attach actor to rootNode
            //rootNode.attachChild(this.handle);
        }
        
        public void initWeapon() {
            this.rArm.rotate(-90 * FastMath.DEG_TO_RAD, 0, 0);
        }
        
        public void updateControl(float tpf){
            //TODO: Call this method from input update, will also need local camera
            //Movement code
            //camDir.set(cam.getDirection()).multLocal(0.5f);
            //camLeft.set(cam.getLeft()).multLocal(0.5f);
        
            //initialize the walkDirection value so it can be recalculated
            /*  Commenting out the old updateControl code as camera data needs to 
             * be refactored into new ActorControl class
             * 
            walkDirection.set(0,0,0);
            if (left) {
                walkDirection.addLocal(camLeft);
            }
            if (right) {
                walkDirection.addLocal(camLeft.negate());
            }
            if (up) {
                walkDirection.addLocal(camDir);
            }
            if (down) {
                walkDirection.addLocal(camDir.negate());
            }
            this.handle.move(walkDirection.mult(0.3f));
            //this.handle.lookAt(camDir, Vector3f.UNIT_Y);
            //cam.setLocation(player.getPhysicsLocation().add(0,0f,0));
            cam.setLocation(new Vector3f(this.handle.getLocalTranslation().x,this.handle.getLocalTranslation().y + 8f,this.handle.getLocalTranslation().z));
            */
        }
}
