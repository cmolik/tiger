/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.scattering;

import com.jogamp.common.nio.Buffers;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.Pass;

/**
 *
 * @author cmolikl
 */
public class NonresizingScatteringPass extends Pass {
    
    private float offsetX;
    private float offsetY;
    protected int inputWidth;
    protected int inputHeight;
    protected int outputWidth;
    protected int outputHeight;
    
    private int callList = 0;
    
    private int VBOVertices;
    // private int VBOColors;
    private FloatBuffer vertices; //, colors;
    //private int[] viewport = new int[4];
    
    public NonresizingScatteringPass(URL vertexShader, URL fragmentShader, 
        int inputWidth, int inputHeight, int outputWidth, int outputHeight) 
    {
        super(vertexShader, fragmentShader);
        this.inputWidth = inputWidth;
        this.inputHeight = inputHeight;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    public NonresizingScatteringPass(InputStream vertexShader, InputStream fragmentShader,
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
        //gl.glCallList(callList);
        renderPoints(gl);
        gl.glPopAttrib();
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        offsetX = 1.0f / inputWidth;
        offsetY = 1.0f / inputHeight;
        initPointVBO(gl);
        
        // callList = gl.glGenLists(1);	
        // gl.glNewList(callList, GL2.GL_COMPILE);
        // renderPoints(gl);
        // gl.glEndList();
        
        super.init(drawable);
    }
    
    public void renderPoints(GL2 gl) {
//        float ox = offsetX/2f;
//        float oy = offsetY/2f;
//        gl.glBegin(GL.GL_POINTS);
//        for(float x = ox; x < 1f; x += offsetX) {
//            for(float y = oy; y < 1f; y += offsetY) {
//                gl.glColor3f(0.0f, 0.0f, 0.0f);
//                gl.glVertex3f(x, y, 0.0f);
//            }
//        }
//        gl.glEnd();
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        // gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        
        // gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOColors);
        // gl.glColorPointer(3, GL.GL_FLOAT, 0, 0);
        
        gl.glDrawArrays(GL2.GL_POINTS, 0, vertices.capacity()/3);
        
        // gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
    
    private void initPointVBO(GL2 gl){
        float ox = offsetX/2f;
        float oy = offsetY/2f;
        int numX = 1+(int)Math.ceil((1-(offsetX/2))*(1/offsetX)-1);
        int numY = 1+(int)Math.ceil((1-(offsetY/2))*(1/offsetY)-1);
        int numPoints = numX*numY;
        vertices = Buffers.newDirectFloatBuffer(3*numPoints);
        for(float x = ox; x < 1f; x += offsetX) {
            for(float y = oy; y < 1f; y += offsetY) {
                vertices.put(x);
                vertices.put(y);
                vertices.put(0.0f);
            }
        }
        vertices.rewind();
        
        int[] temp = new int[2];
        gl.glGenBuffers(2, temp, 0);
        
        VBOVertices = temp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT,
                            vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        
    }
} 
