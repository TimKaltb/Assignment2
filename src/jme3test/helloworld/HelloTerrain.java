package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.HillHeightMap; // for exercise 2
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/** Sample 10 - How to create fast-rendering terrains from heightmaps,
and how to use texture splatting to make the terrain look good.  */
public class HelloTerrain extends SimpleApplication
        implements ActionListener {
  
  private BulletAppState bulletAppState;
  private CharacterControl player;
  private RigidBodyControl landscape;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false;
  
  public static void main(String[] args) {
    HelloTerrain app = new HelloTerrain();
    app.start();
  }

  @Override
  public void simpleInitApp() {
    
    // setting up the physics behind it
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);  
      
    // setting the flycam speed to 100
    flyCam.setMoveSpeed(100);
    // calling the setUoKeys() method
    setUpKeys();

    /** 1. Create terrain material and load four textures into it. */
    Material mat_terrain = new Material(assetManager,
            "Common/MatDefs/Terrain/Terrain.j3md");

    /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
            "Textures/Terrain/splat/alphamap.png"));

    /** 1.2) Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
            "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);

    /** 1.3) Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
            "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);

    /** 1.4) Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
            "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);

    /* 2.a Create a custom height map from an image */
    //AbstractHeightMap heightmap = null;
    //Texture heightMapImage = assetManager.loadTexture(
    //        "Textures/Terrain/splat/mountains512.png");
    //heightmap = new ImageBasedHeightMap(heightMapImage.getImage());

    /*2.b Create a random height map */
     HillHeightMap heightmap = null;
      HillHeightMap.NORMALIZE_RANGE = 100;
      try {
          heightmap = new HillHeightMap(513, 1000, 50, 100, (byte) 3);
      } catch (Exception ex) {
          ex.printStackTrace();
      }

    heightmap.load();

    /** 3. We have prepared material and heightmap.
     * Now we create the actual terrain:
     * 3.1) Create a TerrainQuad and name it "my terrain".
     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
     * 3.5) We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    TerrainQuad terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);
    

    /** 5. The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    control.setLodCalculator( new DistanceLodCalculator(patchSize, 2.7f) ); // patch size, and a multiplier
    terrain.addControl(control);
    
    /** 6. Add physics: */
    // We set up collision detection for the scene by creating a static RigidBodyControl with mass zero.
    terrain.addControl(new RigidBodyControl(0));
    
    // creating the player, that is not able to move through the landscape
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setJumpSpeed(20);
    player.setFallSpeed(30);
    player.setGravity(30);
    player.setPhysicsLocation(new Vector3f(0, 10, 0));
    
    // adding the scene and player to the rootnode
    bulletAppState.getPhysicsSpace().add(terrain);
    bulletAppState.getPhysicsSpace().add(player);
   }
  
  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
  }
  
  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
   @Override
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
  }
  
  @Override
  public void simpleUpdate(float tpf) {
    Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
    Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
    walkDirection.set(0, 0, 0);
    if (left)  { walkDirection.addLocal(camLeft); }
    if (right) { walkDirection.addLocal(camLeft.negate()); }
    if (up)    { walkDirection.addLocal(camDir); }
    if (down)  { walkDirection.addLocal(camDir.negate()); }
    player.setWalkDirection(walkDirection);
    cam.setLocation(player.getPhysicsLocation());
  }
  }

// Exercises
    
    
    // Exercise1:
    // What happens when you swap two layers, for example Tex1 and Tex2?
    
    // A: When you swap texture 1 and texture 2 the two terrain swap with each other.
    // In the case of Tex1 and Tex2 you will have grass instead of dirt, and dirt instead of grass.
    
    
    // Exercise2:
    // a.)
    // What result do you get when you replace the above three heightmap
    // lines by the following lines and run the sample?
    
    // A: The map seems a lot smoother and the mountains are scaled down.
    
    
    // b.)
    // Change one parameter at a time, and the run the sample again. Note the differences. 
    // Can you find out which of the values has which effect on the generated terrain (look at the javadoc also)?
    // Which value controls the size?
    // A: The first parameter controls the size of the terrain.
    
    // What happens if the size is not a square number +1 ?
    // A: I'm not sure how to interpret this question exactly.
    
    // Which value controls the number of hills generated?
    // A: The second parameter controls the iterations, the number of hills to grow or generated.
    
    // Which values control the size and steepness of the hills?
    // A: The third and fourth parameters control the minimum radius of a hill and the maximum radius of a hill.
    // Therefore, parameters 3 and 4 control the size and steepness of the hills.
    
    // What happens if the min is bigger than or equal to max?
    // A: You get a NullPointerException.
    
    // What happens if both min and max are small values (e.g. 10/20)?
    // A: The mountains are a lot steeper and sharper.
    
    // What happens if both min and max are large values (e.g. 1000/1500)?
    // A: The mountains are not steep and sharp at all, and it loops like the map is shifted to the side,
    // as the radius of the mountains are interfering with each other because they are so big.
    
    // What happens if min and max are very close(e.g. 1000/1001, 20/21)? Very far apart (e.g. 10/1000)?
    // A: When the min and max are very close, the mountains are all equally steep and sharp.