/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;


import gleem.BSphereProvider;
import gleem.ExaminerViewer;
import gleem.MouseButtonHelper;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class OffscreenWindow implements GLEventListener {
    
    GLEventListener listener;
    Texture2D texture;
    File file;
    String ext;

    BSphereProvider bSphereProvider = null;
    ExaminerViewer viewer = null;

    Texture2D target;
    FrameBuffer fbo;
    Pass pass;

    public OffscreenWindow(BSphereProvider bSphereProvider, Texture2D texture, String path) {
        this.bSphereProvider = bSphereProvider;
        this.texture = texture;
        file = new File(path);
        int dot = path.lastIndexOf(".");
        ext = path.substring(dot + 1);

        target = new Texture2D(texture.width, texture.height, GL.GL_RGBA, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE);
        fbo = new FrameBuffer(target);

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.enable(GL.GL_DEPTH_TEST);

        InputStream frag = ClassLoader.getSystemResourceAsStream("tiger/util/saq/YReversedScreenAlignedQuad.frag");
        pass = new Saq(frag, texture);
        pass.renderState = rs;
        pass.setTarget(fbo);
    }

    public void setEffect(GLEventListener listener) {
        this.listener = listener;
    }

    public void init(GLAutoDrawable drawable) {
        listener.init(drawable);

        target.init(drawable);
        fbo.init(drawable);

        pass.init(drawable);

        if(bSphereProvider != null) {
            viewer = new OrthogonalExaminerViewer(MouseButtonHelper.numMouseButtons());
            //viewer = new ExaminerViewer(MouseButtonHelper.numMouseButtons());
            viewer.attach(drawable, bSphereProvider);
            viewer.setNoAltKeyMode(true);
            viewer.setAutoRedrawMode(true);
            viewer.rotateFaster();
            viewer.viewAll(drawable.getGL());
        }
    }

    public void display(GLAutoDrawable drawable) {
        if(listener != null) {
            GL gl = drawable.getGL();

            if(viewer != null) {
                viewer.update(gl);
            }

            listener.display(drawable);
            pass.display(drawable);
            BufferedImage output = fbo.getData(target);
            try {
                ImageIO.write(output, ext, file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        if(listener != null) {
            listener.reshape(drawable, x, y, width, height);
        }
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }

    public void start() {
        GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);
        capabilities.setDoubleBuffered(true);
        GLCanvas canvas = new GLCanvas(capabilities);
        canvas.setSize(texture.width, texture.height);
        canvas.addGLEventListener(this);
        Frame frame = new Frame();
        frame.add(canvas);
        frame.pack();
        canvas.display();
    }

    public void dispose(GLAutoDrawable arg0) {
    }
}
