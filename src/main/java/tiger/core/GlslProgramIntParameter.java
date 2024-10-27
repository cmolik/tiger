/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

import com.jogamp.opengl.GL;

/**
 *
 * @author cmolikl
 */
public class GlslProgramIntParameter extends GlslProgramParameter {
    int value;
    
    public GlslProgramIntParameter(String name, int value) {
        super(name);
        this.value = value;
    }
    
    protected void initValue(GL gl, int location) {
       gl.getGL2().glUniform1i(location, value);
    }

    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
        TigerChangeEvent e = new TigerChangeEvent(this);
        for(TigerChangeListener l : listeners) {
            l.stateChanged(e);
        }
    }
    
    public boolean parseValue(String string) {
       try {
           setValue(Integer.parseInt(string));
       }
       catch(Exception e) {
           return false;
       }
       return true;
    }
    
    public String toString() {
        return "" + value;
    }
}
