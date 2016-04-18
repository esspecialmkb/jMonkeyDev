/*
 *  Creating a template for character replacement. 
 *  Too much time wasted trying to fix blender-to-jMoneky joint transform issue
 */
package CustomActor;

import com.cubes.BlockChunkControl;
import com.cubes.BlockChunkListener;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
public class ActorClassDev extends SimpleApplication {
    //Old r1 code for ActorControl dev
    /**
     * r1 dev code for ActorControl -- depreciated
     * Node root;
    Node body;
    Node rLeg;
    Node lLeg;
    Node rArm;
    Node lArm;
    Node head;
    
    Box legBox;
    Box bodyBox;
    Box armBox;
    Box headBox;
    
    Geometry lLegGeom;
    Geometry rLegGeom;
    Geometry bodyGeom;
    Geometry lArmGeom;
    Geometry rArmGeom;
    Geometry headGeom;
            
    Material legMat;
    Material bodyMat;
    Material armMat;
    Material headMat;
    
    AnimChannel channel;
    AnimControl control;
    */
    //ActorControl for npc
    ActorControl npc;
    
    //Members for cube world terrain
    public RigidBodyControl landscape;
    public BlockTerrainControl blockTerrain;
    
    //Camera data
    public Vector3f camDir = new Vector3f();
    public Vector3f camLeft = new Vector3f();
    
    //User Navigation using physics
    public Vector3f walkDirection = new Vector3f();
    public boolean left=false, right=false, up=false, down=false;
    
    //The ActorControl class encapsulates the ActorSetup method and related data
    class ActorControl {
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
                
        ActorControl() {
            this.legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            this.bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            this.armMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            this.headMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
            this.legMat.setColor("Color", ColorRGBA.DarkGray);
            this.bodyMat.setColor("Color", ColorRGBA.Blue);
            this.armMat.setColor("Color", ColorRGBA.Brown);
            this.headMat.setColor("Color", ColorRGBA.Brown);
            
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
            
            this.handle.setLocalTranslation(10f, 5f, 15f);
            
            rootNode.attachChild(this.handle);
        }
        
        public void initWeapon() {
            this.rArm.rotate(-90 * FastMath.DEG_TO_RAD, 0, 0);
        }
        
        public void updateControl(float tpf){
            //TODO: Call this method from input update, will also need local camera
            //Movement code
            camDir.set(cam.getDirection()).multLocal(0.5f);
            camLeft.set(cam.getLeft()).multLocal(0.5f);
        
            //initialize the walkDirection value so it can be recalculated
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
            
        }
    }
    
    //The required main function
    public static void main(String[] args){
        ActorClassDev app = new ActorClassDev();
        app.start(); // start the game
    }
    
    //Old r1 code for MaterialSetup and ActorSetup methods
    /**
     *
     * 
    //r1 function
    public void MaterialSetup() {
        legMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        armMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        headMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        legMat.setColor("Color", ColorRGBA.Blue);
        bodyMat.setColor("Color", ColorRGBA.Red);
        armMat.setColor("Color", ColorRGBA.Brown);
        headMat.setColor("Color", ColorRGBA.White);
    }
    
    //r1 function
    public void ActorSetup() {
        //Create the nodes that are used for joints
        root = new Node("root");
        body = new Node("body");
        rLeg = new Node("rLeg");
        lLeg = new Node("lLeg");
        rArm = new Node("rArm");
        lArm = new Node("lArm");
        head = new Node("head");
        
        //Create the relationships between the joint nodes
        root.attachChild(body);
        body.attachChild(lLeg);
        body.attachChild(rLeg);
        body.attachChild(lArm);
        body.attachChild(rArm);
        body.attachChild(head);
        
        //Move joints to local positions
        //TODO: Add .move commands
        body.move(0,3f,0);
        lLeg.move(0.5f,0,0);
        rLeg.move(-0.5f,0,0);
        lArm.move(1.5f,3f,0);
        rArm.move(-1.5f,3f,0);
        head.move(0,3f,0);
        
        
        //Create the physical dimensions of the actor 'minecraft-style'
        legBox = new Box(0.5f, 1.5f, 0.5f);
        bodyBox = new Box(1, 1.5f, 0.5f);
        armBox = new Box(0.5f, 1.5f, 0.5f);
        headBox = new Box(1, 1, 1);
        
        //Create the visual elements and add materials
        lLegGeom = new Geometry("Box", legBox);
        rLegGeom = new Geometry("Box", legBox);
        bodyGeom = new Geometry("Box", bodyBox);
        lArmGeom = new Geometry("Box", armBox);
        rArmGeom = new Geometry("Box", armBox);
        headGeom = new Geometry("Box", headBox);
        
        lLegGeom.setMaterial(legMat);
        rLegGeom.setMaterial(legMat);
        bodyGeom.setMaterial(bodyMat);
        lArmGeom.setMaterial(armMat);
        rArmGeom.setMaterial(armMat);
        headGeom.setMaterial(headMat);
        
        //Set the local transforms of geometry to align with joints properly
        lLegGeom.move(0,-1.5f,0);
        rLegGeom.move(0,-1.5f,0);
        bodyGeom.move(0,1.5f,0);
        lArmGeom.move(0,-1.5f,0);
        rArmGeom.move(0,-1.5f,0);
        headGeom.move(0,1f,0);
        
        //Attach geometries to nodes
        body.attachChild(bodyGeom);
        lLeg.attachChild(lLegGeom);
        rLeg.attachChild(rLegGeom);
        lArm.attachChild(lArmGeom);
        rArm.attachChild(rArmGeom);
        head.attachChild(headGeom);
        
        root.scale(0.3f, 0.3f, 0.3f);
        
        //Attach root to rootNode
        rootNode.attachChild(root);
        
    }
    
    */
    
