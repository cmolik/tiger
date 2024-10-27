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
public class NonresizingScatteringPass_old extends Pass {
    
    private float offsetX;
    private float offsetY;
    protected int inputWidth;
    protected int inputHeight;
    protected int outputWidth;
    protected int outputHeight;
    
    private int callList = 0;
    //private int[] viewport = new int[4];
    
    public NonresizingScatteringPass_old(URL vertexShader, URL fragmentShader, 
        int inputWidth, int inputHeight, int outputWidth, int outputHeight) 
    {
        super(vertexShader, fragmentShader);
        this.inputWidth = inputWidth;
        this.inputHeight = inputHeight;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    public NonresizingScatteringPass_old(InputStream vertexShader, InputStream fragmentShader,
        int inputWidth, int inputHeight, int outputWidth, int outputHeight)
    {
        super(vertexShader, fragmentShader);
        this.inputWidth = inputWidth;
        this.inputHeight = inputHeight;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }
    
    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        gl.glViewport(0, 0, outputWidth, outputHeight);
        gl.glCallList(callList);
        gl.glPopAttrib();
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        offsetX = 1.0f / inputWidth;
        offsetY = 1.0f / inputHeight;
        
        callList = gl.glGenLists(1);	
        gl.glNewList(callList, GL2.GL_COMPILE);
        renderPoints(gl);
        gl.glEndList();
        
        super.init(drawable);
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
