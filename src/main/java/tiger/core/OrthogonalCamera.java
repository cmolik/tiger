/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import gleem.BSphere;
import gleem.BSphereProvider;
import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;
import org.eclipse.swt.events.MouseMoveListener;
import tiger.animation.Interpolable;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

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

/**
 *
 * @author cmolikl
 */
public class OrthogonalCamera implements GLEventListener, Interpolable<OrthogonalCamera> {

    public class CameraZoomListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            float wheelRotation = (float) e.getPreciseWheelRotation();
            if(wheelRotation < 0) {
                scale /= 1.1f;
            }
            else {
                scale *= 1.1f;
            }
            fireChange();
        }
    };

    public class CameraOrbitListener implements MouseListener, MouseMotionListener {
        float pixelsToPI = (float) (Math.PI/360f);
        int startX;
        int startY;

        @Override
        public void mouseDragged(MouseEvent e) {
            if(!e.isShiftDown()) {
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

    public class CameraPanListener implements MouseListener, MouseMotionListener {
        int startX;
        int startY;

        @Override
        public void mouseDragged(MouseEvent e) {
            if(e.isShiftDown()) {
                int currentX = e.getX();
                int currentY = e.getY();
                float distX = startX - currentX;
                float distY = currentY - startY;
                startX = currentX;
                startY = currentY;

                //System.out.println("DistX = " + distX + ", DistY = " + distY);
                pan.set(pan.x() + distX*scale, pan.y() + distY*scale);

                fireChange();
            }
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

    public class SwtCameraZoomListener implements org.eclipse.swt.events.MouseWheelListener {
        @Override
        public void mouseScrolled(org.eclipse.swt.events.MouseEvent mouseEvent) {
            float wheelRotation = (float) mouseEvent.count;
            if(wheelRotation > 0) {
                scale /= 1.1f;
            }
            else {
                scale *= 1.1f;
            }
            fireChange();
        }
    };

    public class SwtCameraOrbitListener implements org.eclipse.swt.events.MouseListener, MouseMoveListener {
        float pixelsToPI = (float) (Math.PI/360f);
        int startX;
        int startY;
        boolean mouseDown = false;

        @Override
        public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent mouseEvent) {

        }

        @Override
        public void mouseDown(org.eclipse.swt.events.MouseEvent mouseEvent) {
            mouseDown = true;
            startX = mouseEvent.x;
            startY = mouseEvent.y;
        }

        @Override
        public void mouseUp(org.eclipse.swt.events.MouseEvent mouseEvent) {
            mouseDown = false;
        }

        @Override
        public void mouseMove(org.eclipse.swt.events.MouseEvent mouseEvent) {
            if(mouseDown) {
                int currentX = mouseEvent.x;
                int currentY = mouseEvent.y;
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
        }
    }

    public Vec3f position;
    public Vec3f center;
    public Vec3f upvector;

    public float scale;
    public Vec2f pan;

    private float width;
    private float height;

    public BSphereProvider boundingSphereProvider;

    protected GLEventListener glListener;
    protected HashSet<TigerChangeListener> changeListeners = new HashSet<>();

    protected OrthogonalCamera() {
        position = new Vec3f(0f, 0f, 1f);
        center = new Vec3f(0f, 0f, 0f);
        upvector = new Vec3f(0f, 1f, 0f);

        scale = 1f;
        pan = new Vec2f(0f, 0f);
    }

    public OrthogonalCamera(GLEventListener glListener) {
        this();
        this.glListener = glListener;
    }

    public OrthogonalCamera(GLEventListener glListener, BSphereProvider boundingSphereProvider) {
        this(glListener);
        this.boundingSphereProvider = boundingSphereProvider;
    }

    public OrthogonalCamera(OrthogonalCamera camera) {
        this();
        set(camera);
    }

    public void set(OrthogonalCamera camera) {
        this.glListener = camera.glListener;
        this.boundingSphereProvider = camera.boundingSphereProvider;
        this.center.set(camera.center);
        this.position.set(camera.position);
        this.upvector.set(camera.upvector);
        this.scale = camera.scale;
        this.pan.set(camera.pan);
    }

    public void viewAll() {
        if(boundingSphereProvider == null) return;

        Vec3f direction = new Vec3f(position);
        direction.sub(center);
        direction.normalize();

        BSphere boundingSphere = boundingSphereProvider.getBoundingSphere();
        center.set(boundingSphere.getCenter());

        float radius = boundingSphere.getRadius();
        direction.scale(radius);
        direction.add(center);
        position.set(direction);
        //position.set(center);
        //position.set(2, center.z() + radius);
        scale = 1f;
        pan.set(0f, 0f);
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

    private float[] mvMatrix = new float[16];
    private float[] pMatrix = new float[16];

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        GLU glu = GLU.createGLU(gl);

        float panX = pan.x()/glad.getSurfaceWidth();
        float panY = pan.y()/glad.getSurfaceHeight();
        float panDist = pan.length();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mvMatrix, 0);
        //gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluLookAt(position.x() + panX, position.y() + panY, position.z(),
                center.x() + panX, center.y() + panY, center.z(),
                upvector.x(), upvector.y(), upvector.z());

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glGetFloatv(GL2.GL_PROJECTION_MATRIX, pMatrix, 0);
        //gl.glPushMatrix();
        gl.glLoadIdentity();

        BSphere boundingSphere = boundingSphereProvider.getBoundingSphere();
        float radius = boundingSphere.getRadius();
        float sRadius = radius*scale;
        //System.out.println("scale: " + scale);
        if(width >= height) {
            float aspect = width/height;
            gl.glOrtho(-sRadius*aspect, sRadius*aspect, -sRadius, sRadius, 0-panDist, 2f*(radius+panDist));
        }
        else {
            float aspect = height/width;
            gl.glOrtho(-sRadius, sRadius, -sRadius*aspect, sRadius*aspect, 0-panDist, 2f*(radius+panDist));
        }


        glListener.display(glad);


        //gl.glPopMatrix();
        gl.glLoadMatrixf(pMatrix, 0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        //gl.glPopMatrix();
        gl.glLoadMatrixf(mvMatrix, 0);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        glListener.reshape(glad, x, y, width, height);
    }

    @Override
    public void interpolate(OrthogonalCamera camera1, OrthogonalCamera camera2, float t) {

        if(camera1 == null || camera2 == null) return;

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

        scale = (1f-t)*camera1.scale + t*camera2.scale;
        pan.set((1f-t)*camera1.pan.x() + t*camera2.pan.x(), (1f-t)*camera1.pan.y() + t*camera2.pan.y());

        fireChange();
    }

    public void addChangeListener(TigerChangeListener l) {
        changeListeners.add(l);
    }

    public void fireChange() {
        TigerChangeEvent e = new TigerChangeEvent(this);
        for(TigerChangeListener l : changeListeners) {
            l.stateChanged(e);
        }
    }

}

