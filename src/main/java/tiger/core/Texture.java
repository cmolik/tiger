/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map.Entry;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public abstract class Texture implements Link<Texture> {

    public Buffer imageData = null;
    protected IntBuffer glNumber = null;
    protected int textureType;
    protected HashMap<Integer, Integer> params = new HashMap<Integer, Integer>();;
    protected boolean resizable = true;
    private boolean initialized = false;

    protected int width = 0;
    protected int height = 0;
    protected int depth = 0;
    protected int internalFormat;
    protected int format;
    protected int type;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    public int getDepth() {
        return depth;
    }

    public int getFormat() {
        return format;
    }

    public int getType() {
        return type;
    }

    public int getGlNumber() {
        return glNumber.get(0);
    }

    public Texture get() {
        return this;
    }

    public void bind(GL gl) {
        gl.glBindTexture(textureType, glNumber.get(0));
    }

    protected void allocate(GL gl) {
        glNumber = IntBuffer.allocate(1);
        gl.glGenTextures(1, glNumber);
    }

    public void setParameter(int parameter, int value) {
        params.put(parameter, value);
    }

    protected void setParams(GL gl) {
        for (Entry<Integer, Integer> param : params.entrySet()) {
                gl.glTexParameteri(textureType, param.getKey(), param.getValue());
        }
    }

    public abstract void loadImage(GL gl, int mipLevel, int internalFormat,
        int format, int type, int border, int width, int height, int depth, Buffer data);

    public void init(GLAutoDrawable drawable) {
        if(!initialized) {
            GL gl = drawable.getGL();
            allocate(gl);
            bind(gl);
            setParams(gl);
            if(!resizable) {
                loadImage(gl, 0, internalFormat, format, type, 0, width, height, depth, imageData);
            }
            else {
                loadImage(gl, 0, internalFormat, format, type, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight(), depth, imageData);
            }
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        if(resizable) {
            this.width = width;
            this.height = height;
            GL gl = drawable.getGL();
            loadImage(gl, 0, internalFormat, format, type, 0, width, height, depth, null);
        }
    }

    public void delete(GL gl) {
        gl.glDeleteTextures(1, glNumber);
    }
}
