/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import gleem.BSphere;
import gleem.BSphereProvider;
import java.util.HashSet;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tiger.animation.Interpolable;

/**
 *
 * @author cmolikl
 */
public class Camera implements GLEventListener, Interpolable<Camera> {
    
    public static final int ORTHOGRAPHIC = 0;
    public static final int PERSPECTIVE = 1;
    
    public GlslProgramFloatArrayParameter position;
    public GlslProgramFloatArrayParameter center;
    public GlslProgramFloatArrayParameter upvector;
    
    public GlslProgramFloatParameter left;
    public GlslProgramFloatParameter right;
    public GlslProgramFloatParameter bottom;
    public GlslProgramFloatParameter top;
    public GlslProgramFloatParameter near;
    public GlslProgramFloatParameter far;
    
    private float width;
    private float height;
    
    public int projection;
    
    public BSphereProvider boundingSphereProvider;
    
    protected GLEventListener glListener;
    protected HashSet<ChangeListener> changeListeners = new HashSet<>();
    
    public Camera(GLEventListener glListener) {
        position = new GlslProgramFloatArrayParameter("position", new float[] {0f, 0f, 1f});
        center = new GlslProgramFloatArrayParameter("center", new float[] {0f, 0f, 0f});
        upvector = new GlslProgramFloatArrayParameter("upvector", new float[] {0f, 1f, 0f});
        
        left = new GlslProgramFloatParameter("left", -1f);
        right = new GlslProgramFloatParameter("right", 1f);
        bottom = new GlslProgramFloatParameter("bottom", -1f);
        top = new GlslProgramFloatParameter("top", 1f);
        near = new GlslProgramFloatParameter("near", -1f);
        far = new GlslProgramFloatParameter("far", 1f);
        
        projection = PERSPECTIVE;
        
        this.glListener = glListener;
    }
    
    public Camera(GLEventListener glListener, BSphereProvider boundingSphereProvider) {
        this(glListener);
        this.boundingSphereProvider = boundingSphereProvider;
    }
    
    public void viewAll() {
        if(boundingSphereProvider == null) return;
        
        BSphere boundingSphere = boundingSphereProvider.getBoundingSphere();
        center.value[0] = boundingSphere.getCenter().x();
        center.value[1] = boundingSphere.getCenter().y();
        center.value[2] = boundingSphere.getCenter().z();
        
        float radius = boundingSphere.getRadius();
        position.value[0] = center.value[0];
        position.value[1] = center.value[1];
        position.value[2] = center.value[2] + radius;
        
        left.value = -radius;
        right.value = radius;
        bottom.value = -radius;
        top.value = radius;
        near.value = -radius;
        far.value = radius;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        width = glad.getSurfaceWidth();
        height = glad.getSurfaceHeight();
        viewAll();
        glListener.init(glad);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        glListener.dispose(glad);
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);
        
        
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        float[] pos = position.getValue();
        float[] cen = center.getValue();
        float[] up = upvector.getValue();
        glu.gluLookAt(pos[0], pos[1], pos[2], cen[0], cen[1], cen[2], up[0], up[1], up[2]);
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        float xScale = 1f;
        float yScale = 1f;
        if(width > height) {
            xScale = width / height;
        }
        else {
            yScale = height / width;
        }
        if(projection == ORTHOGRAPHIC) {
            gl.glOrthof(left.value * xScale, right.value * xScale, bottom.value * yScale, top.value * yScale, near.value, far.value);
        }
        else {
            gl.glFrustumf(left.value * xScale, right.value * xScale, bottom.value * yScale, top.value * yScale, near.value, far.value);
        }  
        
        glListener.display(glad);
        
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        width = glad.getSurfaceWidth();
        height = glad.getSurfaceHeight();
        glListener.reshape(glad, i, i1, i2, i3);
    }

    @Override
    public void interpolate(Camera value1, Camera value2, float t) {
        position.interpolate(value1.position, value2.position, t);
        center.interpolate(value1.center, value2.center, t);
        upvector.interpolate(value1.upvector, value2.upvector, t);
        
        left.interpolate(value1.left, value2.left, t);
        right.interpolate(value1.right, value2.right, t);
        top.interpolate(value1.top, value2.top, t);
        bottom.interpolate(value1.bottom, value2.bottom, t);
        near.interpolate(value1.near, value2.near, t);
        far.interpolate(value1.far, value2.far, t);
    }
    
    public void addChangeListener(ChangeListener l) {
        changeListeners.add(l);
    }
    
    public void fireChange() {
        ChangeEvent e = new ChangeEvent(this);
        for(ChangeListener l : changeListeners) {
            l.stateChanged(e);
        }
    }
    
}
