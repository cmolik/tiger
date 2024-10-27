/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.GL;
import tiger.animation.Interpolable;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class GlslProgramFloatParameter extends GlslProgramParameter implements Interpolable<GlslProgramFloatParameter>{
    float value;
    
    public GlslProgramFloatParameter(String name, float value) {
        super(name);
        this.value = value;
    }
    
    protected void initValue(GL gl, int location) {
       gl.getGL2().glUniform1f(location, value);
    }

    public float getValue() {
        return value;
    }
    
     public void setValue(float value) {
        if(this.value != value) {
            this.value = value;
            TigerChangeEvent e = new TigerChangeEvent(this);
            for(TigerChangeListener l : listeners) {
                l.stateChanged(e);
            }
        }
     }
    
    public boolean parseValue(String string) {
       try {
           setValue(Float.parseFloat(string));
       }
       catch(Exception e) {
           return false;
       }
       return true;
    }
    
    public String toString() {
        return "" + value;
    }

    @Override
    public void interpolate(GlslProgramFloatParameter value1, GlslProgramFloatParameter value2, float t) {
        if(t <= 0) {
            this.value = value1.getValue();
        }
        else if (t >= 1){
            this.value = value2.getValue();
        }
        else {
            this.value = (1f - t)*value1.getValue() + t*value2.getValue();
        }
    }
}
