package hu.akarnokd.s4xmg.tutorial;

import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.*;

public class FontTexture {

    public static class Glyph {
        final int x;
        final int width;

        public Glyph(int x, int width) {
            this.x = x;
            this.width = width;
        }
    }

    final Map<Character, Glyph> charMap = new HashMap<>();

    final int texId;

    final int width;

    final int height;

    final int maxCharWidth;

    public FontTexture(Font font, char[] characters) {

        int maxHeight;
        int totalWidth = 0;

        BufferedImage bimg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        bimg.setAccelerationPriority(0);
        Graphics2D g2 = bimg.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            FontMetrics fm = g2.getFontMetrics(font);
            maxHeight = fm.getHeight();
            maxCharWidth = fm.getMaxAdvance();

            for (char c : characters) {
                totalWidth += fm.charWidth(c);
            }
        } finally {
            g2.dispose();
        }

        bimg = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        bimg.setAccelerationPriority(0);
        g2 = bimg.createGraphics();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(font);
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int ascent = fm.getAscent();
            totalWidth = 0;
            char[] ca = { ' ' };

            for (char c : characters) {
                int w = fm.charWidth(c);
                Glyph gl = new Glyph(totalWidth, w);
                charMap.put(c, gl);
                ca[0] = c;

                g2.drawChars(ca, 0, 1, totalWidth, ascent);

                totalWidth += w;
            }
        } finally {
            g2.dispose();
        }
        int[] pixels = new int[bimg.getWidth() * bimg.getHeight()];
        bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), pixels, 0, bimg.getWidth());

        ByteBuffer buf = MemoryUtil.memAlloc(4 * pixels.length);
        try {
            for (int rgba : pixels) {
                buf.put((byte) ((rgba >> 16) & 0xFF));
                buf.put((byte) ((rgba >> 8) & 0xFF));
                buf.put((byte) ((rgba) & 0xFF));
                buf.put((byte) ((rgba >> 24) & 0xFF));
            }
            buf.flip();

            texId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texId);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bimg.getWidth(), bimg.getHeight(),
                    0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        } finally {
            MemoryUtil.memFree(buf);
        }
        height = bimg.getHeight();
        width = bimg.getWidth();
    }

    public int texId() {
        return texId;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public static FontTexture createAscii(Font font) {
        char[] chars = new char[224];
        for (char c = 32; c < 256; c++) {
            chars[c - 32] = c;
        }
        return new FontTexture(font, chars);
    }

    public void cleanup() {
        glDeleteTextures(texId);
    }

    public Glyph glyph(char c) {
        return charMap.get(c);
    }

    public int maxCharWidth() {
        return maxCharWidth;
    }
}
