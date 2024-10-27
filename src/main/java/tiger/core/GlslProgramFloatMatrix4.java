/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.GL;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class GlslProgramFloatMatrix4 extends GlslProgramParameter {
    Matrix4 value;
    
    public GlslProgramFloatMatrix4(String name, Matrix4 value) {
        super(name);
        this.value = value;
    }
    
    @Override
    protected void initValue(GL gl, int location) {
       gl.getGL3().glUniformMatrix4fv(location, 1, false, value.getMatrix(), 0);
    }

    public Matrix4 getValue() {
        return value;
    }
    
     public void setValue(Matrix4 value) {
        if(this.value != value) {
            this.value = value;
            TigerChangeEvent e = new TigerChangeEvent(this);
            for(TigerChangeListener l : listeners) {
                l.stateChanged(e);
            }
        }
     }
    
    @Override
    public boolean parseValue(String string) {
       return true;
    }
    
    @Override
    public String toString() {
        return "" + value.toString();
    }
}
