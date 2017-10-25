package hu.akarnokd.s4xmg.tutorial;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TextMesh {

    final int vaoId;

    final int vboId;

    final int idxId;

    final int texId;

    final int verticesCount;

    final FontTexture texture;

    final int width;

    final int height;

    public TextMesh(String text, FontTexture texture) {

        this.texture = texture;

        FloatBuffer verticesBuffer = memAllocFloat(4 * 3 * text.length());

        FloatBuffer textureBuffer = memAllocFloat(2 * 4 * text.length());

        IntBuffer indexBuffer = memAllocInt(6 * text.length());

        verticesCount = 6 * text.length();

        int x = 0;
        float totalWidth = texture.width();
        int h = texture.height();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            FontTexture.Glyph gl = texture.glyph(c);
            int w;
            if (gl == null) {
                gl = texture.glyph('?');
            }
            int tx = gl.x;
            w = gl.width;

            verticesBuffer
                    .put(x).put(0f).put(0f)
                    .put(x).put(h).put(0f)
                    .put(x + w).put(0f).put(0f)
                    .put(x + w).put(h).put(0f);

            textureBuffer
                    .put(tx / totalWidth).put(0)
                    .put(tx / totalWidth).put(1)
                    .put((tx + w) / totalWidth).put(0)
                    .put((tx + w) / totalWidth).put(1);

            indexBuffer
                    .put(4 * i).put(4 * i + 1).put(4 * i + 2)
                    .put(4 * i + 2).put(4 * i + 1).put(4 * i + 3);

            x += w;
        }

        width = x;
        height = h;

        verticesBuffer.flip();
        textureBuffer.flip();
        indexBuffer.flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        texId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, texId);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        idxId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        memFree(verticesBuffer);
        memFree(textureBuffer);
        memFree(indexBuffer);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteBuffers(vboId);
        glDeleteBuffers(texId);
        glDeleteBuffers(idxId);

        glBindVertexArray(0);

        glDeleteVertexArrays(vaoId);
    }

    public void draw() {
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
