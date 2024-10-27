/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface;

import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public interface SurfaceRenderer<T extends Surface> {
    public void init(GLAutoDrawable drawable, T surface);
    public void render(GLAutoDrawable drawable, T surface);
}
