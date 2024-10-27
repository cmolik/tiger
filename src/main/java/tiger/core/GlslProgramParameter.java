package tiger.core;

import tiger.ui.TigerChangeListener;

import java.util.HashSet;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public abstract class GlslProgramParameter {
    protected HashSet<TigerChangeListener> listeners = new HashSet<>();
    public String name;
    
    protected GlslProgramParameter(String name) {
        this.name = name;
    }
    
    public void init(GlslProgram glslProgram) {
            GL2 gl = GLU.getCurrentGL().getGL2();
            int location = gl.glGetUniformLocation(glslProgram.getGlNumber(), name);
            initValue(gl, location);
    }
    
    protected abstract void initValue(GL gl, int location);

    public void addChangeListener(TigerChangeListener l) {
        listeners.add(l);
    }
    
    public abstract boolean parseValue(String s);
    
    @Override
    public abstract String toString();

}
