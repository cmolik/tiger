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
public class SurfaceGroupRenderer implements SurfaceRenderer<SurfaceGroup> {
    private static final SurfaceGroupRenderer RENDERER = new SurfaceGroupRenderer();
    protected SurfaceGroupRenderer() {
    }
    public void init(GLAutoDrawable drawable, SurfaceGroup sg) {
        for(Surface s : sg.surfaces) {
            SurfaceRenderer r = s.getRenderer();
            r.init(drawable, s);
        }
    }
    public void render(GLAutoDrawable drawable, SurfaceGroup sg) {
        for(Surface s : sg.surfaces) {
            SurfaceRenderer r = s.getRenderer();
            r.render(drawable, s);
        }
    }
    public static SurfaceRenderer getRenderer() {
        return RENDERER;
    }
}
