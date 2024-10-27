/*
 * BasicJoglListener.java
 *
 * Created on 21.10.2007, 14:16:18
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import com.jogamp.opengl.util.Animator;
import gleem.CameraParameters;
import gleem.ExaminerViewer;
import gleem.MouseButtonHelper;
import gleem.linalg.Rotf;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import scene.Scene;
import scene.surface.Surface;

/**
 *
 * @author cmolikl
 */
public class GLPanel extends GLCanvas {

    protected ExaminerViewer viewer;
    protected Scene<? extends Surface> scene = null;
    protected Animator animator = null;
    protected Pass effect = null;
    long prevTime;
    int fps = 0;
    
    boolean inicialized;
    boolean loadEffect;
    boolean loadCameraParameters;
    CameraParameters cameraParameters;
    
    
    public GLPanel(GLCapabilities capabilities) {
        super(capabilities);       
        addGLEventListener(new GLPanelEventListener());
        viewer = new OrthogonalExaminerViewer(MouseButtonHelper.numMouseButtons());
        viewer.setNoAltKeyMode(true);
        viewer.setAutoRedrawMode(true);
        viewer.rotateFaster();
    }
    
    public void setScene(Scene<? extends Surface> scene) {
        this.scene = scene;
        if(inicialized) {
            viewer.detach();
            viewer.attach(this, scene);
            viewer.setOrientation(new Rotf());
            viewer.viewAll(getGL());
            repaint();
        }
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void setEffect(Pass effect) {
        this.effect = effect;
        if(inicialized) {
            loadEffect = true;
            repaint();
        }
    }
    
    public void setCameraParameters(CameraParameters cameraParameters) {
        viewer.setOrientation(cameraParameters.getOrientation());
        viewer.setPosition(cameraParameters.getPosition());
        repaint();
    }
    
    public CameraParameters getCameraParameters() {
        return (CameraParameters) viewer.getCameraParameters().clone();
    }
    
    public void viewAll() {
        if(inicialized) {
            viewer.viewAll(getGL());
            repaint();
        }
    }
    
    private class GLPanelEventListener implements GLEventListener {

        public void init(GLAutoDrawable drawable) {
            //GL gl = drawable.getGL();
            //gl.setSwapInterval(0);

            //drawable.setGL(new DebugGL(drawable.getGL()));
            inicialized = true;
            if(effect != null) {
                effect.init(drawable);
            }
            if(scene != null) {
                // Register the window with the ManipManager
                viewer.attach(drawable, scene);
                viewer.viewAll(getGL());
            }
        }

        public void display(GLAutoDrawable drawable) {
            if(effect != null && scene != null) {
                if(loadEffect) {
                    loadEffect = false;
                    effect.init(drawable);
                }
                GL gl = drawable.getGL();
                viewer.update(gl);
                effect.prepare(drawable);
                effect.renderScene(drawable, scene);
            }
        }

        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            if(effect != null) {
                effect.reshape(drawable, x, y, width, height);
            }
        }

        public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        }

        public void dispose(GLAutoDrawable arg0) {
        }
    }
}


