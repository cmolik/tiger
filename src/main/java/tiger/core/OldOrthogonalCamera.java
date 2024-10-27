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
public class OldOrthogonalCamera implements GLEventListener, Interpolable<OldOrthogonalCamera> {
    
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
    
    public int projection = ORTHOGRAPHIC;
    
    public BSphereProvider boundingSphereProvider;
    
    protected GLEventListener glListener;
    protected HashSet<ChangeListener> changeListeners = new HashSet<>();
    
    public OldOrthogonalCamera(GLEventListener glListener) {
        
        position = new GlslProgramFloatArrayParameter("position", new float[] {0f, 0f, 1f});
        center = new GlslProgramFloatArrayParameter("center", new float[] {0f, 0f, 0f});
        upvector = new GlslProgramFloatArrayParameter("upvector", new float[] {0f, 1f, 0f});
        
        left = new GlslProgramFloatParameter("left", -1f);
        right = new GlslProgramFloatParameter("right", 1f);
        bottom = new GlslProgramFloatParameter("bottom", -1f);
        top = new GlslProgramFloatParameter("top", 1f);
        if(projection == ORTHOGRAPHIC) {
            near = new GlslProgramFloatParameter("near", -1f);
            far = new GlslProgramFloatParameter("far", 1f);
        }
        else {
            near = new GlslProgramFloatParameter("near", 0.01f);
            far = new GlslProgramFloatParameter("far", 10f);
        }
        
        this.glListener = glListener;
    }
    
    public OldOrthogonalCamera(GLEventListener glListener, BSphereProvider boundingSphereProvider) {
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
        
        if(projection == ORTHOGRAPHIC) {
            near.value = -radius;
            far.value = radius;
        }
        else {
            near.value = 0.01f;
            far.value = 10f;
        }
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
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        glListener.reshape(glad, x, y, width, height);
    }

    @Override
    public void interpolate(OldOrthogonalCamera value1, OldOrthogonalCamera value2, float t) {
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


