/*
 * VoxelShooterTest -- Third test of Ray picking projectiles
 * Re-uses code from VoxelShooterTest_1.java
 * The voxel map now works with physics collision
 * CharacterControls work with projectile ray-casting
 * This code considers using a second character control and agent to control
 * an NPC bot. The bot,via ray-casting, can be collided with projectiles
 * There seems to be rare exceptions thrown in onSimpleUpdate loop regarding
 * an occaisional index-bounds error in ArrayList for projectiles (projList)
 * Might need to use a removal queue to move the index-shifting involved in
 * removing an item in the middle of the for loop to the end of the loop as the
 * shift of indices mid-loop causing invalid index ids at the end is the suspected 
 * source of the crash.
 */
package DoomVox;

/**
 *
 * @author Maestro
 */
import com.cubes.BlockChunkControl;
import com.cubes.BlockChunkListener;
import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeekBehavior;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;

/** Sample 8 - how to let the user pick (select) objects in the scene 
 * using the mouse or key presses. Can be used for shooting, opening doors, etc. */
public class VoxelShooterTest_2 extends SimpleApplication {

    public static void main(String[] args) {
        VoxelShooterTest_2 app = new VoxelShooterTest_2();
        app.start();
    }
    //Neccessary data for app (to be later moved to appState)
    private Node shootables;
    private Geometry mark;
    // A list to store active projectiles
    private ArrayList<Geometry> projList;
    private boolean projectileMode = true;
    // A control for voxel lib
    private BlockTerrainControl blockTerrain;
    //For physics...
    private BulletAppState bulletAppState;
    // Landscape is a rigidbodycontrol linking terrain to physics
    private RigidBodyControl landscape;
    //Character Controls for player and bot
    private CharacterControl player;
    private CharacterControl bot;
    //Spatial to copy for bots
    private Spatial steveMesh;
    //MonkeyBrains engine
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
    //The seek behavior will be refactored to work directly with CharacterControls instead of Agents (future)
    private SeekBehavior brainSeek;
    private Agent bot1;
    
    //Camera data
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    //User Navigation using physics
    private Vector3f walkDirection = new Vector3f();
    private boolean left=false, right=false, up=false, down=false;

