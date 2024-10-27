/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface;

import java.util.Collection;
import java.util.HashMap;
import tiger.core.GlslProgramParameter;

/**
 *
 * @author cmolikl
 * @param <T>
 */
public abstract class Surface<T extends Surface> {
    
    private static Integer globalId = 0;
    
    public Integer id; 

    protected SurfaceRenderer<T> renderer;
    public String name;
    public boolean visibility = true;
    
    private final HashMap<String, GlslProgramParameter> parameters;
    
    public Surface() {
        synchronized(globalId) {
            id = globalId;
            name = "" + globalId;
            globalId++;
        }
        parameters = new HashMap<>();
    }

    public SurfaceRenderer<T> getRenderer() {
        return renderer;
    }
    
    public void setRenderer(SurfaceRenderer<T> renderer) {
        this.renderer = renderer;
    }
    
    public String getName() {
        return name;
    }
    
    public GlslProgramParameter addParameter(GlslProgramParameter p) {
        return parameters.put(p.name, p);
    }
    
    public GlslProgramParameter removeParameter(GlslProgramParameter p) {
        return parameters.remove(p.name);
    }
    
    public GlslProgramParameter removeParameter(String name) {
        return parameters.remove(name);
    }
    
    public GlslProgramParameter getParameter(String name) {
        return parameters.get(name);
    }
    
    public Collection<GlslProgramParameter> getParameters() {
        return parameters.values();
    }

    public boolean getVisibility() { return visibility; }

    public void setVisibility(boolean visibility) { this.visibility = visibility; }
}
