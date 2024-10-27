/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.saq;

import com.jogamp.common.nio.Buffers;
//import com.sun.prism.impl.BufferUtil;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.GlslFragmentShader;
import tiger.core.GlslVertexShader;
import tiger.core.Link;
import tiger.core.Pass;
import tiger.core.Texture;

/**
 *
 * @author cmolikl
 */
public class Saq extends Pass {
    
    public float xStart = -1f;
    public float xEnd = 1f;
    public float yStart = -1f;
    public float yEnd = 1f;

    public float xTexStart = 0f;
    public float xTexEnd = 1f;
    public float yTexStart = 0f;
    public float yTexEnd = 1f;
    
    private int VBOVertices;
    private int VBOTexCoords;
    private FloatBuffer vertices, texCoords;

    protected Saq() {};
    
    public Saq(URL vertexUrl, URL fragmentUrl) {
        super(vertexUrl, fragmentUrl);
    }

    public Saq(InputStream vertexStream, InputStream fragmentStream) {
        super(vertexStream, fragmentStream);
    }

     public Saq(InputStream fragmentStream) {
        super( ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert"), fragmentStream);
    }
    
    public Saq(URL fragmentUrl, Link<Texture> texture) {
        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        vertexShader = new GlslVertexShader(vertexStream);
        fragmentShader = new GlslFragmentShader(fragmentUrl);
        addTexture(texture, "texture");
    }

    public Saq(InputStream fragmentStream, Link<Texture> texture) {
        InputStream vertexUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        vertexShader = new GlslVertexShader(vertexUrl);
        fragmentShader = new GlslFragmentShader(fragmentStream);
        addTexture(texture, "texture");
    }
    
    public Saq(Link<Texture> texture) {
        InputStream vertexUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        InputStream fragmentUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.frag");
        vertexShader = new GlslVertexShader(vertexUrl);
        fragmentShader = new GlslFragmentShader(fragmentUrl);
        addTexture(texture, "texture");
    }
    
    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
//        gl.glBegin(GL2.GL_QUADS);
//        //gl.glTexCoord2f(xTexStart, yTexStart);
//        gl.glMultiTexCoord2f(0, xTexStart, yTexStart);
//        gl.glVertex3f(xStart, yStart, 0.0f);
//        //gl.glTexCoord2f(xTexEnd, yTexStart);
//        gl.glMultiTexCoord2f(0, xTexEnd, yTexStart);
//        gl.glVertex3f(xEnd, yStart, 0.0f);
//        //gl.glTexCoord2f(xTexEnd, yTexEnd);
//        gl.glMultiTexCoord2f(0, xTexEnd, yTexEnd);
//        gl.glVertex3f(xEnd, yEnd, 0.0f);
//        //gl.glTexCoord2f(xTexStart, yTexEnd);
//        gl.glMultiTexCoord2f(0, xTexStart, yTexEnd);
//        gl.glVertex3f(xStart, yEnd, 0.0f);
//        gl.glEnd();
        
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexCoords);
        gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
        //gl.glMultiTexCoordPointerEXT(...);
        
        gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
        
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }
    
    @Override
    public void init (GLAutoDrawable drawable){
        super.init(drawable);
        //System.out.println("TESTING INIT OF SAQ");
        
        float[] vertexArray = {xStart, yStart, 0, xEnd, yStart, 0, xEnd, yEnd, 0, xStart, yEnd, 0.0f};
        float[] texCoordArray = {xTexStart, yTexStart, xTexEnd, yTexStart, xTexEnd, yTexEnd, xTexStart, yTexEnd};
        
        vertices = Buffers.newDirectFloatBuffer(vertexArray.length);
        vertices.put(vertexArray);
        vertices.rewind();
        
        texCoords = Buffers.newDirectFloatBuffer(texCoordArray.length);
        texCoords.put(texCoordArray);
        texCoords.rewind();
        
        GL2 gl = drawable.getGL().getGL2();
        int[] temp = new int[2];
        gl.glGenBuffers(2, temp, 0);
        
        VBOVertices = temp[0];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOVertices);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vertices.capacity() * Buffers.SIZEOF_FLOAT,
                            vertices, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        
        VBOTexCoords = temp[1];
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, VBOTexCoords);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, texCoords.capacity() * Buffers.SIZEOF_FLOAT,
                            texCoords, GL.GL_STATIC_DRAW);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
        
        
    }
//    @Override
//     public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
//        setInitialized(false);
//        init(drawable);
//    }

}
