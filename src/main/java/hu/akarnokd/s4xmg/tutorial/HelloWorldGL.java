package hu.akarnokd.s4xmg.tutorial;

import org.joml.Matrix4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.*;

import java.awt.*;
import java.io.IOException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
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

    static PngTexture texture;

    static FontTexture font;

    static ShaderProgram fontProgram;

    static TextMesh textMesh;

    static float textAngle = 0f;

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

        float[] positions = new float[] {
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        texture = new PngTexture("data/images/planets/vegetables_tile_64.png");

        mesh = new Mesh(positions, textCoords, indices, texture);

        float aspectRatio = width / (float)height;

        program.createUniform("projectionMatrix");
        program.createUniform("worldMatrix");
        program.createUniform("texture_sampler");

        transformation = new ViewTransformation();

        items = new ArrayList<>();

        items.add(new ViewItem(mesh));

        // --------------------------------------------------------------------------

        fontProgram = new ShaderProgram();
        try {
            fontProgram.createVertexShader(new String(Files.readAllBytes(Paths.get("data/shaders/text_vertex.vs"))));
            fontProgram.createFragmentShader(new String(Files.readAllBytes(Paths.get("data/shaders/text_fragment.fs"))));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        fontProgram.link();

        fontProgram.createUniform("projModelMatrix");
        fontProgram.createUniform("texture_sampler");
        fontProgram.createUniform("color");

        font = FontTexture.createAscii(new Font(Font.MONOSPACED, Font.PLAIN, 20));

        textMesh = new TextMesh("Hello World!", font);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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

            glEnable(GL_DEPTH_TEST);

            program.bind();

            Matrix4f projectionMatrix = transformation.getProjectionMatrix(fov, width, height, zNear, zFar);
            program.setUniform("projectionMatrix", projectionMatrix);
            program.setUniform("texture_sampler", 0);

            for (ViewItem item : items) {

                item.rotation().y += 0.5;
                item.position().z = -1;
                item.scale(0.5f);

                Matrix4f worldMatrix = transformation.getWorldMatrix(item.position(), item.scale, item.rotation);
                program.setUniform("worldMatrix", worldMatrix);

                item.mesh().drawTriangles();
            }

            program.unbind();

            // print text -----------------------------------------------------------------------

            glDisable(GL_DEPTH_TEST);

            fontProgram.bind();
            fontProgram.setUniform("texture_sampler", 0);
            fontProgram.setUniform("color", 1f, 0f, 0f, 1f);

            float textPosX = (width - textMesh.width()) / 2;
            float textPosY = (height - textMesh.height()) / 2;
            Matrix4f ortho = transformation.getOrthoProjectionMatrix(0, width, height, 0);
            Matrix4f projModelMatrix = transformation.getOrthoProjectModelMatrix(textPosX, textPosY, 1, textAngle, ortho);
            fontProgram.setUniform("projModelMatrix", projModelMatrix);

            textMesh.draw();

            fontProgram.unbind();

            textAngle += 1;

            // custom end

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    static void destroy() {

        program.cleanup();

        mesh.cleanup();

        texture.cleanup();

        // cleanup the window manager

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