  @Override
  public void simpleInitApp() {
    initCrossHairs(); // a "+" in the middle of the screen to help aiming
    initKeys();       // load custom key mappings
    initMark();       // a red sphere to mark the hit and blank projectile

    /** create four colored boxes and a floor to shoot at: 
     *  Every item that is 'shootable' must be attached to the shootable Node
     */
    
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //At this part, add a monkey brains object for steering behaviors
    //In the future, a steering/AI manager will be abstracted from BetterCharacterControl
    brainsAppState.setApp(this);
    
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeCube("a Dragon", -2f, 0f, 1f));
    shootables.attachChild(makeCube("a tin can", 1f, -2f, 0f));
    shootables.attachChild(makeCube("the Sheriff", 0f, 1f, -2f));
    shootables.attachChild(makeCube("the Deputy", 1f, 0f, -4f));
    // Create the voxel terrain and populate it with entities
    initBlockTerrain();
    initPlayer();
    initBots();
    //Prepare the array list
    projList = new ArrayList<Geometry>();
    
    
    // Set up camera data
    cam.setLocation(new Vector3f(0, 10, 0));
    cam.lookAtDirection(new Vector3f(0, -0.56f, 1), Vector3f.UNIT_Y);
    flyCam.setMoveSpeed(50);
  }
  
  /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        //Movement code
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
        cam.setLocation(player.getPhysicsLocation().add(0,0f,0));
        
        //bot.setWalkDirection(bot1.getVelocity().mult(tpf));
        //bot1.setLocalTranslation(bot.getPhysicsLocation().setY(2f));
        
        //This code can be re-implemented inside a control, similar to ProjectileControl
        walkDirection = player.getPhysicsLocation();
        walkDirection.y = 2.5f;
        //brainSeek.setSeekingPosition( walkDirection.mult(0) );
        //Update the MonkeyBrains AppState
        brainsAppState.update(tpf);
        
        //END MOVEMENT CODE
        
        // get list of spatials attached to shootables Node
        List<Spatial> list = shootables.getChildren();
        // make one of the cubes rotate:
        list.get(0).rotate(tpf * 1, tpf * 2, tpf *1);
        Vector3f tVect;
        float pLife;
        float pSpeed;
        //Iterate over the list of projectiles
        if(projList.size() > 0) {
            // 1. Prepare CollisionResults
            CollisionResults res = new CollisionResults();
            for(int i = 0; i < projList.size();i++) {
                //For each projectile that is alive, move, then we will cast rays
                pLife = projList.get(i).getUserData("Lifetime");
                if(pLife > 0.0) {
                    // Get temp Vector and move projectile, subtract life
                    pSpeed = projList.get(i).getUserData("Speed");
                    tVect = (Vector3f)projList.get(i).getUserData("Direction");
                    projList.get(i).move( tVect.mult(tpf * pSpeed));
                    pLife = pLife - tpf;
                    projList.get(i).setUserData("Lifetime", pLife);
                    //Re-using onAction method from sample 8
                    //2. Obtain the Ray the use. Need local translation and direction cast from user data
                    Ray r = new Ray(projList.get(i).getLocalTranslation(),(Vector3f)projList.get(i).getUserData("Direction"));
                    // 3. Collect intersections between Ray and Shootables in results list
                    shootables.collideWith(r,res);
                    // 4. Print the results (I want to anylize data instead)
                    for (int j = 0; j < res.size(); j++) {
                        // For each hit, we know distance, impact point, name of geometry.
                        float dist = res.getCollision(j).getDistance();
                        Vector3f pt = res.getCollision(j).getContactPoint();
                        //Here, we check the dist and point to check if we can count
                        //this as a hit
                        //----Encountering an occaisional OUT-OF-BOUNDS ERROR at this if statement (random, few times)
                        if( dist < 0.2 && projList.get(i) != null) {
                            //Register a hit, then remove the projectile
                            System.out.println("Hit confirmed at : " + pt);
                            //System.out.println(res.);
                            projList.get(i).removeFromParent();
                            projList.remove(i);
                            //In the future, we will subtract hit points from the shootable
                            // 5. USe the results (mark the last spot hit)
                            if (res.size() > 0) {
                                // The closest collision point is what was truly hit:
                                CollisionResult closest = res.getClosestCollision();
                                // Let's interact - we mark the hit with a red dot.
                                mark.setLocalTranslation(closest.getContactPoint());
                                System.out.println(closest.getGeometry().getName());
                                rootNode.attachChild(mark);
                            } else {
                                // No hits? Then remove the red mark.
                                rootNode.detachChild(mark);
                            }
                        }
                        //End of for loop (j)
                    }
                } else {
                    //If lifetime is less than 0.0, then we should release the projectile
                    projList.get(i).removeFromParent();
                    projList.remove(i);
                }             
                //End of for loop (i)
            }
        }
    }
    
    protected void initBots() {
        //Adding a bot
        //Mesh first
        steveMesh = assetManager.loadModel("Models/MinecraftSteve/MinecraftSteve.j3o");
        steveMesh.setName("steveNPC");
        steveMesh.setLocalScale(1.25f);
        
        //We want to be able to shoot at this bot, attach to shootables node
        shootables.attachChild(steveMesh);
        CapsuleCollisionShape capsuleShape2 = new CapsuleCollisionShape(0.7f,2f,1);
        bot = new CharacterControl(capsuleShape2,0.5f);
        bot.setJumpSpeed(10);
        bot.setFallSpeed(9);
        bot.setGravity(15);
        //MonkeyBrains
        //Create bot1 based off of steveMesh (Will soon refactor seek behavior so agent wont be neccesary)
        bot1 = new Agent("BOT_1",steveMesh);
        brainsAppState.addAgent(bot1);
        //Set physics data for bot
        bot.setPhysicsLocation(new Vector3f(16f,10f,20f));
        bulletAppState.getPhysicsSpace().add(bot);
        //Create the behavior of the npc agent
        SimpleMainBehavior mainBehavior = new SimpleMainBehavior(bot1);
        brainSeek = new SeekBehavior(bot1, new Vector3f(7f,2.5f,12f));
        mainBehavior.addBehavior(brainSeek);
        //setting moveSpeed, rotationSpeed, mass..
        bot1.setMoveSpeed(2); 
        bot1.setRotationSpeed(5);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        bot1.setMass(80);
        bot1.setMaxForce(0.5f);

        //agents can have only one behavior but that behavior can contain other behaviors
        bot1.setMainBehavior(mainBehavior);
        //starting agents
        brainsAppState.start();
    }
    
    protected void initPlayer(){
        //Set up first-person view with collisions
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.7f,4f,1);
        player = new CharacterControl(capsuleShape,0.5f);//step size is set in last argument
        player.setJumpSpeed(10);
        player.setFallSpeed(9);
        player.setGravity(15);
            
        //Place player in starting position
        //player.warp(new Vector3f(8.5f,2f,2f));
        player.setPhysicsLocation(new Vector3f(16f,10f,4f));
        
        //bulletAppState.getPhysicsSpace().add(landscape);
        bulletAppState.getPhysicsSpace().add(player);
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
            player.jump();
        }
      if (name.equals("Shoot") && !keyPressed) {
        //switch for projectileMode
        if(!projectileMode) {
            
        } else {
            //New code to generate projectile
            //The old code will be refactored to check collisions on multiple projectiles
            
            // 1. Create a projectile and attach it to the root node
            // ((Soon, we will check a cool-down value for firing rate))
            Geometry p = makeProjectile(cam.getLocation(),cam.getDirection());
            rootNode.attachChild(p);
            // 2.Add the projectile to the array list
            projList.add(p);
            // ((In the future we will play a shooting sound))
        }
        
      }
    }
  };

  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 1, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }

  /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(15, .2f, 15);
    Geometry floor = new Geometry("the Floor", box);
    floor.setLocalTranslation(0, -4, -5);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Gray);
    floor.setMaterial(mat1);
    return floor;
  }
  
  // Adding a projectile to attack a target with
  protected Geometry makeProjectile(Vector3f pos,Vector3f dir) {
      Sphere sphere = new Sphere(4, 4, 0.1f);
      Geometry projectile = new Geometry("projectile",sphere);
      projectile.setLocalTranslation(pos);
      Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      mat1.setColor("Color", ColorRGBA.Yellow);
      projectile.setMaterial(mat1);
      // Set lifetime and direction as userData
      projectile.setUserData("Lifetime", 2.0f);
      projectile.setUserData("Direction", dir);
      projectile.setUserData("Speed",20.5f);
      return projectile;
  }

  /** A red ball that marks the last spot that was "hit" by the "shot". */
  protected void initMark() {
    Sphere sphere = new Sphere(30, 30, 0.2f);
    mark = new Geometry("BOOM!", sphere);
    Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mark_mat.setColor("Color", ColorRGBA.Red);
    mark.setMaterial(mark_mat);
  }

  /** A centred plus sign to help the player aim. */
  protected void initCrossHairs() {
    setDisplayStatView(false);
    guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    BitmapText ch = new BitmapText(guiFont, false);
    ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
    ch.setText("+"); // crosshairs
    ch.setLocalTranslation( // center
      settings.getWidth() / 2 - ch.getLineWidth()/2, settings.getHeight() / 2 + ch.getLineHeight()/2, 0);
    guiNode.attachChild(ch);
  }

  public void initBlockTerrain() {
        //Create minecraft engine
        CubesTestAssets.registerBlocks();
        CubesSettings blockSettings = CubesTestAssets.getSettings(this);
        blockSettings.setBlockSize(2.0f);
        blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(4,1,4));

        //To set a block, just specify the location and the block object
        //(Existing blocks will be replaced)
        //blockTerrain.setBlock(new Vector3Int(7, 1, 0), CubesTestAssets.BLOCK_WOOD);
        //blockTerrain.setBlock(0, 0, 0, CubesTestAssets.BLOCK_GRASS); //For the lazy users :P

        //You can place whole areas of blocks too: setBlockArea(location, size, block)
        blockTerrain.setBlockArea(new Vector3Int(1, 0, 1), new Vector3Int(16, 1, 24), CubesTestAssets.BLOCK_WOOD);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 0), new Vector3Int(18, 2, 1), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(17, 1, 0), new Vector3Int(1, 2, 26), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 0), new Vector3Int(1, 2, 25), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlockArea(new Vector3Int(0, 1, 25), new Vector3Int(18, 2, 1), CubesTestAssets.BLOCK_STONE);
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
                    landscape = new RigidBodyControl(0);
                    optimizedGeometry.addControl(landscape);
                    bulletAppState.getPhysicsSpace().add(landscape);
                }
                landscape.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
            }
        });
        
        //Attach voxel world/map to rootNode
        Node terrainNode = new Node("terrain");
        terrainNode.addControl(blockTerrain);
        shootables.attachChild( terrainNode );
    }
}
