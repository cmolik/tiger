/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mesh.occlusions;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class OcclusionQuery {
    
    private int[] glNumber = new int[1];
    private boolean valid;
    private int[] result = new int[1];
    
    public OcclusionQuery() {
    }
    
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glGenQueries(1, glNumber, 0);
    }
    
    public void beginQuery(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glBeginQuery(GL2.GL_SAMPLES_PASSED, glNumber[0]);
        valid = false;
    }
    
    public void beginQuery(GLAutoDrawable drawable, int queryType) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glBeginQuery(queryType, glNumber[0]);
        valid = false;
    }
    
    public void endQuery(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEndQuery(GL2.GL_SAMPLES_PASSED);
    }
    
    public void endQuery(GLAutoDrawable drawable, int queryType) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEndQuery(queryType);
    }
    
    public int getResult(GLAutoDrawable drawable) {
        if(valid) {
            return result[0];
        }
        else {
            GL2 gl = drawable.getGL().getGL2();
            gl.glGetQueryObjectiv(glNumber[0], GL2.GL_QUERY_RESULT, result, 0);
            valid = true;
            return result[0];
        }
    }
}
