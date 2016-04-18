/*
 * Early use-case test of usage of jMonkeyBrains steering behaviors
 */
package mygame;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.*;
import com.jme3.ai.agents.behaviors.npc.steering.*;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;

/**
 *
 * @author Maestro
 */
public class Example extends SimpleApplication {

    //defining game
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance(); 
    private Spatial steveMob;
    public static void main(String[] args) {
        Example app = new Example();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining app
        brainsAppState.setApp(this);
        
        steveMob = assetManager.loadModel("Models/MinecraftSteve/MinecraftSteve.j3o");
        rootNode.attachChild(steveMob);
        //initialization of Agents with their names and spatials
        Agent agent = new Agent("First agent", steveMob); 
        //there isn't any method in framework like createAgentSpatial()
        //user is supposed to build his own spatials for game

        //adding agent to MonkeyBrainsAppState
        brainsAppState.addAgent(agent);
        
        SimpleMainBehavior mainBehavior = new SimpleMainBehavior(agent);
        mainBehavior.addBehavior(new SeekBehavior(agent,new Vector3f(20f,0f,12f)));

        //setting moveSpeed, rotationSpeed, mass..
        agent.setMoveSpeed(20); 
        agent.setRotationSpeed(30);
        //used for steering behaviors in com.jme3.ai.agents.behaviors.npc.steering
        agent.setMass(40);
        agent.setMaxForce(3);

        //creating main behavior
        //agent can have only one behavior but that behavior can contain other behaviors
        agent.setMainBehavior(mainBehavior);

        //starting agents
        brainsAppState.start();

    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}
