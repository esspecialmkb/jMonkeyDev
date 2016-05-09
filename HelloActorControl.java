/*
 *      The HelloActorControl prototype, third revision
 *      Animation code now exists within AnimationControl.java and includes transitions
 * 
 *  simpleUpdate has been refactored into separate code
 */

package CustomActor;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;

/** Sample 1 - how to get started with the most simple JME 3 application.
 * Display a blue 3D cube and view from all sides by
 * moving the mouse and pressing the WASD keys. */
public class HelloActorControl_3 extends SimpleApplication implements ActionListener,AnalogListener{
    // Old animation code refactored into AnimationControl.java
    ActorControl actor;
    AnimationControl anim;
    boolean defaultInputMappings = true;
    
    private void initControls(){
        if(defaultInputMappings == false) {
            inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_MEMORY );
            inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_HIDE_STATS );
            inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_CAMERA_POS );
            inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_EXIT );
        }
        
        inputManager.addMapping("Forward", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Move_Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Backward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Move_Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Mouse_Right", new MouseAxisTrigger(MouseInput.AXIS_X,true));
        inputManager.addMapping("Mouse_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Mouse_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("Mouse_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Mouse_Wheel_Up", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("Mouse_Wheel_Down", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        
        inputManager.addListener(this, "Forward"); 
        inputManager.addListener(this, "Move_Left"); 
        inputManager.addListener(this, "Backward"); 
        inputManager.addListener(this, "Move_Right"); 
        inputManager.addListener(this, "Mouse_Up");
        inputManager.addListener(this, "Mouse_Down");
        inputManager.addListener(this, "Mouse_Left");
        inputManager.addListener(this, "Mouse_Right");
        inputManager.addListener(this, "Mouse_Wheel_Up");
        inputManager.addListener(this, "Mouse_Wheel_Down");
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("Forward")) {
            if(isPressed) {
                anim.setCurrentAction(2);
            } else if (!isPressed) {
                anim.setCurrentAction(1);
            }
        } else if(name.equals("Backward")) {
            if(isPressed) {
                anim.setCurrentAction(2);
            } else if (!isPressed) {
                anim.setCurrentAction(1);
            } 
        } else if(name.equals("Move_Left")) {
            if(isPressed) {
                anim.setCurrentAction(2);
            } else if (!isPressed) {
                anim.setCurrentAction(1);
            }  
        } else if(name.equals("Move_Right")) {
            if(isPressed) {
                anim.setCurrentAction(2);
            } else if (!isPressed) {
                anim.setCurrentAction(1);
            }
        }
    }
    
    public void onAnalog(String name, float value, float tpf) {
        if(name.equals("Forward")) {
            
        } else if(name.equals("Backward")) {
            
        } else if(name.equals("Move_Left")) {
            
        } else if(name.equals("Move_Right")) {
           
        } else if(name.equals("Mouse_Up")) {
           
        } else if(name.equals("Mouse_Down")) {
           
        } else if(name.equals("Mouse_Left")) {
           
        } else if(name.equals("Mouse_Right")) {
            
        } else if(name.equals("Mouse_Wheel_Up")) {
           
        } else if(name.equals("Mouse_Wheel_Down")) {
            
        } 
    }
    
    //Moving animation class to AnimationControl
        
    public static void main(String[] args){
        HelloActorControl_3 app = new HelloActorControl_3();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        actor = new ActorControl(this); 
        rootNode.attachChild(actor.handle);
        actor.handle.move(0,0,-5);// make the cube appear in the scene
        cam.setLocation(new Vector3f(0,2,5));
        flyCam.setEnabled(false);
        anim = new AnimationControl(actor);
        initControls();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        anim.update(tpf);
    }
}
