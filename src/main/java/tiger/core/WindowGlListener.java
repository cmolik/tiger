/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import gleem.MouseButtonHelper;
import java.awt.image.BufferedImage;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import tiger.recording.ImageWriter;

/**
 *
 * @author cmolikl
 */
public class WindowGlListener implements GLEventListener {
    
    Window w;
   
    int frame = 0;
    
    WindowGlListener(Window w) {
        this.w = w;
    } 
    
    public void init(GLAutoDrawable drawable) {
        //drawable.setGL(new DebugGL(drawable.getGL()));
        //if(effect != null) {
        
        if(!w.viewPorts.isEmpty()) {
            //GL gl = drawable.getGL();

            if(w.debug) {
                w.setDebug();
            }
        
            //effect.init(drawable);
            for(ViewPort viewPort : w.viewPorts) {
                viewPort.init(drawable);
            }
           
            w.initScreenShotBuffer(drawable);
        }
        
        if(w.bSphereProvider != null && w.viewer == null && w.interaction == true) {
            w.viewer = new OrthogonalExaminerViewer(MouseButtonHelper.numMouseButtons());
            //viewer = new ExaminerViewer(MouseButtonHelper.numMouseButtons());
            w.viewer.attach(w.canvas, w.bSphereProvider);
            w.viewer.setNoAltKeyMode(true);
            if(w.runFastAsPosible) {
                w.viewer.setAutoRedrawMode(false);
            }
            else {
                w.viewer.setAutoRedrawMode(true);
            }
            w.viewer.rotateFaster();
            w.viewer.viewAll(w.canvas.getGL());
        }
    }
    
    public void display(GLAutoDrawable drawable) {

        if(!w.viewPorts.isEmpty()) {
            GL2 gl = drawable.getGL().getGL2();

            if(w.viewer != null && w.interaction) {
                synchronized(w.viewer) {
                    w.viewer.update(gl);
                }
            }

            for(ViewPort viewPort : w.viewPorts) {
                viewPort.display(drawable);
            }
            
            if(w.captureScreenShot.getValue() != 0) {
                w.captureScreenShot.setValue(0);
                w.screenShot = w.getScreenShot(drawable);
                ImageWriter imageWriter = new ImageWriter(w.screenShot, w.screenShotPath + "screenShot" + frame + ".png");
                w.screenShot = new BufferedImage(w.screenShot.getWidth(), w.screenShot.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                imageWriter.start();
                frame++;
            }
            
            if(w.printFps) {
                w.fps++;
                long time = System.currentTimeMillis();
                if (time - w.prevTime >= 1000) {
                    w.currentFps = (float) w.fps;
                    System.out.println("FPS: " + w.currentFps);
                    w.prevTime = time;
                    w.fps = 0;
                }
            }
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        if(!w.viewPorts.isEmpty()) {
            for(ViewPort viewPort : w.viewPorts) {
                viewPort.reshape(drawable, x, y, width, height);
            }
        }
        w.initScreenShotBuffer(drawable);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }
}
