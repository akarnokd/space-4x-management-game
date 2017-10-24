package hu.akarnokd.s4xmg;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {

    final int vaoId;

    final int vboId;

    final int idxVboId;

    final int colorVboId;

    final int verticesCount;

    public Mesh(float[] positions, float[] colors, int[] indices) {
        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer colorBuffer = null;
        try {
            verticesCount = indices.length;

            indicesBuffer = memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            verticesBuffer = memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();

            colorBuffer = memAllocFloat(colors.length);
            colorBuffer.put(colors).flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);


            colorVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
            glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null) {
                memFree(verticesBuffer);
            }
            if (indicesBuffer != null) {
                memFree(indicesBuffer);
            }
            if (colorBuffer != null) {
                memFree(colorBuffer);
            }
        }
    }

    public int vaoId() {
        return vaoId;
    }

    public int vertexCount() {
        return verticesCount;
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteBuffers(vboId);
        glDeleteBuffers(idxVboId);
        glDeleteBuffers(colorVboId);

        glBindVertexArray(0);

        glDeleteVertexArrays(vaoId);
    }

    public void drawTriangles() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, verticesCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
