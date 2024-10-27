/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import java.util.LinkedList;
import java.util.List;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 *
 * @author cmolikl
 */
public class Effect implements GLEventListener {

    public boolean debug = false;
	
    protected List<Texture> textures;
    protected List<FrameBuffer> targets;
    protected List<GLEventListener> listeners;

    protected boolean initialized = false;

    public Effect() {
        this(null, null, null);
    }

    public Effect(
        List<Texture> textures,
        List<FrameBuffer> targets,
        List<GLEventListener> listeners)
    {
        if(textures != null) {
            this.textures = textures;
        }
        else {
            this.textures = new LinkedList<Texture>();
        }
        
        if(targets != null) {
            this.targets = targets;
        }
        else {
            this.targets = new LinkedList<FrameBuffer>();
        }

        if(listeners != null) {
            this.listeners = listeners;
        }
        else {
            this.listeners = new LinkedList<GLEventListener>();
        }
    }

//    protected Texture2D createTexture(String name) {
//        Texture2D texture = new Texture2D();
//        textures.put(name, texture);
//        return texture;
//    }
//
//    protected FrameBuffer createTarget(String name, Texture2D... textures) {
//        if(textures == null) return null;
//        FrameBuffer target = new FrameBuffer(textures);
//        return target;
//    }

    public void addTexture(Texture texture) {
        textures.add(texture);
    }

    public void addTarget(FrameBuffer target) {
        targets.add(target);
    }

    public void addGLEventListener(GLEventListener listener) {
        listeners.add(listener);
    }

    public void init(GLAutoDrawable drawable) {
        if(debug) {
            drawable.setGL(Window.getDebugGL(drawable.getGL()));
        }

        GL gl = drawable.getGL();

        for(Texture texture : textures) {
            texture.init(drawable);
//            texture.bind(gl);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
//            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        }
        //Texture2D.bindNothing(gl);

        for(FrameBuffer target : targets) {
            target.init(drawable);
        }
        FrameBuffer.bindScreen(gl);

        for(GLEventListener listener : listeners) {
            listener.init(drawable);
        }
        gl.getGL2().glUseProgram(0);

        initialized = true;
    }

    public void display(GLAutoDrawable drawable) {
        if(!initialized) {
            init(drawable);
            reshape(drawable, 0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
        }
        for(GLEventListener listener : listeners) {
            listener.display(drawable);
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        for(Texture texture : textures) {
            texture.reshape(drawable, x, y, width, height);
        }

        for(FrameBuffer target : targets) {
            target.reshape(drawable, x, y, width, height);
        }

        for(GLEventListener listener : listeners) {
            listener.reshape(drawable, x, y, width, height);
        }
    }

    public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
    }

    public void dispose(GLAutoDrawable arg0) {
    }
}
