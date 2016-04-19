/** 
 * 
 *  Map Tool Dev r3
 *  2nd Generation code from mapDevToolTest
 * 
 *  DONE: Load/Save ability (currently being developed in fileMAnagementDev.java)
 *  - Pressing left control now saves data to "MyTerrain.cube"
 * 
 *  The basic implementation for save/loadis complete and file saving has been tested
 *  The implementation for loading data no longer throws an exception
 * 
 *  TODO: Implement loading data at start
 *  - Pressing left shift now loads data from "MyTerrain.cube" 
 * 
 *  TODO: Create block type to register game node (i.e. spawn points, item placement, interactive objects/blocks)
 * 
 */

package MapDev;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.system.AppSettings;
import com.jme3.scene.Node;
import com.cubes.*;
import com.cubes.network.CubesSerializer;
import com.cubes.test.CubesTestAssets;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class mapDevToolTest extends SimpleApplication implements ActionListener{

    public static void main(String[] args){
        Logger.getLogger("").setLevel(Level.SEVERE);
        mapDevToolTest app = new mapDevToolTest();
        app.start();
    }

    public mapDevToolTest(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Map Dev Tool Test r3");
    }
    private Node terrainNode;
    private BlockTerrainControl blockTerrain;
    private byte[] serialBlockTerrain;
    
    /**
     * 
     *  The currentBlock data member will keep track of the currently used block
     *  0 - BLOCK_GRASS
     *  1 - BLOCK_WOOD
     *  2 - BLOCK_WOOD_FLAT
     *  3 - BLOCK_BRICK
     *  4 - BLOCK_CONNECTOR_ROD
     *  5 - BLOCK_GLASS
     *  6 - BLOCK_STONE
     *  7 - BLOCK_STONE_PILLAR
     *  8 - BLOCK_WATER
     */
    private int currentBlock;
    // This bitmap font will remind the user of the choosen block
    private BitmapText currentBlockDisplay;

    @Override
    public void simpleInitApp(){
        CubesTestAssets.registerBlocks();
        initControls();
        initBlockTerrain();
        initGUI();
        cam.setLocation(new Vector3f(-16.6f, 46, 97.6f));
        cam.lookAtDirection(new Vector3f(0.68f, -0.47f, -0.56f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(15);
        currentBlock = 0;
    }
    
    //  This save function serializes the voxel map and writes the byte data
    //directly to a file. I was having an issue writing directly to assets/models
    //folder so until it's fixed, we're saving under VoxelPlatform directory
    public void saveTerrainNode() {
        String userHome = System.getProperty("user.home");
        //File file = new File(userHome+"/Models/"+"MyTerrain.cube");
        File file = new File("MyTerrain.cube");
        serialBlockTerrain = CubesSerializer.writeToBytes(blockTerrain);
        int length = serialBlockTerrain.length;
        BufferedOutputStream outBuff = null;
        try {
          //exporter.save(serialBlockTerrain, file);
            FileOutputStream out = new FileOutputStream(file);
            outBuff = new BufferedOutputStream(out);
            //outBuff.write(length);
            System.out.println("Writing data");
            outBuff.write(serialBlockTerrain);
        } catch (IOException ex) {
          Logger.getLogger(mapDevToolTest2.class.getName()).log(Level.SEVERE, "Error: Failed to save game!", ex);
        } finally {
            if(outBuff != null) {
                try {
                    System.out.println("Closing file buffer");
                    outBuff.flush();
                    outBuff.close();
                } catch(Exception e){}
            }
        }
    }
    
    // This load function loads a pre-made voxel map and re-creates the blockTerrain 
    public void loadTerrainNode() throws IOException {
        String userHome = System.getProperty("user.home");
        
        //File file = new File(userHome+"/Models/"+"MyTerrain.cube");
        File file = new File("MyTerrain.cube");
        InputStream in = new FileInputStream(file);
        
        //  Get the file length, workaraound for determining how many elements should be
        //within the serialized byte array. In future, an integer reference preceding the
        //data can allow voxel data to exist next to other data in the same file
        long length = file.length();
        
        //  Check to ensure that we haven't exceded interger limitations
        if(length > Integer.MAX_VALUE) {
            throw new IOException("File too long");
        }
        
        //  Empty byte array to serialize imported voxel map
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        
        //  Begin reading data byte per byte and increment offset
        while(offset < bytes.length && (numRead = in.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        
        if( offset < bytes.length) {
            throw new IOException("Can't confirm file");
        }
        
        //  Close the stream and set the blockTerrain
        in.close();
        terrainNode.removeControl(blockTerrain);
        rootNode.detachChild(terrainNode);
        //  Importing map data
        System.out.println("Importing map data via CubesSerializer");
        CubesSerializer.readFromBytes(blockTerrain, bytes);
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    // Callback-helper method to update currentBlock data from onAction method
    private void changeCurrentBlock() {
        currentBlock = currentBlock + 1;
        if(currentBlock > 8) {
            currentBlock = 0;
        }
        switch(currentBlock) {
            case 0:
                currentBlockDisplay.setText("Current Block: 0 - BLOCK_GRASS");
                break;
            case 1:
                currentBlockDisplay.setText("Current Block: 1 - BLOCK_WOOD");
                break;
            case 2:
                currentBlockDisplay.setText("Current Block: 2 - BLOCK_WOOD_FLAT");
                break;
            case 3:
                currentBlockDisplay.setText("Current Block: 3 - BLOCK_BRICK");
                break;
            case 4:
                currentBlockDisplay.setText("Current Block: 4 - BLOCK_CONNECTOR_ROD");
                break;
            case 5:
                currentBlockDisplay.setText("Current Block: 5 - BLOCK_GLASS");
                break;
            case 6:
                currentBlockDisplay.setText("Current Block: 6 - BLOCK_STONE");
                break;
            case 7:
                currentBlockDisplay.setText("Current Block: 7 - BLOCK_STONE_PILLAR");
                break;
            case 8:
                currentBlockDisplay.setText("Current Block: 8 - BLOCK_WATER");
                break;
        }
    } 
    
    private void initControls(){
        inputManager.addMapping("set_block", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "set_block");
        inputManager.addMapping("remove_block", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(this, "remove_block");
        
        // Adding mapping and listener for change_block action
        inputManager.addMapping("change_block", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "change_block");
        
        // Mappings to allow saving and loading of voxel map
        inputManager.addMapping("save_block", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addListener(this, "save_block");
        inputManager.addMapping("load_block", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addListener(this, "load_block");
        
    }
    
    // initBlockTerrain() encapsulates the process of creating the voxel map
    // The original example uses an in-built noise function to randomly generate an editable terrain
    private void initBlockTerrain(){
        CubesTestAssets.registerBlocks();
        CubesSettings blockSettings = CubesTestAssets.getSettings(this);
        blockSettings.setBlockSize(5);
        blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(2, 1, 2));
        blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), CubesTestAssets.BLOCK_STONE);
        blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5f, CubesTestAssets.BLOCK_GRASS);
        terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
    }
    
    private void initGUI(){
        
        //Crosshair
        BitmapText crosshair = new BitmapText(guiFont);
        crosshair.setText("+");
        crosshair.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        crosshair.setLocalTranslation(
                (settings.getWidth() / 2) - (guiFont.getCharSet().getRenderedSize() / 3 * 2),
                (settings.getHeight() / 2) + (crosshair.getLineHeight() / 2), 0);
        guiNode.attachChild(crosshair);
        //Instructions
        BitmapText instructionsText1 = new BitmapText(guiFont);
        instructionsText1.setText("Left Click: Set");
        instructionsText1.setLocalTranslation(0, settings.getHeight(), 0);
        guiNode.attachChild(instructionsText1);
        BitmapText instructionsText2 = new BitmapText(guiFont);
        instructionsText2.setText("Right Click: Remove");
        instructionsText2.setLocalTranslation(0, settings.getHeight() - instructionsText2.getLineHeight(), 0);
        guiNode.attachChild(instructionsText2);
        BitmapText instructionsText3 = new BitmapText(guiFont);
        instructionsText3.setText("(Bottom layer is marked as indestructible)");
        instructionsText3.setLocalTranslation(0, settings.getHeight() - (2 * instructionsText3.getLineHeight()), 0);
        guiNode.attachChild(instructionsText3);
        
        //CurrentBlock indicator
        currentBlockDisplay = new BitmapText(guiFont);
        currentBlockDisplay.setText("Current Block: 0 - BLOCK_GRASS");
        currentBlockDisplay.setLocalTranslation(0, settings.getHeight() - (3 * instructionsText3.getLineHeight()), 0);
        guiNode.attachChild(currentBlockDisplay);
   }
    
    // The onAction method implements the actions that are registered from  inputManager from 
    //initControls method
    @Override
    public void onAction(String action, boolean value, float lastTimePerFrame){
        if(action.equals("set_block") && value){
            Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
            if(blockLocation != null){
                //blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_WOOD);
                //The currently selected block determines what type of block to add
                switch(currentBlock) {
                    case 0:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_GRASS);
                        break;
                    case 1:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_WOOD);
                        break;
                    case 2:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_WOOD_FLAT);
                        break;
                    case 3:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_BRICK);
                        break;
                    case 4:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_CONNECTOR_ROD);
                        break;
                    case 5:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_GLASS);
                        break;
                    case 6:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_STONE);
                        break;
                    case 7:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_STONE_PILLAR);
                        break;
                    case 8:
                        blockTerrain.setBlock(blockLocation, CubesTestAssets.BLOCK_WATER);
                        break;
                }
            }
        }
        else if(action.equals("remove_block") && value){
            Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
            //This conditional test ensures the bottom row is not removed.
            if((blockLocation != null) && (blockLocation.getY() > 0)){
                blockTerrain.removeBlock(blockLocation);
            }
        } // This action allows you to change the block that you are building with
        else if(action.equals("change_block") && value) {
            // Call the helper method: changeCurrentBlock
            changeCurrentBlock();
        } // This action calls the saveTerrainNode method to save data when LCONTROL is pressed
        else if(action.equals("save_block") && value) {
            saveTerrainNode();
        } // This action calls the loadTerrainNode method to load data
        else if(action.equals("load_block") && value) {
            try {
                loadTerrainNode();
            } catch (IOException ex) {
                Logger.getLogger(mapDevToolTest2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Vector3Int getCurrentPointedBlockLocation(boolean getNeighborLocation){
        CollisionResults results = getRayCastingResults(terrainNode);
        if(results.size() > 0){
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
            return BlockNavigator.getPointedBlockLocation(blockTerrain, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }
    
    private CollisionResults getRayCastingResults(Node node){
        Vector3f origin = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.0f);
        Vector3f direction = cam.getWorldCoordinates(new Vector2f((settings.getWidth() / 2), (settings.getHeight() / 2)), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        Ray ray = new Ray(origin, direction);
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }
}

