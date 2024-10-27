/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.saq;

import java.io.InputStream;
import java.net.URL;
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
public class Saq_old extends Pass {
    
    public float xStart = -1f;
    public float xEnd = 1f;
    public float yStart = -1f;
    public float yEnd = 1f;

    public float xTexStart = 0f;
    public float xTexEnd = 1f;
    public float yTexStart = 0f;
    public float yTexEnd = 1f;

    protected Saq_old() {};
    
    public Saq_old(URL vertexUrl, URL fragmentUrl) {
        super(vertexUrl, fragmentUrl);
    }

    public Saq_old(InputStream vertexStream, InputStream fragmentStream) {
        super(vertexStream, fragmentStream);
    }

     public Saq_old(InputStream fragmentStream) {
        super( ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert"), fragmentStream);
    }
    
    public Saq_old(URL fragmentUrl, Link<Texture> texture) {
        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        vertexShader = new GlslVertexShader(vertexStream);
        fragmentShader = new GlslFragmentShader(fragmentUrl);
        addTexture(texture, "texture");
    }

    public Saq_old(InputStream fragmentStream, Link<Texture> texture) {
        InputStream vertexUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        vertexShader = new GlslVertexShader(vertexUrl);
        fragmentShader = new GlslFragmentShader(fragmentStream);
        addTexture(texture, "texture");
    }
    
    public Saq_old(Link<Texture> texture) {
        InputStream vertexUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        InputStream fragmentUrl = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.frag");
        vertexShader = new GlslVertexShader(vertexUrl);
        fragmentShader = new GlslFragmentShader(fragmentUrl);
        addTexture(texture, "texture");
    }
    
    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glBegin(GL2.GL_QUADS);
        //gl.glTexCoord2f(xTexStart, yTexStart);
        gl.glMultiTexCoord2f(0, xTexStart, yTexStart);
        gl.glVertex3f(xStart, yStart, 0.0f);
        //gl.glTexCoord2f(xTexEnd, yTexStart);
        gl.glMultiTexCoord2f(0, xTexEnd, yTexStart);
        gl.glVertex3f(xEnd, yStart, 0.0f);
        //gl.glTexCoord2f(xTexEnd, yTexEnd);
        gl.glMultiTexCoord2f(0, xTexEnd, yTexEnd);
        gl.glVertex3f(xEnd, yEnd, 0.0f);
        //gl.glTexCoord2f(xTexStart, yTexEnd);
        gl.glMultiTexCoord2f(0, xTexStart, yTexEnd);
        gl.glVertex3f(xStart, yEnd, 0.0f);
        gl.glEnd();
    }

}
