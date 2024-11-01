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
public class LineScatteringPassVBO extends Pass {
    
    protected int width;
    protected int height;
    
    private int vbo;
    private FloatBuffer lines;
    
    public LineScatteringPassVBO(URL vertexShader, URL fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

    public LineScatteringPassVBO(InputStream vertexShader, InputStream fragmentShader, int width, int height) {
        super(vertexShader, fragmentShader);
        this.width = width;
        this.height = height;
    }

     public LineScatteringPassVBO(InputStream vertexShader, InputStream fragmentShader, InputStream geometryShader, int inputType, int outputType, int width, int height) {
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
        renderLines(gl);
        gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        int[] temp = new int[1];
        gl.glGenBuffers(1, temp, 0);
        
        vbo = temp[0];
        
        super.init(drawable);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        initLineVBO(gl, width, height);
        super.reshape(drawable, x, y, width, height);
    }
    
    public void initLineVBO(GL2 gl, int width, int height) {
        
        float offsetX = 1.0f/width;
        float offsetY = 1.0f/height;
        float ox = 0.5f*offsetX;
        float oy = 0.5f*offsetY;
        
        int numLines = width*height;
        // On the next line, the multiplicatior 10 is calculated as 2 vertices per line (each 3 floats) and 2 texture coordinates per line (each 2 floats) 
        lines = ByteBuffer.allocateDirect(numLines*10*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for(float x = ox; x < 1f; x += offsetX) {
            for(float y = oy; y < 1f; y += offsetY) {
                //First vertex
                lines.put(0f);
                lines.put(0.5f);
                lines.put(0f);

                //Texture coordinates of the first vertex
                lines.put(x);
                lines.put(y);

                //Second vertex
                lines.put(1f);
                lines.put(0.5f);
                lines.put(0f);

                //Texture coordinates of the second vertex
                lines.put(x);
                lines.put(y);
            }
        }
        
        lines.rewind();
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, lines.capacity() * Buffers.SIZEOF_FLOAT,
                            lines, GL.GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
    } 
    
    private void renderLines(GL2 gl) {
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo);
        gl.glVertexPointer(3, GL.GL_FLOAT, 2, 0);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 3, 3);
        
        gl.glDrawArrays(GL2.GL_LINES, 0, lines.capacity()/10);
        
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
} 