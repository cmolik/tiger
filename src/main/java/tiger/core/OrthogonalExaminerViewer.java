/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import gleem.BSphere;
import gleem.BSphereProvider;
import gleem.ExaminerViewer;
import gleem.ManipManager;
import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.awt.event.MouseEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class OrthogonalExaminerViewer extends ExaminerViewer {

    protected float radius = 100;

    public OrthogonalExaminerViewer(int numMouseButtons) {
        super(numMouseButtons);
    }

    @Override
    protected void init() {
        interactionUnderway = false;
        iOwnInteraction = false;
        button1Down = false;
        button2Down = false;

        int xSize = window.getSurfaceWidth();
        int ySize = window.getSurfaceHeight();
        params.setOrientation(orientation);
        params.setPosition(center);//computePosition(new Vec3f()));
        params.setForwardDirection(Vec3f.NEG_Z_AXIS);
        params.setUpDirection(Vec3f.Y_AXIS);
        //params.setVertFOV((float) Math.PI / 8.0f);
        params.setImagePlaneAspectRatio((float) xSize / (float) ySize);
        params.setXSize(xSize);
        params.setYSize(ySize);
        zNear = -radius;
    }

    @Override
    public void attach(GLAutoDrawable window, BSphereProvider provider) {
        radius = provider.getBoundingSphere().getRadius();
        super.attach(window, provider);
    }

    /** Call this from within your display() method to cause the
    ExaminerViewer to recompute its position based on the visible
    geometry. A BSphereProvider must have already been set or this
    method has no effect. */
    @Override
    public void viewAll(GL gl) {
        if (provider == null) {
            return;
        }
        BSphere bsph = provider.getBoundingSphere();
        dolly.setZ(0f);
        center.set(bsph.getCenter());
        recalc(gl);
    }

    /** Sets the position of this ExaminerViewer. */
    @Override
    public void setPosition(Vec3f position) {
        Vec3f tmp = orientation.rotateVector(Vec3f.NEG_Z_AXIS);
        tmp.scale(dolly.z());
        center.add(position, tmp);
    }
    
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void recalc() {
        // Recompute position, forward and up vectors
        Vec3f tmp = new Vec3f();
        params.setPosition(computePosition(tmp));
        orientation.rotateVector(Vec3f.NEG_Z_AXIS, tmp);
        params.setForwardDirection(tmp);
        orientation.rotateVector(Vec3f.Y_AXIS, tmp);
        params.setUpDirection(tmp);
        params.setOrientation(orientation);

        // Compute modelview matrix based on camera parameters, position and
        // orientation
        Mat4f tmpMat = new Mat4f();
        tmpMat.makeIdent();
        tmpMat.setRotation(orientation);
        tmpMat.setTranslation(center);
        tmpMat.invertRigid();
        params.setModelviewMatrix(tmpMat);

        // Compute perspective matrix given camera parameters
        //Vec3f c = provider.getBoundingSphere().getCenter();
        float r = provider.getBoundingSphere().getRadius();
        tmpMat.makeIdent();
        //tmpMat.set(0, 0, cotangent / aspect);
        //tmpMat.set(1, 1, cotangent);
        if(params.getXSize() <= params.getYSize()) {
            tmpMat.set(0, 0, 1 / radius);
            //tmpMat.set(0, 3, -c.x() / radius);
            tmpMat.set(1, 1, 1 / radius * params.getImagePlaneAspectRatio());
        }
        else {
            tmpMat.set(0, 0, 1 / radius / params.getImagePlaneAspectRatio());
            //tmpMat.set(0, 3, -c.x() / radius);
            tmpMat.set(1, 1, 1 / radius);
        }
        //tmpMat.set(1, 3, -c.y() / radius);
        tmpMat.set(2, 2, -1/r);
        //tmpMat.set(2, 3, - c.z() / r);
        tmpMat.set(3, 3, 1);
        params.setProjectionMatrix(tmpMat);
    }

    @Override
    protected void motionMethod(MouseEvent e, int x, int y) {
        if (interactionUnderway && !iOwnInteraction) {
            ManipManager.getManipManager().mouseDragged(e);
        } else {
            int dx = x - lastX;
            int dy = y - lastY;

            lastX = x;
            lastY = y;

            if ((button1Down && (!button2Down))) {

                // Rotation functionality
                float xRads = (float) Math.PI * -1.0f * dy * rotateSpeed / 1000.0f;
                float yRads = (float) Math.PI * -1.0f * dx * rotateSpeed / 1000.0f;
                Rotf xRot = new Rotf(Vec3f.X_AXIS, xRads);
                Rotf yRot = new Rotf(Vec3f.Y_AXIS, yRads);
                Rotf newRot = yRot.times(xRot);
                orientation = orientation.times(newRot);

            } else if (button2Down && (!button1Down)) {

                // Translate functionality
                // Compute the local coordinate system's difference vector
                Vec3f localDiff = new Vec3f(dollySpeed * -1.0f * dx / 100.0f,
                        dollySpeed * dy / 100.0f,
                        0.0f);
                // Rotate this by camera's orientation
                Vec3f worldDiff = orientation.rotateVector(localDiff);
                // Add on to center
                center.add(worldDiff);

            } else if (button1Down && button2Down) {
                // Zoom functionality 
                radius *= 1.0 + dy/10.0;
                if (radius < 0) {
                    radius = 0.1f;
                }
            }

            if (autoRedrawMode) {
                // Force redraw
                window.display();
                //window.repaint();
            }
        }
    }
}
