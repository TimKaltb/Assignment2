package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/** Sample 4 - how to trigger repeating actions from the main event loop.
 * In this example, you use the loop to make the player character
 * rotate continuously. */
public class HelloUpdateLoop extends SimpleApplication {

    public static void main(String[] args){
        HelloUpdateLoop app = new HelloUpdateLoop();
        app.start();
    }

    protected Geometry bluecube;
    protected Geometry redcube;
    protected Geometry whitecube;
    protected Geometry randomcube;
    protected Material mat4;
    // creating a boolean that indicates whether the cube is growing or not
    private boolean isGrowing = true;
    // creating a float that is the time
    private float time = 0;

    @Override
    public void simpleInitApp() {
        /** this blue box is our player character */
        Box b1 = new Box(1, 1, 1);
        bluecube = new Geometry("blue cube", b1);
        Material mat1 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        bluecube.setMaterial(mat1);
        rootNode.attachChild(bluecube);
        
        // create another box called b2
        Box b2 = new Box(1, 1, 1);
        redcube = new Geometry("red cube", b2);
        // give the new box a colour
        Material mat2 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        redcube.setMaterial(mat2);
        // move the new cube next to the first one
        redcube.move(3, 0, 0);
        // lets the new cube appear on the screen
        rootNode.attachChild(redcube);
        
        // create another box called b3
        Box b3 = new Box(1, 1, 1);
        whitecube = new Geometry("white cube", b3);
        // give the new box a colour
        Material mat3 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.White);
        whitecube.setMaterial(mat3);
        // move the new cube next to the first one
        whitecube.move(-3, 0, 0);
        // lets the new cube appear on the screen
        rootNode.attachChild(whitecube);
        
        // create another box called b4
        Box b4 = new Box(1, 1, 1);
        randomcube = new Geometry("random cube", b4);
        // give the new box a colour
        mat4 = new Material(assetManager,
          "Common/MatDefs/Misc/Unshaded.j3md");
        mat4.setColor("Color", ColorRGBA.randomColor());
        randomcube.setMaterial(mat4);
        // move the new cube next to the first one
        randomcube.move(6, 0, 0);
        // lets the new cube appear on the screen
        rootNode.attachChild(randomcube);
    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the blue cube rotate:
        bluecube.rotate(0, 2*tpf, 0);
        // make the red cube rotate twice as fast as the blue cube
        redcube.rotate(0, 4*tpf, 0);
        // make the white cube pulsate
        // if the boolean isGrowing is true, the white cube will grow
        // if the boolean isGrowing is false, the white cube will shrink
        if (isGrowing) {
            whitecube.scale(1 + (1.5f * tpf));
        } else {
            whitecube.scale(1 - (1.5f * tpf));
        }
        
        // import Vector3f and create a variable called size that defines the size of the cube
        Vector3f size = whitecube.getLocalScale();
        
        // if the size of the cube is bigger than 1.3 of the original size, the boolean
        // isGrowing becomes false, which will lead to the cube shrinking
        // if the size of the cube is smaller than 0.7 of the original size, the boolean
        // isGrowing becomes true, which will lead to the cube growing in size
        if (size.getX() > 1.3f) {
                isGrowing = false;
        } else if (size.getX() < 0.7f) {
                isGrowing = true;
        }
        
        // the time variable is increasing with the time per frame variable
        time += tpf;
        // if statement that changes the color to a random color everytime
        // the time variable gets bigger than 1, and then initializes it to 0 again
        if (time > 1){
            mat4.setColor("Color", ColorRGBA.randomColor());
            time = 0;
        } 
    }
    
    // 1.) What happens if you give the rotate() method negative numbers?
    
    // A: The cube will rotate in the opposite direction. It's rotating
    // to the left now, instead of to the right.
    
    
    // 2.) Can you create two Geometries next to each other,
    // and make one rotate twice as fast as the other?
    
    // A: The answer to this is documentated in the code above.
    
    
    // 3.) Can you make a cube that pulsates?
    
    // A: The answer to this is documentated in the code above. 
    
    
    // 4.) Can you make a cube that changes color?
    
    // A: The answer to this is documentated in the code above.
}