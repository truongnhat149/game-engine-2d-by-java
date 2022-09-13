package jade;

import jdk.nashorn.internal.runtime.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import util.Time;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private int width, height;
    private long glfwWindow;
    private static Window window = null;
    private String title;

    public float r,g,b,a;

    private boolean fadeToBlack = false;

    public static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                 currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false: "Unknown scene '"+ newScene +"'";
                break;
        }
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.version() + "!");

        init();

        loop();

        // Free the window callbacks and destroy the window

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
//
//        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public  void init() {
        // Setup and error callback. The default implementation
        // will print the error message in System.err

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW function will not work before

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }


        // Configure GLFW

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);


        // Create the window

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);


        if (glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(glfwWindow);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public  void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(glfwWindow) ) {
            // pool event;

            glfwPollEvents();

            // Set the clear color
            glClearColor(r,g,b,a);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            if (fadeToBlack) {
//                r = Math.max(r - 0.01f, 0);
//                g = Math.max(g - 0.01f, 0);
//                b = Math.max(b - 0.01f, 0);
//            }
//
//            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
//                fadeToBlack = true;
//            }


            if (dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;

        }



    }
}
