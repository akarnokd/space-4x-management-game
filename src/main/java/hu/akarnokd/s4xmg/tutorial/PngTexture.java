package hu.akarnokd.s4xmg.tutorial;

import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.system.MemoryUtil.*;

public class PngTexture {

    int texId;

    public PngTexture(String filePath) {
        ByteBuffer buf = null;
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(filePath));
            buf = memAlloc(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
            buf.flip();

            texId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texId);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(),
                    0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            //glGenerateMipmap(GL_TEXTURE_2D);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (buf != null) {
                memFree(buf);
            }
        }
    }

    public int texId() {
        return texId;
    }

    public void cleanup() {
        glDeleteTextures(texId);
    }
}
