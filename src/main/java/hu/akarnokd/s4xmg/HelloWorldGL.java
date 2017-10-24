package hu.akarnokd.s4xmg;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.*;

import java.io.IOException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorldGL {
    static long window;

    static ShaderProgram program;

    static Mesh mesh;

    static boolean resized;

    static int width;

    static int height;

    static float fov = (float)Math.toRadians(60);

    static float zNear = 0.01f;

    static float zFar = 1000f;

    static ViewTransformation transformation;

    static List<ViewItem> items;

    public static void main(String[] args) {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        loop();

        destroy();
    }

    static void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(300, 300, "Hello World", NULL, NULL);

        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
           if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
               glfwSetWindowShouldClose(window, true);
           }
        });

        glfwSetWindowSizeCallback(window, (wnd, w, h) -> {
            resized = true;
            width = w;
            height = h;
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            resized = true;
            width = pWidth.get(0);
            height = pHeight.get(0);

            glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);

        // shader example initialization

        GL.createCapabilities();

        program = new ShaderProgram();
        try {
            program.createVertexShader(new String(Files.readAllBytes(Paths.get("data/shaders/vertex.vs"))));
            program.createFragmentShader(new String(Files.readAllBytes(Paths.get("data/shaders/fragment.fs"))));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        program.link();

        float[] vertices = {
                /* triangle
                0f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f
                */
                /* square from 2 triangles
                -0.5f,  0.5f, 0f,
                -0.5f, -0.5f, 0f,
                 0.5f,  0.5f, 0f,
                 0.5f,  0.5f, 0f,
                -0.5f, -0.5f, 0f,
                 0.5f, -0.5f, 0f
                 */
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] colors = {
                0.5f, 0f, 0f,
                0f, 0.5f, 0f,
                0f, 0f, 0.5f,
                0f, 0.5f, 0.5f
        };

        mesh = new Mesh(vertices, colors, indices);

        float aspectRatio = width / (float)height;

        program.createUniform("projectionMatrix");
        program.createUniform("worldMatrix");

        transformation = new ViewTransformation();

        items = new ArrayList<>();

        items.add(new ViewItem(mesh));
    }

    static void loop() {
        glClearColor(1f, 0.9f, 0.9f, 0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (resized) {
                glViewport(0, 0, width, height);
                resized = false;
            }

            // custom rendering part

            program.bind();

            Matrix4f projectionMatrix = transformation.getProjectionMatrix(fov, width, height, zNear, zFar);
            program.setUniform("projectionMatrix", projectionMatrix);

            for (ViewItem item : items) {

                item.rotation().z += 0.5;
                item.position().z = -1;

                Matrix4f worldMatrix = transformation.getWorldMatrix(item.position(), item.scale, item.rotation);
                program.setUniform("worldMatrix", worldMatrix);

                item.mesh().drawTriangles();
            }



            mesh.drawTriangles();

            program.unbind();

            // custom end

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    static void destroy() {

        program.cleanup();

        mesh.cleanup();

        // cleanup the window manager

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
