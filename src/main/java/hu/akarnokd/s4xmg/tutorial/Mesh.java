package hu.akarnokd.s4xmg.tutorial;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {

    final int vaoId;

    final int vboId;

    final int idxVboId;

    final int textureVboId;

    final int verticesCount;

    final PngTexture texture;

    public Mesh(float[] positions, float[] textCoords, int[] indices, PngTexture texture) {
        this.texture = texture;
        FloatBuffer verticesBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer textureBuffer = null;
        try {
            verticesCount = indices.length;

            indicesBuffer = memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();

            verticesBuffer = memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();

            textureBuffer = memAllocFloat(textCoords.length);
            textureBuffer.put(textCoords).flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            textureVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (verticesBuffer != null) {
                memFree(verticesBuffer);
            }
            if (indicesBuffer != null) {
                memFree(indicesBuffer);
            }
            if (textureBuffer != null) {
                memFree(textureBuffer);
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
        glDeleteBuffers(textureVboId);

        glBindVertexArray(0);

        glDeleteVertexArrays(vaoId);
    }

    public void drawTriangles() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture.texId());

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);


        glDrawElements(GL_TRIANGLES, verticesCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }
}
