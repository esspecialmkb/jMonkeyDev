/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MapDev;

import com.cubes.BlockTerrainControl;
import com.cubes.CubesSettings;
import com.cubes.Vector3Int;
import com.cubes.test.CubesTestAssets;
import com.jme3.app.Application;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import java.io.IOException;

/**
 *
 * @author Maestro
 */
public class fileManagementDev {
    
    public class MyCustomClass implements Savable {
        private int      someIntValue;   // some custom user data
        private float    someFloatValue; // some custom user data
        private Material someJmeObject;  // some custom user data
        
         // your other code...
    
        // Here is the method that takes care of exporting MyCustomClass into data
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule capsule = ex.getCapsule(this);
            capsule.write(someIntValue,   "someIntValue",   1);
            capsule.write(someFloatValue, "someFloatValue", 0f);
            //capsule.write(someJmeObject,  "someJmeObject",  new Material());
        }
        
        // Here is the method that takes care of importing MyCustomClass from data
        public void read(JmeImporter im) throws IOException {
            InputCapsule capsule = im.getCapsule(this);
            someIntValue   = capsule.readInt(    "someIntValue",   1);
            someFloatValue = capsule.readFloat(  "someFloatValue", 0f);
            //someJmeObject  = capsule.readSavable("someJmeObject",  new Material());
        }
    }
    
    public class TerrainClass implements Savable {
        private int      someIntValue;   // some custom user data
        private float    someFloatValue; // some custom user data
        private Material someJmeObject;  // some custom user data
        
        private Node terrainNode;
        private BlockTerrainControl blockTerrain;
        
        TerrainClass() {
            //empty constructor (for now)
        }
        // your other code...
        
        // Plaaning to move initBlockTerrain into a custom class to implement saving
        // the voxel data
        public void initBlockTerrain(Application app, Node rootNode){
            CubesTestAssets.registerBlocks();
            CubesSettings blockSettings = CubesTestAssets.getSettings(app);
            blockSettings.setBlockSize(5);
            blockTerrain = new BlockTerrainControl(blockSettings, new Vector3Int(2, 1, 2));
            blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), CubesTestAssets.BLOCK_STONE);
            blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5f, CubesTestAssets.BLOCK_GRASS);
            terrainNode = new Node();
            terrainNode.addControl(blockTerrain);
            rootNode.attachChild(terrainNode);
        }
        
        // Here is the method that takes care of exporting MyCustomTerrainClass into data
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule capsule = ex.getCapsule(this);
            capsule.write(someIntValue,   "someIntValue",   1);
            capsule.write(someFloatValue, "someFloatValue", 0f);
            //capsule.write(someJmeObject,  "someJmeObject",  new Material());
        }
        
        // Here is the method that takes care of importing MyCustomTerrainClass from data
        public void read(JmeImporter im) throws IOException {
            InputCapsule capsule = im.getCapsule(this);
            someIntValue   = capsule.readInt(    "someIntValue",   1);
            someFloatValue = capsule.readFloat(  "someFloatValue", 0f);
            //someJmeObject  = capsule.readSavable("someJmeObject",  new Material());
        }
    }
}
