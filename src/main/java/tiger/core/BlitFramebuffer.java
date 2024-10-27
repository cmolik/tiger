/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class BlitFramebuffer extends GLEventAdapter {

    private Link<FrameBuffer> source;
    private Link<FrameBuffer> destination;
    private int srcX0, srcY0, srcX1, srcY1;
    private int dstX0, dstY0, dstX1, dstY1;
    private int mask, filter;

    public BlitFramebuffer(Link<FrameBuffer> source, Link<FrameBuffer> destination, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        this.source = source;
        this.destination = destination;
        this.srcX0 = srcX0;
        this.srcY0 = srcY0;
        this.srcX1 = srcX1;
        this.srcY1 = srcY1;
        this.dstX0 = dstX0;
        this.dstY0 = dstY0;
        this.dstX1 = dstX1;
        this.dstY1 = dstY1;
        this.mask = mask;
        this.filter = filter;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0 );
        gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, source.get().getGlNumber());
        gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, destination.get().getGlNumber());
        gl.glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
    }

}
