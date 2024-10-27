/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

/**
 *
 * @author cmolikl
 */
public class GlslProgram {

    private int glNumber;

    public int getGlNumber() {
        return glNumber;
    }

    public void init(GL gl) {
        glNumber = gl.getGL2().glCreateProgram();
    }

    public void attachGlslShader(GL gl, GlslShader shader) {
        GL3 gl3 = gl.getGL3();
        gl3.glAttachShader(glNumber, shader.getGlNumber());
        if (shader instanceof GlslGeometryShader) {
            GlslGeometryShader gs = (GlslGeometryShader) shader;
            gl3.glProgramParameteriARB(glNumber, GL3.GL_GEOMETRY_INPUT_TYPE_ARB, gs.getInputType());
            gl3.glProgramParameteriARB(glNumber, GL3.GL_GEOMETRY_OUTPUT_TYPE_ARB, gs.getOutputType());
            int[] temp = new int[1];
            gl3.glGetIntegerv(GL3.GL_MAX_GEOMETRY_OUTPUT_VERTICES_ARB, temp, 0);
            gl3.glProgramParameteriARB(glNumber, GL3.GL_GEOMETRY_VERTICES_OUT_ARB, temp[0]);
        }
    }

    public void detachGlslShader(GL gl, GlslShader shader) {
        gl.getGL2().glDetachShader(glNumber, shader.getGlNumber());
    }

    public void linkProgram(GL gl) {
        GL2 gl2 = gl.getGL2();
        gl2.glLinkProgram(glNumber);
        int[] tmp = new int[1];
        gl2.glGetProgramiv(glNumber, GL2.GL_LINK_STATUS, tmp, 0);
        if (tmp[0] == 0) {
            System.out.println("GLSL program failed to link:");
            byte[] buffer = new byte[1000];
            gl2.glGetProgramInfoLog(glNumber, 1000, (int[]) null, 0, buffer, 0);
            System.out.println("Error message: \"" + new String(buffer) + "\"");
        }
    }

    public void useProgram(GL gl) {
        gl.getGL2().glUseProgram(glNumber);
    }
}
