# jMonkeyDev

As time moves forward, I'm going to keep a few note-worthy proof of concept demos and test releases of my java code for jMonkeyEngine. At this time, I'm officially working on a first-person dungeon/zombie style shooter. I also have plans for a simple dungeon crawler and rudimentary racing game as well. Most of the code at home is dirty and not commented well, so as I clean them up, I will post them here.

There are at least four note-worthy java classes I have completed thus far (out of many) that are the poster children to my endeavors at java programming.

#ActorClassDev.java
This class serves as a potential work-around for a stressful issue I was having with importing animations from blender into jMonkeyEngine. In the spirit of R&D, instead of allowing that temporary setback issue to turn me away from jMonkey, I decided to take matters into my hands as far as developing a 'rag doll' that I could animate from code (simple animations). This file will serve as a foundation to a potential animation editor/engine that will be set apart from jMonkeys existing animation code.

#Tank.java
Proof of concept of using scenegraph Nodes as moveable joints to child spatials(invisible nodes animating visible scene elements). Precursor to ActorClassDev.java.

#VoxelShooterTest_2.java
This is the result after studying jMonkeys tutorials on first-person camera movement with bullet physics involved. A scene created with cubes engine was used instead of loading an external model.

#mapDevToolTest.java
It gets tiring trying to design a voxel map with code only. As this endeavor expands, a graphical map editor will be much needed. Lucky for me, the open-sourced cubes engine provides an example of the basics involved with a voxel map editor. That example will be refactored into a custom scene editor.

#fileManagementDev.java
Planning to refactor example code from jMonkey into a custom save/load tool for maps created with mapDevTool. At the moment, this is just a class file to encapsulate the required methods and functionality.
