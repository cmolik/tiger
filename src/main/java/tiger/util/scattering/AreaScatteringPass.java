/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.util.scattering;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.Pass;
import tiger.core.Window;

/**
 *
 * @author cmolikl
 */
public class AreaScatteringPass extends Pass {
    private float offsetX;
    private float offsetY;
    protected int outputWidth;
    protected int outputHeight;

    private int callList = 0;
    
    public AreaScatteringPass(int outputWidth, int outputHeight)
    {
        super();
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    public AreaScatteringPass(InputStream vertexShader, InputStream fragmentShader, int outputWidth, int outputHeight)
    {
        super(vertexShader, fragmentShader);
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPushAttrib(GL.GL_VIEWPORT);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glViewport(0, 0, outputWidth, outputHeight);
        gl.glCallList(callList);
        gl.glPopAttrib();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();

        offsetX = 1.0f / width;
        offsetY = 1.0f / height;

        callList = gl.glGenLists(1);
        gl.glNewList(callList, GL2.GL_COMPILE);
        renderQuads(gl);
        gl.glEndList();

        super.reshape(drawable, x, y, width, height);
    }

    private void renderQuads(GL2 gl) {
        float ox = offsetX/2f;
        float oy = offsetY/2f;
        gl.glBegin(GL2GL3.GL_QUADS);
        for(float y = oy; y < 1f; y += offsetY) {
            for(float x = ox; x < 1f; x += offsetX) {
                gl.glMultiTexCoord2f(0, x, y);
                gl.glVertex3f(-1f, -1f, 0f);
                gl.glVertex3f(1f, -1f, 0f);
                gl.glVertex3f(1f, 1f, 0f);
                gl.glVertex3f(-1f, 1f, 0f);
            }
        }
        gl.glEnd();
    }
    
    public static void main(String... args) {
        Pass pass = new AreaScatteringPass(256, 256);
        Window w = new Window(256, 256);
        w.setEffect(pass);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.debug = false;
        w.start();
    }
}
