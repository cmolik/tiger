/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 *
 * @author cmolikl
 */
public class ScreenAlignedQuad {
    
    private GlslProgram program;
    private Texture2D texture;
    
    private ScreenAlignedQuad(GlslProgram program, Texture2D texture) {
        this.program = program;
        this.texture = texture;
    }
    
    public static ScreenAlignedQuad init(GL gl, Texture2D texture) {
        
        return null; 
    }
    
    public void draw(GL gl) {
        program.useProgram(gl);
        gl.glEnable(GL.GL_TEXTURE_2D);
        texture.bind(gl);
        GL2 gl2 = gl.getGL2();
        gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord2f(0.0f, 1.0f);
        gl2.glVertex3f(5.0f, 5.0f, 0.0f);
        gl2.glTexCoord2f(1.0f, 1.0f);
        gl2.glVertex3f(-5.0f, 5.0f, 0.0f);
        gl2.glTexCoord2f(1.0f, 0.0f);
        gl2.glVertex3f(-5.0f, -5.0f, 0.0f);
        gl2.glTexCoord2f(0.0f, 0.0f);
        gl2.glVertex3f(5.0f, -5.0f, 0.0f);
        gl2.glEnd();
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glFlush();
    }
}
