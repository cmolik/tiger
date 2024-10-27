/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.scattering;

import com.jogamp.common.nio.Buffers;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.Pass;

/**
 *
 * @author cmolikl
 */
public class ScatteringPass extends Pass {
    
    protected int width;
    protected int height;
    
    private int VBOVertices;
    private FloatBuffer vertices;
    
    public ScatteringPass(URL vertexShader, URL fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

    public ScatteringPass(InputStream vertexShader, InputStream fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

     public ScatteringPass(InputStream vertexShader, InputStream fragmentShader, InputStream geometryShader, int inputType, int outputType, int width, int height) {
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
        renderPoints(gl);
        gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        int[] temp = new int[1];
        gl.glGenBuffers(1, temp, 0);
        
        VBOVertices = temp[0];
        
        super.init(drawable);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        InitPointVBO(gl, width, height);
        super.reshape(drawable, x, y, width, height);
    }
    
    public void InitPointVBO(GL2 gl, int width, int height) {
        
        float offsetX = 1.0f/width;
        float offsetY = 1.0f/height;
        float ox = 0.5f*offsetX;
        float oy = 0.5f*offsetY;
        
        int numPoints = width*height;
        vertices = ByteBuffer.allocateDirect(numPoints*3*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for(float x = ox; x < 1f; x += offsetX) {
            for(float y = oy; y < 1f; y += offsetY) {
                vertices.put(x);
                vertices.put(y);
                vertices.put(0.0f);
            }
        }
        
        vertices.rewind();
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT,
                            vertices, GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    } 
    
    private void renderPoints(GL2 gl) {
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        
        gl.glDrawArrays(GL2.GL_POINTS, 0, vertices.capacity()/3);
        
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
} 
