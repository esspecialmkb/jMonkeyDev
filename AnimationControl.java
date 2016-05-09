/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CustomActor;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

/**
 *
 * @author Maestro
 */
// The basic animation code works for walk animation
    // Now, we need to move it to its own class
    public class AnimationControl {
        // Refactoring keyFrame members
        // Right now, walk animation is hard-coded
        // Index 0 is now used as a place holder for transitions frames
        // Frame index 4 is for standing
        protected float keyFrameRoot[] = {0,0,0,0,0};          //root keyFrames
        protected float keyFrameBody[] = {0,0,0,0,0};
        protected float keyFramelLeg[] = {0,-60,60,-60,0};    //lLeg keyFrames
        protected float keyFramerLeg[] = {0,60,-60,60,0};    //rLeg keyFrames
        protected float keyFramelArm[] = {0,90,-90,90,0};    //lArm keyFrames
        protected float keyFramerArm[] = {0,-90,90,-90,0};   //rArm keyFrames
        protected float keyFrameHead[] = {0,0,0,0,0};
        
        // Members to store the current calculated (tweened) frame
        protected float frameValueRoot = 0f;
        protected float frameValueBody = 0f;
        protected float frameValuelLeg = 0f;
        protected float frameValuerLeg = 0f;
        protected float frameValuelArm = 0f;
        protected float frameValuerArm = 0f;
        protected float frameValueHead = 0f;

        protected float keyFrameTime[] = {-5,0,5,10,0};
        protected float fps = 1;
        protected float currentFrameTime = 0f;
        protected float currentFrame = 0;
        
        // Members used for current frame calculations
        protected float tweenLength = 0;
        protected float frameDiff = 0f;
        protected float tweenTime = 0f;
        protected int prevAction = 1;
        protected int currentAction = 1;
        protected int nextAction = 0;
        
        protected float transitionLength = 0f;
        protected float transitionFrameTime = 0f;
        
        // Flag to keep track of transition state
        protected boolean transition = false;
        protected boolean inTransition = false;
        // Flag to see if tween frames need to be calculated
        protected boolean isAnimating = false;
        
        int tween = 0;
        
        // Interpolate value for tween
        protected float perc = 0f;
        // Need a reference to ActorControl
        ActorControl actor;
        
        // Constructor
        public AnimationControl(ActorControl a) {
            actor = a;
        }
        
        public void setCurrentAction(int action) {
            if(currentAction == 0) {
                currentAction = action;
                //prevAction = action;
                isAnimating = true;
                transition = true;
            }else {
            nextAction = action;
            //currentAction = action;
            //If changing from stand action, isAnimating will be false, change to true
            isAnimating = true;
            //We need to let the update loop know that we need to calculate transition
            //tweening for a moment before starting the new animation
            transition = true;
            }
        }
        
        // Set the current frame values to the ActorControl
        protected void setActorFrame() {
            actor.root.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.body.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.lLeg.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.rLeg.setLocalRotation(new Quaternion().fromAngles(frameValuerLeg * FastMath.DEG_TO_RAD,0,0));
            actor.lArm.setLocalRotation(new Quaternion().fromAngles(frameValuelArm * FastMath.DEG_TO_RAD,0,0));
            actor.rArm.setLocalRotation(new Quaternion().fromAngles(frameValuerArm * FastMath.DEG_TO_RAD,0,0));
            actor.head.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
        }
        
        protected void setTransition() {
            //The transition will use keyFrame[0] to use currentFrame as first frame in transition
            //Multiple animations will have each first frame referenced to time=0
            //That should allow us to use a negative time value to insert this frame BEFORE the first frame
            float angles[] = new float[3];
            
            //Grab the currentRotation values and place them into the transition frame (index = 0)
            actor.root.getLocalRotation().toAngles(angles);
            keyFrameRoot[0] = angles[1] * FastMath.RAD_TO_DEG;
            
            actor.body.getLocalRotation().toAngles(angles);
            keyFrameBody[0] = angles[1] * FastMath.RAD_TO_DEG;
            
            actor.lLeg.getLocalRotation().toAngles(angles);
            keyFramelLeg[0] = angles[0] * FastMath.RAD_TO_DEG;
            
            actor.rLeg.getLocalRotation().toAngles(angles);
            keyFramerLeg[0] = angles[0] * FastMath.RAD_TO_DEG;
            
            actor.lArm.getLocalRotation().toAngles(angles);
            keyFramelArm[0] = angles[0] * FastMath.RAD_TO_DEG;
            
            actor.rArm.getLocalRotation().toAngles(angles);
            keyFramerArm[0] = angles[0] * FastMath.RAD_TO_DEG;
            
            actor.head.getLocalRotation().toAngles(angles);
            keyFrameHead[0] = angles[0] * FastMath.RAD_TO_DEG;
            
            //We set the time for this frame to a negative value.
            keyFrameTime[0] = -1.5f; 
            
            //Set the transition flag to true so we can calculate transition tweens instead of action tweens
            inTransition = true; 
            transition = false;
            currentFrameTime = keyFrameTime[0];
            currentFrame = currentFrameTime / (1/fps);
        }
        
        protected void doTransition(float tpf) {
            // Refactoring doWalkAction to use index 0 as start frame
            // Transition to stand goes to frame index 4
            // Transition to walk goes to frame index 1
            switch(nextAction) {
                case 1: //nextAction == Stand
                    //Transition to frame index 4
                    tweenLength = (keyFrameTime[4] - keyFrameTime[0]);
                    frameDiff = tweenLength - currentFrame;

                    // Calculate the interpolation value depending on frame time
                    perc = (float)(tweenLength - frameDiff) / tweenLength;
                    perc = perc + 1;
                    frameValuelLeg = FastMath.interpolateLinear(perc,keyFramelLeg[0],keyFramelLeg[4]);
                    frameValuerLeg = FastMath.interpolateLinear(perc,keyFramerLeg[0],keyFramerLeg[4]);
                    frameValuelArm = FastMath.interpolateLinear(perc,keyFramelArm[0],keyFramelArm[4]);
                    frameValuerArm = FastMath.interpolateLinear(perc,keyFramerArm[0],keyFramerArm[4]);
                    if(currentFrameTime > 0) {
                        inTransition = false;
                        currentAction = nextAction;
                        currentFrameTime = 0;
                        isAnimating = true;
                    }
                    break;
                case 2: //nextAction == Walk
                    //Transition to frame index 1
                    tweenLength = keyFrameTime[1] - keyFrameTime[0];
                    frameDiff = tweenLength - currentFrame;

                    // Calculate the interpolation value depending on frame time
                    perc = (float)(tweenLength - frameDiff) / tweenLength;
                    perc = perc + 1;
                    frameValuelLeg = FastMath.interpolateLinear(perc,keyFramelLeg[0],keyFramelLeg[1]);
                    frameValuerLeg = FastMath.interpolateLinear(perc,keyFramerLeg[0],keyFramerLeg[1]);
                    frameValuelArm = FastMath.interpolateLinear(perc,keyFramelArm[0],keyFramelArm[1]);
                    frameValuerArm = FastMath.interpolateLinear(perc,keyFramerArm[0],keyFramerArm[1]);
                    if(currentFrameTime > 0) {
                        inTransition = false;
                        currentAction = nextAction;
                        currentFrameTime = 0;
                        isAnimating = true;
                    }
                    break;
            }
            // For this prototype method, set the legs and arms at proper transforms
            actor.lLeg.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.rLeg.setLocalRotation(new Quaternion().fromAngles(frameValuerLeg * FastMath.DEG_TO_RAD,0,0));
            actor.lArm.setLocalRotation(new Quaternion().fromAngles(frameValuelArm * FastMath.DEG_TO_RAD,0,0));
            actor.rArm.setLocalRotation(new Quaternion().fromAngles(frameValuerArm * FastMath.DEG_TO_RAD,0,0));
            currentFrameTime = currentFrameTime + (tpf * 10);
        }
        
        // Set the current frame value for standing transformation
        protected void doStandAction(float tpf) {
            // For now, reset the transformations
            frameValueRoot = 0f;
            frameValueBody = 0f;
            frameValuelLeg = 0f;
            frameValuerLeg = 0f;
            frameValuelArm = 0f;
            frameValuerArm = 0f;
            frameValueHead = 0f;
            
            // For this prototype method, set the legs and arms at proper transforms
            actor.lLeg.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.rLeg.setLocalRotation(new Quaternion().fromAngles(frameValuerLeg * FastMath.DEG_TO_RAD,0,0));
            actor.lArm.setLocalRotation(new Quaternion().fromAngles(frameValuelArm * FastMath.DEG_TO_RAD,0,0));
            actor.rArm.setLocalRotation(new Quaternion().fromAngles(frameValuerArm * FastMath.DEG_TO_RAD,0,0));
            prevAction = 2;
            
            // This is a one shot action, set flag to false since there aren't any more to calculate
            isAnimating = false; 
            prevAction = 1;
        }
        
        // Calculate the current frame for walk action (keyFrame index 1-3)
        protected void doWalkAction(float tpf) {
            // Implementation for walk animation
            //Uses keyFrames 1-3
            if(currentFrame < keyFrameTime[2]) {
                //First tween
                tweenLength = keyFrameTime[2] - keyFrameTime[1];
                frameDiff = tweenLength - currentFrame;

                // Calculate the interpolation value depending on frame time
                perc = (float)(tweenLength - frameDiff) / tweenLength;

                // Calculate an interpolated frame (first tween)
                frameValuelLeg = FastMath.interpolateLinear(perc,keyFramelLeg[1],keyFramelLeg[2]);
                frameValuerLeg = FastMath.interpolateLinear(perc,keyFramerLeg[1],keyFramerLeg[2]);
                frameValuelArm = FastMath.interpolateLinear(perc,keyFramelArm[1],keyFramelArm[2]);
                frameValuerArm = FastMath.interpolateLinear(perc,keyFramerArm[1],keyFramerArm[2]);
            } else if(currentFrame < keyFrameTime[3]) {
                //Second tween
                float adjFrame = currentFrame - keyFrameTime[2];
                tweenLength = keyFrameTime[3] - keyFrameTime[2];
                frameDiff = tweenLength - adjFrame;

                // Calculate the interpolation value depending on frame time
                perc = (float)(tweenLength - frameDiff) / tweenLength;

                // Calculate an interpolated frame (second tween)
                frameValuelLeg = FastMath.interpolateLinear(perc, keyFramelLeg[2],keyFramelLeg[3]);
                frameValuerLeg = FastMath.interpolateLinear(perc, keyFramerLeg[2],keyFramerLeg[3]);
                frameValuelArm = FastMath.interpolateLinear(perc, keyFramelArm[2],keyFramelArm[3]);
                frameValuerArm = FastMath.interpolateLinear(perc, keyFramerArm[2],keyFramerArm[3]);
                tween = 2;
            } else if(currentFrame > keyFrameTime[3]) {
                //Loop to beginning and reset currentFrameTime
                currentFrame = currentFrame % (keyFrameTime[3] - keyFrameTime[1]);
                currentFrameTime = currentFrame * fps;

                //Now that the currentFrame has been adjusted to the next loop, do the first tween
                tweenLength = keyFrameTime[2] - keyFrameTime[1];
                frameDiff = tweenLength - currentFrame;
                perc = (float)(tweenLength - frameDiff) / tweenLength;

                // Calculate an interpolated frame (loop to first tween)
                frameValuelLeg = FastMath.interpolateLinear(perc,keyFramelLeg[1],keyFramelLeg[2]);
                frameValuerLeg = FastMath.interpolateLinear(perc,keyFramerLeg[1],keyFramerLeg[2]);
                frameValuelArm = FastMath.interpolateLinear(perc,keyFramelArm[1],keyFramelArm[2]);
                frameValuerArm = FastMath.interpolateLinear(perc,keyFramerArm[1],keyFramerArm[2]);
            } //End of walk animation
            
            // For this prototype method, set the legs and arms at proper transforms
            actor.lLeg.setLocalRotation(new Quaternion().fromAngles(frameValuelLeg * FastMath.DEG_TO_RAD,0,0));
            actor.rLeg.setLocalRotation(new Quaternion().fromAngles(frameValuerLeg * FastMath.DEG_TO_RAD,0,0));
            actor.lArm.setLocalRotation(new Quaternion().fromAngles(frameValuelArm * FastMath.DEG_TO_RAD,0,0));
            actor.rArm.setLocalRotation(new Quaternion().fromAngles(frameValuerArm * FastMath.DEG_TO_RAD,0,0));
            prevAction = 2;
        }
        
        public void update(float tpf) {
            // Update frame time before going to animations
            currentFrame = currentFrameTime / (1/fps);
            
            // When the animation engine receives a request to change actions, we 
            //might want to perform a transition into the new action
            // Check to see if we need to set up a transition (Intentional delay here)
            
            if(transition == true) {
                this.setTransition();
            }
            if(inTransition == true) {
                // Calculate tween frames for transition
                this.doTransition(tpf);
            }else if( (inTransition == false) && (isAnimating == true)) {
                // Perform an Action depending on the current action
                switch(currentAction) {
                    case 0:
                        //Do nothing
                        break;
                    case 1:
                        //Stand
                        this.doStandAction(tpf);
                        break;
                    case 2:
                        //Walk
                        // Calculate frame values for walk animation
                        this.doWalkAction(tpf); 
                        break;
                } 
                //Update the currentFrameTime here as long as isAnimating == true
                currentFrameTime = currentFrameTime + (tpf * 10);
            }
            
        } 
    }
