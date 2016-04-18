/*
 *  Beginning framework for tank prototype game entity (Coded-based mesh)
 */
package CustomActor;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author Maestro
 */
public class Tank extends SimpleApplication{
    
    //Tank body prototype is the shape used for tank body
    Box tank_body_prototype;
    Box tank_turret_prototype;
    Box tank_weapon_prototype;
    //Tank root pivot is the local origin of the whole tank
    Node tank_root_pivot;
    Node tank_turret_pivot;
    Node tank_weapon_pivot;
    //Tank body is the visible representation of the body
    Geometry tank_body;
    Geometry tank_turret;
    Geometry tank_weapon;
    //Materials used for tank parts
    Material mat;
    Material mat2;
    Material mat3;
    
    public static void main(String[] args){
        Tank app = new Tank();
        app.start(); // start the game
    }
    
    public Node createTank() {
        tank_body_prototype = new Box(2, 1, 3.5f); // create cube shape
        tank_body = new Geometry("Tank Body", tank_body_prototype);  // create cube geometry from the shape
        
        tank_turret_prototype = new Box(1, 1, 1);
        tank_turret = new Geometry("Tank Turret", tank_turret_prototype);
        
        tank_weapon_prototype = new Box(0.5f, 0.5f, 1.5f);
        tank_weapon = new Geometry("Tank Weapon", tank_weapon_prototype);
        
        //Create the materials that will be used for tabnk parts
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat2.setColor("Color", ColorRGBA.DarkGray);   // set color of material to dark gray
        
        mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat3.setColor("Color", ColorRGBA.Gray);   // set color of material to gray
        
        //Set the materials of tank parts and create pivots
        tank_body.setMaterial(mat);                   // set the cube's material
        tank_root_pivot = new Node("tank pivot");      // pivot node to rotate box
        
        tank_turret.setMaterial(mat2);
        tank_turret_pivot = new Node("turret pivot");
        
        tank_weapon.setMaterial(mat3);
        tank_weapon_pivot = new Node("weapon pivot");
        
        //attach the children to pivots, set their local positions, and attach to rootNode
        tank_root_pivot.attachChild(tank_body);                 // attach the box geometry to the pivot
        tank_body.move(0,0.5f,0);
        
        //Attach the other children before setting local position
        tank_root_pivot.attachChild(tank_turret_pivot);
        tank_turret_pivot.attachChild(tank_weapon_pivot);
        
        tank_turret_pivot.attachChild(tank_turret);
        tank_turret_pivot.move(0,2,1.5f);
        
        tank_weapon_pivot.attachChild(tank_weapon);
        tank_weapon_pivot.move(0,0,-0.5f);
        tank_weapon.move(0,0,-1.5f);
        
        
        rootNode.attachChild(tank_root_pivot);
        return tank_root_pivot;
    }

    @Override
    public void simpleInitApp() {
        createTank();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        tank_turret_pivot.rotate(0.4f * tpf,0,0);
    }
    
}
