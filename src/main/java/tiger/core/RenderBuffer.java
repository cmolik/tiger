/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.nio.IntBuffer;
import com.jogamp.opengl.GL;

/**
 *
 * @author cmolikl
 */
public class RenderBuffer {
    
    private IntBuffer glNumber = null;
    
    private RenderBuffer(GL gl, int format, int width, int height) {
        glNumber = IntBuffer.allocate(1);
        gl.glGenRenderbuffers(1, glNumber);
    }
    
    public int getGlNumber() {
        return glNumber.get(0);
    }
    
    public static RenderBuffer init(GL gl, int format, int width, int height) {
        return new RenderBuffer(gl, format, width, height);
    }
    
    public void bind(GL gl) {
        gl.glBindRenderbuffer(GL.GL_RENDERBUFFER, glNumber.get(0));
    }
    
    public void attachStorage(GL gl, int format, int width, int height) {
        bind(gl);
        gl.glRenderbufferStorage(GL.GL_RENDERBUFFER, format, width, height);
    }
    
    public void delete(GL gl) {
        gl.glDeleteRenderbuffers(1, glNumber);
    }
}
