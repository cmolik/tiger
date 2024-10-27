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
public abstract class GlslShader {

    protected int glNumber;
    
    public abstract void init(GL gl);
    
    public int getGlNumber() {
        return glNumber;
    }
    
}
