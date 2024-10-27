/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.scattering;

import java.io.InputStream;
import java.net.URL;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.Pass;

/**
 *
 * @author cmolikl
 */
public class ScatteringPass_old extends Pass {
    
    private float offsetX;
    private float offsetY;
    protected int width;
    protected int height;
    
    private int callList = 0;
    
    public ScatteringPass_old(URL vertexShader, URL fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

    public ScatteringPass_old(InputStream vertexShader, InputStream fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

     public ScatteringPass_old(InputStream vertexShader, InputStream fragmentShader, InputStream geometryShader, int inputType, int outputType, int width, int height) {
        super(vertexShader, fragmentShader, geometryShader, inputType, outputType);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        int[] vp = new int[4];                  // Where The Viewport Values Will Be Stored
        gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
        gl.glViewport(0, 0, width, height);
        gl.glCallList(callList);
        gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        
        offsetX = 1.0f / width;
        offsetY = 1.0f / height;
        
        callList = gl.glGenLists(1);	
        gl.glNewList(callList, GL2.GL_COMPILE);
        renderPoints(gl);
        gl.glEndList();
        
        super.reshape(drawable, x, y, width, height);
    }
    
    private void renderPoints(GL2 gl) {
        float ox = offsetX/2f;
        float oy = offsetY/2f;
        gl.glBegin(GL.GL_POINTS);
        for(float x = ox; x < 1f; x += offsetX) {
            for(float y = oy; y < 1f; y += offsetY) {
                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glVertex3f(x, y, 0.0f);
            }
        }
        gl.glEnd();
    }
} 
