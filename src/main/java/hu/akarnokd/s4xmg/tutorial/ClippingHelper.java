package hu.akarnokd.s4xmg.tutorial;

import static org.lwjgl.opengl.GL11.*;

/**
 * Helper class to help with render clipping by switching between three modes:
 * capturing the clipping shape, switching to render+clip mode and exiting clipping.
 * <p>
 *     Usage:
 *     <ol>
 *         <li>Call {@link #beginClipping()} to setup the rendering to target the stencil buffer;
 *         pixels rendered will set the appropriate buffer entry to 1 but not affect the color/depth buffer.</li>
 *         <li>Call {@link #objectDraw()} to render the objects you want, the stencil buffer will
 *         filter out pixels that are not 1 in the stencil buffer, clipping the output</li>
 *         <li>Call {@link #endClipping()} to return to a normal non-clipped mode.</li>
 *     </ol>
 * </p>
 */
public final class ClippingHelper {

    private ClippingHelper() {
        throw new IllegalStateException("No instances!");
    }

    public static void beginClipping() {
        glEnable(GL_STENCIL_TEST);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glClear(GL_STENCIL_BUFFER_BIT);

        glStencilFunc(GL_ALWAYS, 1, 0xFF);
        glStencilMask(0xFF);

        glColorMask(false, false, false, false);
        glDepthMask(false);
    }

    public static void objectDraw() {
        glColorMask(true, true, true, true);
        glDepthMask(true);

        glStencilFunc(GL_EQUAL, 1, 0xFF);
        glStencilMask(0x00);
    }

    public static void endClipping() {
        glStencilMask(0xFF);
        glDisable(GL_STENCIL_TEST);
    }
}
