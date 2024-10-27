/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.scattering;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GLAutoDrawable;
import tiger.core.Pass;

/**
 *
 * @author cmolikl
 */
public class LineScatteringPass extends Pass {
    
    private float offsetX;
    private float offsetY;
    protected int outputWidth;
    protected int outputHeight;

    private int callList = 0;

    public LineScatteringPass(InputStream vertexShader, InputStream fragmentShader, int outputWidth, int outputHeight)
    {
        super(vertexShader, fragmentShader);
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
    }

    @Override
    public void renderScene(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPushAttrib(GL_VIEWPORT);
        gl.glViewport(0, 0, outputWidth, outputHeight);
        gl.glLineWidth(1f);
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
        renderLines(gl);
        gl.glEndList();

        super.reshape(drawable, x, y, width, height);
    }

    private void renderLines(GL2 gl) {
        float ox = offsetX/2f;
        float oy = offsetY/2f;

        gl.glBegin(GL_LINES);
        for(float y = oy; y < 1f; y += offsetY) {
            for(float x = ox; x < 1f; x += offsetX) {
                gl.glMultiTexCoord2f(0, x, y);
                gl.glVertex3f(0f, 0.5f, 0f);
                gl.glVertex3f(1f, 0.5f, 0f);
            }
        }
        gl.glEnd();
    }
}
