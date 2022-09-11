package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    }

    /* Use the main event loop to trigger repeating actions. */
    @Override
    public void simpleUpdate(float tpf) {
        // make the blue cube rotate:
        bluecube.rotate(0, 2*tpf, 0);
        // make the red cube rotate twice as fast as the blue cube
        redcube.rotate(0, 4*tpf, 0);
    }
    
    // 1.) What happens if you give the rotate() method negative numbers?
    
    // A: The cube will rotate in the opposite direction. It's rotating
    // to the left now, instead of to the right.
    
    
    // 2.) Can you create two Geometries next to each other,
    // and make one rotate twice as fast as the other?
    
    // The answer to this is documentated in the code above.
}