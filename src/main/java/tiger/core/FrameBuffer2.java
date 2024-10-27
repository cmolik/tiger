/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Set;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class FrameBuffer2 extends FrameBuffer {

    public HashMap<Integer, Texture> targets = new HashMap<>();
    
    public FrameBuffer2() {}
    
    public void addTarget(Texture target, int attachmentPoint) {
        targets.put(attachmentPoint, target);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        if(!initialized) {
            initialized = true;
            GL3 gl = drawable.getGL().getGL3();
            glNumber = IntBuffer.allocate(1);
            gl.glGenFramebuffers(1, glNumber);

            if (!targets.keySet().isEmpty()) {
                bind(gl);
                for (int attachmentPoint : targets.keySet()) {
                    Texture target = targets.get(attachmentPoint);
                    attachImage(gl, target, attachmentPoint, 0);
                }
            }
        }
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    
    @Override
    public void attachImage(GL3 gl, Texture texture, int attachmentPoint, int mipLevel) {
        if(texture instanceof Texture2D) {
            gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, attachmentPoint, GL.GL_TEXTURE_2D, texture.getGlNumber(), mipLevel);
        }
        else if(texture instanceof Texture2DMultisample) {
            gl.glFramebufferTexture2D(GL.GL_FRAMEBUFFER, attachmentPoint, GL3.GL_TEXTURE_2D_MULTISAMPLE, texture.getGlNumber(), 0);
        }
        else {
            gl.getGL3().glFramebufferTexture(GL.GL_FRAMEBUFFER, attachmentPoint, texture.getGlNumber(), mipLevel);
        }
    }



    @Override
    public void setupDrawBuffers(GL gl) {
        Set<Integer> attachements = targets.keySet();
        int t = attachements.size();
        if(attachements.contains(GL.GL_DEPTH_ATTACHMENT)) {
            t--;
        }
        int[] drawBuffers = new int[t];
        int i = 0;
        for(int attachement : targets.keySet()) {
            if(attachement != GL.GL_DEPTH_ATTACHMENT) {
                drawBuffers[i] = GL.GL_COLOR_ATTACHMENT0 + i;
                i++;
            }
        }
        gl.getGL2().glDrawBuffers(t, drawBuffers, 0);
    }
        
//    @Override
//    public void setupDrawBuffers(GL gl) {
//        int[] drawBuffers = new int[] {GL2.GL_COLOR_ATTACHMENT0};
//        gl.getGL2().glDrawBuffers(1, drawBuffers, 0);
//    }
}
