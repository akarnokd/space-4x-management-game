package hu.akarnokd.s4xmg.tutorial;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class ShapeMesh {

    final int vaoId;

    final int vboId;

    final int idxId;

    final int verticesCount;

    public ShapeMesh(float[] vertices, int[] indices) {
        FloatBuffer verticeBuffer = memAllocFloat(vertices.length);
        verticeBuffer.put(vertices).flip();

        IntBuffer indexBuffer = memAllocInt(indices.length);
        indexBuffer.put(indices).flip();

        verticesCount = indices.length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticeBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        idxId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        memFree(verticeBuffer);
        memFree(indexBuffer);
    }

    public void draw() {
        draw(GL_TRIANGLES);
    }

    public void drawLines() {
        draw(GL_LINES);
    }

    public void drawLineLoop() {
        draw(GL_LINE_LOOP);
    }

    public void drawPoints() {
        draw(GL_POINTS);
    }

    void draw(int mode) {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        glDrawElements(mode, verticesCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        glDeleteBuffers(idxId);


        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public static ShapeMesh rectangle(int x, int y, int width, int height) {
        return new ShapeMesh(new float[] {
                x, y, 0,
                x, y + height - 1, 0,
                x + width - 1, y, 0,
                x + width - 1, y + height - 1, 0
        }, new int[] {
                0, 1, 2, 2, 1, 3
        });
    }

    public static ShapeMesh rectangleLines(int x, int y, int width, int height) {
        return new ShapeMesh(new float[] {
                x, y, 0,
                x, y + height - 1, 0,
                x + width - 1, y, 0,
                x + width - 1, y + height - 1, 0
        }, new int[] {
                0, 1, 1, 3, 3, 2, 2, 0
        });
    }

    public static ShapeMesh rectangleLineLoop(int x, int y, int width, int height) {
        return new ShapeMesh(new float[] {
                x, y, 0,
                x, y + height - 1, 0,
                x + width - 1, y, 0,
                x + width - 1, y + height - 1, 0
        }, new int[] {
                0, 1, 3, 2
        });
    }
}
