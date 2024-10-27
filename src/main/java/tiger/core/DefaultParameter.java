package tiger.core;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public enum DefaultParameter {

    MESH_ID("meshId");
    public String name;
    public int location;

    DefaultParameter(String name) {
        this.name = name;
    }
    
    public void init(GlslProgram glslProgram) {
        GL2 gl = GLU.getCurrentGL().getGL2();
        location = gl.glGetUniformLocationARB(glslProgram.getGlNumber(), name);
    }
}
