package tiger.ui.swt;

import com.jogamp.opengl.swt.GLCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import tiger.core.ViewPort;


import com.jogamp.opengl.*;
import java.awt.*;

public class SwtCanvas extends Composite {
    private GLEventListener glEventListener;
    public GLCanvas glCanvas;
    private GLContext glContext;

    public SwtCanvas(Composite parent, GLEventListener glEventListener) {
        super(parent, SWT.NONE);
        setLayout(new FillLayout());

        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        glCanvas = new GLCanvas(this, SWT.NO_BACKGROUND, glcapabilities, null );

        float zoom = glCanvas.getMonitor().getZoom();
        float scaling = zoom / 100f;

        ViewPort viewPort = new ViewPort(0f, 0f, 1f, 1f, scaling, glEventListener);
        glCanvas.addGLEventListener(viewPort);
    }

    @Override
    public void dispose() {
        if(glEventListener != null  && glCanvas != null) {
            glEventListener.dispose(glCanvas);
        }
        super.dispose();
    }
}
