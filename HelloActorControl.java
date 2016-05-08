/*
 *      The HelloActorControl prototype
 *   I desparately needed an implementation for simple "programmed' animations for my
 *  ActorControl class. The whole purpose for it was as a work-around for blender animations.
 *  The simpleUpdate method implements a rudimentary walk animation based on keyFrames
 * 
 *  simpleUpdate will be refactored into separate code and simply provide tweened frame transformations
 */

package CustomActor;

import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

public class HelloActorControl extends SimpleApplication {
    // We need to store information about the keyFrame rotations
    float keyFrame[] = {-30,30,-30};    //lLeg keyFrames
    float keyFrame2[] = {30,-30,30};    //rLeg keyFrames
    float keyFrame3[] = {30,-30,30};    //lArm keyFrames
    float keyFrame4[] = {-30,30,-30};   //rArm keyFrames
    
    // This array tracks the frame number for each keyframe
    float keyFrameTime[] = {0,5,10};
    float fps = 1; // Should be renamed 'speed_factor'
    float currentFrameTime = 0f;
    float currentFrame = 0;
    
    // My custom ActorControl class
    ActorControl actor;
        
    public static void main(String[] args){
        HelloActorControl app = new HelloActorControl();
        app.start(); // start the game
    }

    @Override
    public void simpleInitApp() {
        actor = new ActorControl(this); 
        rootNode.attachChild(actor.handle);
        actor.handle.move(0,0,-5);// make the cube appear in the scene
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // Calculate the currentFrame with respect to speed
        currentFrame = currentFrameTime / (1/fps);
        float tweenLength = 0;
        float frameDiff = 0f;
        
        // Interpolate value for tween
        float perc = 0f;
        
        // Frame results from tween
        float frameValue = 0f;
        float frameValue2 = 0f;
        float frameValue3 = 0f;
        float frameValue4 = 0f;
        int tween = 1;
        if(currentFrame < keyFrameTime[1]) {
            //First tween
            tweenLength = keyFrameTime[1] - keyFrameTime[0];
            frameDiff = tweenLength - currentFrame;
            
            // Calculate the interpolation value depending on frame time
            perc = (float)(tweenLength - frameDiff) / tweenLength;
            
            // Calculate an interpolated frame (first tween)
            frameValue = FastMath.interpolateLinear(perc,keyFrame[0],keyFrame[1]);
            frameValue2 = FastMath.interpolateLinear(perc,keyFrame2[0],keyFrame2[1]);
            frameValue3 = FastMath.interpolateLinear(perc,keyFrame3[0],keyFrame3[1]);
            frameValue4 = FastMath.interpolateLinear(perc,keyFrame4[0],keyFrame4[1]);
        } else if(currentFrame < keyFrameTime[2]) {
            //Second tween
            float adjFrame = currentFrame - keyFrameTime[1];
            tweenLength = keyFrameTime[2] - keyFrameTime[1];
            frameDiff = tweenLength - adjFrame;
            
            // Calculate the interpolation value depending on frame time
            perc = (float)(tweenLength - frameDiff) / tweenLength;
            
            // Calculate an interpolated frame (second tween)
            frameValue = FastMath.interpolateLinear(perc, keyFrame[1],keyFrame[2]);
            frameValue2 = FastMath.interpolateLinear(perc, keyFrame2[1],keyFrame2[2]);
            frameValue3 = FastMath.interpolateLinear(perc, keyFrame3[1],keyFrame3[2]);
            frameValue4 = FastMath.interpolateLinear(perc, keyFrame4[1],keyFrame4[2]);
            tween = 2;
        } else if(currentFrame > keyFrameTime[2]) {
            //Loop to beginning and reset currentFrameTime
            currentFrame = currentFrame % (keyFrameTime[2] - keyFrameTime[0]);
            currentFrameTime = currentFrame * fps;
            
            //Now that the currentFrame has been adjusted to the next loop, do the first tween
            tweenLength = keyFrameTime[1] - keyFrameTime[0];
            frameDiff = tweenLength - currentFrame;
            perc = (float)(tweenLength - frameDiff) / tweenLength;
            
            // Calculate an interpolated frame (loop to first tween)
            frameValue = FastMath.interpolateLinear(perc,keyFrame[0],keyFrame[1]);
            frameValue2 = FastMath.interpolateLinear(perc,keyFrame2[0],keyFrame2[1]);
            frameValue3 = FastMath.interpolateLinear(perc,keyFrame3[0],keyFrame3[1]);
            frameValue4 = FastMath.interpolateLinear(perc,keyFrame4[0],keyFrame4[1]);
        }
        //Animate the actor according to frameValue
        actor.lLeg.setLocalRotation(new Quaternion().fromAngles(frameValue * FastMath.DEG_TO_RAD,0,0));
        actor.rLeg.setLocalRotation(new Quaternion().fromAngles(frameValue2 * FastMath.DEG_TO_RAD,0,0));
        actor.lArm.setLocalRotation(new Quaternion().fromAngles(frameValue3 * FastMath.DEG_TO_RAD,0,0));
        actor.rArm.setLocalRotation(new Quaternion().fromAngles(frameValue4 * FastMath.DEG_TO_RAD,0,0));
        //Advance currentFrameTime counter
        currentFrameTime = currentFrameTime + (tpf * 10);
        System.out.println("Frame value = " + frameValue + ". TweenFrames = " + tween + ". percent = " + perc);
    }
}
