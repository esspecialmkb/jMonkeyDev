/**
 *  Proof of concept for using jMonkeyBrains AI engine featuring a Minecraft model found on Blender cookie
 */

package mygame;

//MonkeyBrain imports
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.*;
import com.jme3.ai.agents.behaviors.npc.steering.*;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;

//Cubes Engine imports
import com.cubes.*;
import com.cubes.test.CubesTestAssets;

//jME imports
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.shape.Sphere;

//Misc. Java imports
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener, PhysicsCollisionListener {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    public Main(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Voxel Platform Dev");
    }
    
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
    private SeekBehavior npcSeek;
    private BulletAppState bulletAppState;
    private Spatial steveNPC;
    private Agent npc;
    private RigidBodyControl landscape;
    private BlockTerrainControl blockTerrain;
    private CharacterControl player;
    private CharacterControl steveCtrl;
    private Material proj_mat;
    
    //Camera data
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    //User Navigation using physics
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false, right=false, up=false, down=false;
    
    private ProjectileControl proj_ctrl;
    private ProjectilePhysicsControl proj_phy;
    private static final Sphere sphere;
    
    static {
        sphere = new Sphere(4,4,0.4f,true,false);
    }

    @Override
    public void simpleInitApp() {
        //Set up physics engine
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
               
        //Create minecraft engine
        CubesTestAssets.registerBlocks();
        CubesSettings blockSettings = CubesTestAssets.getSettings(this);
        blockSettings.setBlockSize(2.5f);
        blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(4,1,4));
        
        //Create voxel world/map
        blockTerrain.setBlockArea(new Vector3Int(2, 0, 2), new Vector3Int(14, 1, 14), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(16, 3, 1), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 15), new Vector3Int(16, 3, 1), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(1, 3, 15), CubesTestAssets.BLOCK_WOOD );
        blockTerrain.setBlockArea(new Vector3Int(16, 0, 1), new Vector3Int(1, 3, 15), CubesTestAssets.BLOCK_WOOD );
        
        //Add voxel world/map to collisions
        blockTerrain.addChunkListener(new BlockChunkListener(){
            @Override
            public void onSpatialUpdated(BlockChunkControl blockChunk){
                Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
                landscape = optimizedGeometry.getControl(RigidBodyControl.class);
                if(landscape == null){
                    landscape = new RigidBodyControl(0);
                    optimizedGeometry.addControl(landscape);
                    bulletAppState.getPhysicsSpace().add(landscape);
                }
                landscape.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });
        
        //Attach voxel world/map to rootNode
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
        
        //Set up first-person view with collisions
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f,3f,1);
        player = new CharacterControl(capsuleShape, 0.05f);//Step size is set in last argument
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
            
        //Place player in starting position
        player.setPhysicsLocation(new Vector3f(10,10,10));
        
        //bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
        
        
        //Load the minecraft character model from blender
        steveNPC = assetManager.loadModel("Models/MinecraftSteve/MinecraftSteve.j3o");
        steveNPC.setName("steveNPC");
        steveNPC.setLocalScale(1.5f);
        rootNode.attachChild(steveNPC);
        CapsuleCollisionShape capsuleShape2 = new CapsuleCollisionShape(1.0f,2.5f,1);
        steveCtrl = new CharacterControl(capsuleShape2,0.05f);
        steveCtrl.setFallSpeed(1);
        steveCtrl.setGravity(30);
                
        //Set up MonkeyBrains Engine and create the 'agent'/bot for steve
        brainsAppState.setApp(this);
        npc = new Agent("NPC", steveNPC);
        brainsAppState.addAgent(npc);
        //Set bullet physics
        steveCtrl.setPhysicsLocation(new Vector3f(20f, 10f, 10f));
        npc.setLocalTranslation(steveCtrl.getPhysicsLocation().subtract(0,5f,0));
        bulletAppState.getPhysicsSpace().add(steveCtrl);
        
        //Create the behavior of the npc agent
        SimpleMainBehavior mainBehavior = new SimpleMainBehavior(npc);
        npcSeek = new SeekBehavior(npc, new Vector3f(7f,2.5f,12f));
        mainBehavior.addBehavior(npcSeek);
        //setting moveSpeed, rotationSpeed, mass..
        npc.setMoveSpeed(2); 
        npc.setRotationSpeed(5);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        npc.setMass(80);
        npc.setMaxForce(0.1f);

        //agents can have only one behavior but that behavior can contain other behaviors
        npc.setMainBehavior(mainBehavior);
        //starting agents
        brainsAppState.start();
        
        //cam.setLocation(new Vector3f(5, 10, 5));
        //cam.lookAt(steveNPC.getWorldTranslation(), Vector3f.UNIT_Y);
        cam.lookAtDirection(new Vector3f(1, 0, 1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(15);
        setUpKeys();
        
        //Create Material
        proj_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        proj_mat.setColor("Color",ColorRGBA.Blue);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    }
    
    //Add input mappings to inputManager
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        //inputManager.addMapping("Shoot", new KeyTrigger (KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        
        inputManager.addListener(this, "Jump");
    }
    
    public void makeProjectile(float time){
        //Create the geometry for the projectile
        Geometry proj_geo = new Geometry("bullet projectile", sphere);
        proj_geo.setMaterial(proj_mat);
        
        //Attach geometry to rootNode
        rootNode.attachChild(proj_geo);
        //Configure position and direction vectors
        proj_geo.setLocalTranslation(cam.getLocation());
        proj_geo.lookAt(cam.getDirection(), cam.getUp());
        
        proj_ctrl = new ProjectileControl();
        //proj_phy = new ProjectilePhysicsControl();
        proj_geo.addControl(proj_ctrl);
        
        proj_ctrl.setLinearVelocity(cam.getDirection().mult(40));
        proj_ctrl.setLifetime(1f);
        
        //proj_geo.addControl(proj_phy);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
            if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
            if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
            if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) {
            player.jump();
        }
        if (binding.equals("Shoot") && !value) {
            makeProjectile(tpf);
        }
    }
    
    private Vector3f tempVect = new Vector3f();
    @Override
    public void simpleUpdate(float tpf) {
        
        camDir.set(cam.getDirection()).multLocal(0.3f);
        camLeft.set(cam.getLeft()).multLocal(0.2f);
        
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
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation().add(0,1f,0));
        
        steveCtrl.setWalkDirection(npc.getVelocity().mult(0.05f));
        npc.setLocalTranslation(steveCtrl.getPhysicsLocation().subtract(0,2.5f,0));
        
        //This code can be re-implemented inside a control, similar to ProjectileControl
        tempVect = player.getPhysicsLocation();
        tempVect.y = 2.5f;
        npcSeek.setSeekingPosition( tempVect );
        //Update the MonkeyBrains AppState
        brainsAppState.update(tpf);
    }
    /*
    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    */

    public void collision(PhysicsCollisionEvent event) {
        if ( event.getNodeA() != null ) {
            if ( event.getNodeA().getName().equals("steveNPC") ) {
                final Spatial node = event.getNodeA();
                /** ... do something with the node ... */
                node.removeFromParent();
                // col_Obj = node;
            }
        } else if ( event.getNodeB() != null ) {
            if ( event.getNodeB().getName().equals("steveNPC") ) {
                final Spatial node = event.getNodeB();
                /** ... do something with the node ... */
                node.removeFromParent();
                //col_Obj = node;
            }
        }
    }
}
