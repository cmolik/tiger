/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.GL;

/**
 *
 * @author cmolikl
 */
public class GlslProgramUIntParameter extends GlslProgramIntParameter {
    
    public GlslProgramUIntParameter(String name, int value) {
        super(name, value);
    }
    
    protected void initValue(GL gl, int location) {
       gl.getGL2().glUniform1ui(location, value);
    }
}
