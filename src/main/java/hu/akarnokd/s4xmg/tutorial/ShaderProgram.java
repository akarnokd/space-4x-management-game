package hu.akarnokd.s4xmg.tutorial;

import org.joml.Matrix4f;
import org.lwjgl.system.*;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL20.*;

/**
 * Represents a shader program and tools to compile, validate, use, stop using and cleaning up.
 * <p>Usage:
 * <ol>
 *     <li>createVertexShader("program source")</li>
 *     <li>createFragmentShader("program source")</li>
 *     <li>{@link #link()}</li>
 *     <li>{@link #bind()} then {@link #unbind()} as many times as needed</li>
 *     <li>{@link #cleanup()}</li>
 * </ol>
 * </p>
 */
public class ShaderProgram {
    final int programId;

    int vertexShaderId;

    int fragmentShaderId;

    final Map<String, Integer> uniforms = new HashMap<>();

    public ShaderProgram() {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new RuntimeException("Unable to create OpenGL program");
        }
    }

    public void createVertexShader(String shaderCode) {
       vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    int createShader(String shaderCode, int type) {
        int id = glCreateShader(type);
        if (id == 0) {
            throw new RuntimeException("Unable to create shader " + type);
        }

        glShaderSource(id, shaderCode);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(glGetShaderInfoLog(id, 1024));
        }

        glAttachShader(programId, id);
        return id;
    }

    public void link() {
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }

        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        // validates the program against current context, may fail, remove in release versions
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(programId, 1024));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public void createUniform(String name) {
        int uid = glGetUniformLocation(programId, name);
        if (uid < 0) {
            throw new RuntimeException("Uniform " + name + " not found");
        }
        uniforms.put(name, uid);
    }

    public void setUniform(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            matrix.get(buf);

            glUniformMatrix4fv(uniforms.get(name), false, buf);
        }
    }

    public void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }
}
