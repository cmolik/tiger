/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class Timestamp {
    private int[] glNumber = new int[2];
    private long[] time = new long[2];
    private int[] stopTimeAvailable = new int[] {0};
    private long result = 0L;
    private boolean valid = false;
    
    public Timestamp() {}
    
    public void init(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL3();
        gl.glGenQueries(2, glNumber, 0);
    } 
    
    public void start(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL4();
        gl.glQueryCounter(glNumber[0], GL3.GL_TIMESTAMP);
        valid = false;
    }
    
    public void end(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL3();
        gl.glQueryCounter(glNumber[1], GL3.GL_TIMESTAMP);
        valid = false;
    }
    
    public long getResult(GLAutoDrawable glad) {
        if(!valid) {
            GL3 gl = glad.getGL().getGL3();
            stopTimeAvailable[0] = 0;
            while(stopTimeAvailable[0] == 0) {
                gl.glGetQueryObjectiv(glNumber[1], GL3.GL_QUERY_RESULT_AVAILABLE, stopTimeAvailable, 0);
            }

            // get query results
            gl.glGetQueryObjectui64v(glNumber[0], GL3.GL_QUERY_RESULT, time, 0);
            gl.glGetQueryObjectui64v(glNumber[1], GL3.GL_QUERY_RESULT, time, 1);
        
            result = time[1] - time[0];
            valid = true;
        }
        return result;
    }
    
}
