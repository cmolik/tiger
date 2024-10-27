/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/**
 *
 * @author cmolikl
 */
public class ViewPort implements GLEventListener {
    
    private float x;
    private float y;
    private float width;
    private float height;
    private float dpiScale = 1f;

    private int windowWidth;
    private int windowHeight;

    private GLEventListener listener;

    public ViewPort(float x, float y, float width, float height, GLEventListener listener) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.listener = listener;
    }

    public ViewPort(float x, float y, float width, float height, float scale, GLEventListener listener) {
        this(x, y, width, height, listener);
        this.dpiScale = scale;
    }

    //boolean initialized = false;
    public void init(GLAutoDrawable drawable) {
        //if(!reshape) return;
        //if(initialized) return;
        //System.out.println("Viewport init");
        listener.init(drawable);
        //initialized = true;
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        int[] vp = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, vp, 0);
        gl.glViewport((int) (x * windowWidth), (int) (y * windowHeight), (int) (width * windowWidth), (int) (height * windowHeight));
        //System.out.println("Viewport display");
        listener.display(drawable);
        gl.glViewport(vp[0], vp[1], vp[2], vp[3]);
    }

    //boolean reshape = false;
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        windowWidth = (int)(width * dpiScale);
        windowHeight = (int)(height * dpiScale);
        //System.out.println("Viewport reshape to " + width + ", " + height);
        if(listener != null) listener.reshape(drawable, x, y, (int)(this.width * windowWidth), (int)(this.height * windowHeight));
        //reshape = true;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean bln, boolean bln1) {}

    public void dispose(GLAutoDrawable arg0) {
    }

}
