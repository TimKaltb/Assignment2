package jme3test.helloworld;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 * Sample 5 - how to map keys and mousebuttons to actions
 */
public class HelloInput extends SimpleApplication {

    public static void main(String[] args) {
        HelloInput app = new HelloInput();
        app.start();
    }

    protected Geometry player;
    private boolean isRunning = true;

    @Override
    public void simpleInitApp() {
        System.out.println(speed);
        
        
        Box b = new Box(1, 1, 1);               //creating a box
        player = new Geometry("Player", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        player.setMaterial(mat);
        rootNode.attachChild(player);
        initKeys(); // load my custom keybinding
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void initKeys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Pause",  new KeyTrigger(KeyInput.KEY_P));  // button p will pause the cube
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_J));  // button j will move the cube to the left  
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_K));  // button k will move the cube to the right
        inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE), // button space and clicking the left button on the mouse 
                                                                              //will rotate the cube with 
                                          new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Pause");
        inputManager.addListener(analogListener, "Left", "Right", "Rotate");
        
        // Exercises:
        // 1.)
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_H)); // with the button h, the cube will move up
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_L)); // with the button h, the cube will move up
        inputManager.addListener(analogListener, "Up", "Down");
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Pause") && !keyPressed) {
                isRunning = !isRunning; 
            }
        }
    };

    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (isRunning) {
                if (name.equals("Rotate")) {
                    player.rotate(0, value * speed, 0);
                }
                if (name.equals("Right")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x + value * speed, v.y, v.z);
                }
                if (name.equals("Left")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x - value * speed, v.y, v.z);
                }
                // Analoglisteners for moving the cube up and down.
                if (name.equals("Up")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y + value * speed, v.z);
                }
                if (name.equals("Down")) {
                    Vector3f v = player.getLocalTranslation();
                    player.setLocalTranslation(v.x, v.y - value * speed, v.z);
                }
            } else {
                System.out.println("Press P to unpause.");
            }
        }
    };
}