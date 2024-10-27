/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import gleem.BSphere;
import gleem.BSphereProvider;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import tiger.animation.Interpolable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cmolikl
 */
public class PerspectiveCamera implements GLEventListener, Interpolable<PerspectiveCamera> {
    
    public class CameraZoomListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            float wheelRotation = (float) e.getPreciseWheelRotation();
            //System.out.println("FOV: " + fov.getValue());
            //System.out.println("Wheel rotation: " + wheelRotation);
            if(wheelRotation < 0) {
                fov *= 1.1f;
                if(fov > 180f) fov = 180f;
            }
            else {
                fov /= 1.1f;
                if(fov < 1f) fov = 1f;
                
            }
            //System.out.println("FOV: " + fov.getValue());
            fireChange();
        }
    };
    
    public class CameraOrbitListener implements MouseListener, MouseMotionListener {
        float pixelsToPI = (float) (Math.PI/360f);
        int startX;
        int startY;
        
        @Override
        public void mouseDragged(MouseEvent e) {
            int currentX = e.getX();
            int currentY = e.getY();
            int distX = startX - currentX;
            int distY = currentY - startY;
            startX = currentX;
            startY = currentY;
            float pitch = distX * pixelsToPI;
            float roll = distY * pixelsToPI;

            upvector.normalize();
            
            Vec3f or = new Vec3f();
            or.sub(position, center);
            float length = or.length();
            or.normalize();
            
            Vec3f left = new Vec3f();
            left.cross(or, upvector);

            Rotf pitchRot = new Rotf();
            pitchRot.set(upvector, pitch);

            Rotf rollRot = new Rotf();
            rollRot.set(left, roll);

            Rotf rot = pitchRot.times(rollRot);

            
            Vec3f npos = new Vec3f();
            rot.rotateVector(or, npos);
            npos.scale(length);
            npos.add(center);

            Vec3f nup = new Vec3f();
            rot.rotateVector(upvector, nup);
            
            position.set(npos);
            upvector.set(nup);
            
            fireChange();
        }

        @Override
        public void mouseMoved(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
    }
    
    public Vec3f position;
    public Vec3f center;
    public Vec3f upvector;
    
    public float fov;
    public float aspect;
    
    public float near;
    public float far;
    
    private float width;
    private float height;
    
    public BSphereProvider boundingSphereProvider;
    
    protected GLEventListener glListener;
    protected HashSet<ChangeListener> changeListeners = new HashSet<>();

    protected PerspectiveCamera() {
        position = new Vec3f(0f, 0f, 1f);
        center = new Vec3f(0f, 0f, 0f);
        upvector = new Vec3f(0f, 1f, 0f);

        fov = 45f;
        aspect = 1f;

        near = 1f;
        far = 10f;
    }

    public PerspectiveCamera(GLEventListener glListener) {
        this();
        this.glListener = glListener;
    }
    
    public PerspectiveCamera(GLEventListener glListener, BSphereProvider boundingSphereProvider) {
        this(glListener);
        this.boundingSphereProvider = boundingSphereProvider;
    }

    public PerspectiveCamera(PerspectiveCamera camera) {
        this();
        set(camera);
    }

    public void set(PerspectiveCamera camera) {
        this.glListener = camera.glListener;
        this.boundingSphereProvider = camera.boundingSphereProvider;
        this.center.set(camera.center);
        this.position.set(camera.position);
        this.upvector.set(camera.upvector);
        this.fov = camera.fov;
        this.far = camera.far;
        this.near = camera.near;
    }
    
    public void viewAll() {
        if(boundingSphereProvider == null) return;
        
        BSphere boundingSphere = boundingSphereProvider.getBoundingSphere();
        center.set(boundingSphere.getCenter());
        
        float radius = boundingSphere.getRadius();
        position.set(center);
        double alpha = fov / 360.0 *  Math.PI;
        float cameraDistanceFromCenter = radius / (float) Math.sin(alpha);
        position.set(2, center.z() + cameraDistanceFromCenter);
        
        near = cameraDistanceFromCenter - radius;
        far = cameraDistanceFromCenter + radius;
    }

    @Override
    public void init(GLAutoDrawable glad) {
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
        glu.gluLookAt(position.x(), position.y(), position.z(),
                      center.x(), center.y(), center.z(),
                      upvector.x(), upvector.y(), upvector.z());
        
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        //gl.glFrustumf(left.value * xScale, right.value * xScale, bottom.value * yScale, top.value * yScale, near.value, far.value);
        glu.gluPerspective(fov, aspect, near, far);
        
        glListener.display(glad);
        
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        aspect = width/height;
        glListener.reshape(glad, x, y, width, height);
    }

    @Override
    public void interpolate(PerspectiveCamera camera1, PerspectiveCamera camera2, float t) {

        Vec3f orientation1 = new Vec3f();
        orientation1.sub(camera1.position, camera1.center);
        Vec3f orientation2 = new Vec3f();
        orientation2.sub(camera2.position, camera2.center);

        //Calculate axis of rotation and angles
        Rotf rot = new Rotf();
        rot.set(orientation1, orientation2);

        Vec3f axis = new Vec3f();
        float angle = rot.get(axis);

        angle *= t;
        rot.set(axis, angle);
        rot.rotateVector(orientation1, position);
        position.add(center);
        Vec3f up = new Vec3f();
        rot.rotateVector(camera1.upvector, up);

        Rotf upRot = new Rotf();
        upRot.set(up, camera2.upvector);
        Vec3f upAxis = new Vec3f();
        float upAngle = upRot.get(upAxis);

        upAngle *= t;
        rot.set(upAxis, upAngle);
        rot.rotateVector(up, upvector);

        fov = (1f-t)*camera1.fov + t*camera2.fov;
        near = (1f-t)*camera1.near + t*camera2.near;
        far = (1f-t)*camera1.far + t*camera2.far;

        fireChange();
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
