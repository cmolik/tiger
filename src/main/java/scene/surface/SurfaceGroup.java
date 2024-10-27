/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface;

import java.util.HashSet;

/**
 *
 * @author cmolikl
 */
public class SurfaceGroup extends Surface<SurfaceGroup> {

    protected HashSet<Surface> surfaces;

    public SurfaceGroup(Surface... ss) {
        surfaces = new HashSet<Surface>(ss.length);
        for(Surface s : ss) {
            surfaces.add(s);
        }
        setRenderer(SurfaceGroupRenderer.getRenderer());
    }

    public void add(Surface s) {
        surfaces.add(s);
    }

    public void remove(Surface s) {
        surfaces.remove(s);
    }
}