    public void initBlockTerrain(float blockSize) {
        //Create minecraft engine
        CubesTestAssets.registerBlocks();
        CubesSettings blockSettings = CubesTestAssets.getSettings(this);
        blockSettings.setBlockSize(blockSize);
        blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(4,1,4));

        //To set a block, just specify the location and the block object
        //(Existing blocks will be replaced)
        blockTerrain.setBlock(new Vector3Int(7, 1, 0), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(0, 0, 1), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(1, 0, 0), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(new Vector3Int(1, 0, 1), CubesTestAssets.BLOCK_STONE);
        //blockTerrain.setBlock(0, 0, 0, CubesTestAssets.BLOCK_GRASS); //For the lazy users :P

        //You can place whole areas of blocks too: setBlockArea(location, size, block)
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(7, 1, 15), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(1, 1, 0), new Vector3Int(7, 2, 1), CubesTestAssets.BLOCK_BRICK);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 1), new Vector3Int(1, 2, 15), CubesTestAssets.BLOCK_BRICK);
        blockTerrain.setBlockArea(new Vector3Int(1, 1, 15), new Vector3Int(7, 2, 1), CubesTestAssets.BLOCK_BRICK);
        blockTerrain.setBlockArea(new Vector3Int(8, 1, 1), new Vector3Int(1, 2, 15), CubesTestAssets.BLOCK_BRICK);
        //blockTerrain.removeBlockArea(Vector3Int location, Vector3Int size);

        //Removing a block works in a similar way
        //blockTerrain.removeBlock(new Vector3Int(1, 2, 1));
        //blockTerrain.removeBlock(new Vector3Int(1, 3, 1));

        //Add voxel world/map to collisions
        blockTerrain.addChunkListener(new BlockChunkListener(){
            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk){ 
                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                landscape = optimizedGeometry.getControl(RigidBodyControl.class);
                if(landscape == null){
                    optimizedGeometry.setName("terrain_b");
                    //landscape = new RigidBodyControl(0);
                    //optimizedGeometry.addControl(landscape);
                    //bulletAppState.getPhysicsSpace().add(landscape);
                }
                //landscape.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });
        
        //Attach voxel world/map to rootNode
        Node terrainNode = new Node("terrain");
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    public void initFlyCamera() {
        cam.setLocation(new Vector3f(6, 3, -0.5f));
        cam.lookAtDirection(new Vector3f(0, 0f, 1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(7);
        //flyCam.setEnabled(false);
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    }
    
     /** Declaring the "Shoot" action and mapping to its triggers. */
  private void initKeys() {
        inputManager.addMapping("Shoot",
            new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
            new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "Shoot");
  }
  
  /** Defining the "Shoot" action: Determine what was hit and how to respond. */
  private ActionListener actionListener = new ActionListener() {

    public void onAction(String name, boolean keyPressed, float tpf) {
        //Movement code
        if (name.equals("Left")) {
            if (keyPressed) { left = true; } else { left = false; }
        } else if (name.equals("Right")) {
            if (keyPressed) { right = true; } else { right = false; }
        } else if (name.equals("Up")) {
            if (keyPressed) { up = true; } else { up = false; }
        } else if (name.equals("Down")) {
            if (keyPressed) { down = true; } else { down = false; }
        } else if (name.equals("Jump")) {
            //player.jump();
        }
      if (name.equals("Shoot") && !keyPressed) {
        //switch for projectileMode
        //if(!projectileMode) {
            
        //} else {
            //New code to generate projectile
        //}
        
      }
    }
  };
    
    @Override
    public void simpleInitApp() {
        //1st revion functions: MaterialSetup() and ActorSetup()
        //MaterialSetup();
        //ActorSetup();
        
        npc = new ActorControl();
        npc.initWeapon();
        initBlockTerrain(5f);
        initFlyCamera();
        initKeys();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //head.rotate(0.4f * tpf,0,0);
        npc.updateControl(tpf);
    }
} 
