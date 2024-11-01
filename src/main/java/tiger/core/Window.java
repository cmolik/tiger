/*
 * BasicJoglListener.java
 *
 * Created on 21.10.2007, 14:16:18
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import com.jogamp.common.nio.Buffers;
import gleem.BSphereProvider;
import gleem.CameraParameters;
import gleem.ExaminerViewer;
import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.DebugGL3;
import com.jogamp.opengl.DebugGL3bc;
import com.jogamp.opengl.DebugGL4;
import com.jogamp.opengl.DebugGL4bc;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GL3bc;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4bc;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.GLSharedContextSetter;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import tiger.animation.Animator;
import tiger.ui.BooleanMenuItem;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class Window {
    
    public String cameraPath = "C:/Temp/camera.txt";
    public String screenShotPath = "C:/Temp/";
    public String videoPath = "C:/Temp/screenGrab.mp4";
    public int frameRate = 25;
    public int frameRepetition = 1;
    public GlslProgramIntParameter captureScreenShot = new GlslProgramIntParameter("Make screen shot", 0);
    public GlslProgramIntParameter recordVideo = new GlslProgramIntParameter("Record Video", 0);
    public BufferedImage screenShot;

    protected ExaminerViewer viewer;
    public BSphereProvider bSphereProvider;
    public Animator animator;
    public int animatorType = Animator.REAL_TIME;
    //protected GLEventListener effect;
    protected ArrayList<ViewPort> viewPorts = new ArrayList<>();
    public GLCanvas canvas;
    public ArrayList<GLAutoDrawable> slaves = new ArrayList<>();
    public JFrame frame;
    public JMenuBar menuBar;
    long prevTime;
    int fps = 0;
    float currentFps = 0;

    public boolean runFastAsPosible = false;
    public boolean printFps = false;
    public boolean debug = false;
    public boolean interaction = true;
    public boolean addRecordingMenu = false;

    boolean loadCameraParameters;
    CameraParameters savedCameraParameters;
    LinkedList<JMenu> menus = new LinkedList<>();
    KeyListener keyListener = null;
    
    HashMap<String, Long> stats = new HashMap<>(); 

    Vec3f yAxis = new Vec3f(0, 1, 0);
    Vec3f zAxis = new Vec3f(0, 0, 1);
    Rotf yAxisRotationStep = new Rotf(yAxis, (float) Math.PI / 90);
    //TextRenderer textRenderer;

    public JPanel params = new JPanel();
    public JScrollPane scrolling;
    public int verticalScrollBar = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;

    private float dpiScale = 1f;
    
//    public Window(BSphereProvider bSphereProvider, GLProfile profile) {
//        this.bSphereProvider = bSphereProvider;
//        GLCapabilities capabilities = new GLCapabilities(profile);
//        capabilities.setSampleBuffers(true);
//        capabilities.setNumSamples(4);
//        capabilities.setDoubleBuffered(true);
//        canvas = new GLCanvas(capabilities);
//        //GLJPanel canvas = new GLJPanel(capabilities);
//        canvas.setSize(512, 512);
//        create();
//    }
//
//    public Window(BSphereProvider bSphereProvider) {
//        this(bSphereProvider, GLProfile.getDefault());
//    }
//
//    public Window(BSphereProvider bSphereProvider, boolean debug) {
//        this(bSphereProvider);
//        this.debug = debug;
//    }

    public Window(int width, int height, GLProfile profile) {
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setSampleBuffers(true);
        capabilities.setNumSamples(4);
        capabilities.setDoubleBuffered(true);
        canvas = new GLCanvas(capabilities);
        canvas.setSize(width, height);
        canvas.setPreferredSize(new Dimension(width, height));

        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        String version = System.getProperty("java.version");
        if(!version.startsWith("1.")) {
            this.dpiScale = dpi/96.0f;
        }

        create();
    }

    public Window(int width, int height) {
        this(width, height, GLProfile.getDefault());
    }
    
    public Window(BSphereProvider bSphereProvider, int width, int height) {
        this(width, height);
        this.bSphereProvider = bSphereProvider;
    }

    public Window(BSphereProvider bSphereProvider, int width, int height, boolean debug) {
        this(bSphereProvider, width, height);
        this.debug = debug;
    }

    void setDebug() {
        GL gl = canvas.getGL();
        canvas.setGL(Window.getDebugGL(gl));
    }

    public static GL getDebugGL(GL gl) {
        GL debugGL = null;
        if (gl.isGL4bc()) {
            final GL4bc gl4bc = gl.getGL4bc();
            debugGL = new DebugGL4bc(gl4bc);
        }
        else {
            if (gl.isGL4()) {
                final GL4 gl4 = gl.getGL4();
                debugGL = new DebugGL4(gl4);
            }
            else {
                if (gl.isGL3bc()) {
                    final GL3bc gl3bc = gl.getGL3bc();
                    debugGL = new DebugGL3bc(gl3bc);
                }
                else {
                    if (gl.isGL3()) {
                        final GL3 gl3 = gl.getGL3();
                        debugGL = new DebugGL3(gl3);
                    }
                    else {
                        if (gl.isGL2()) {
                            final GL2 gl2 = gl.getGL2();
                            debugGL = new DebugGL2(gl2);
                        }
                    }
                }
            }
        }
        return debugGL;
    }
    
    public ExaminerViewer getControler() {
        return viewer;
    }

    public void setControler(ExaminerViewer viewer) {
        this.viewer = viewer;
    }
    
    public void setEffect(GLEventListener effect) {
        //this.effect = effect;
        viewPorts.clear();
        ViewPort viewPort = new ViewPort(0f, 0f, 1f, 1f, dpiScale, effect);
        viewPorts.add(viewPort);
    }

    public void addViewPort(ViewPort viewPort) {
        viewPorts.add(viewPort);
    }

    public void setAnimator(Animator animator) {
        this.animator = animator;
    }
    
    public void addMenu(JMenu menu) {
        menus.add(menu);
    }

    public LinkedList<JMenu> getMenus() {
        return menus;
    }
    
    public void setKeyListener(KeyListener listener) {
        keyListener = listener;
    }

    public void saveCameraParameters() {
        savedCameraParameters = (CameraParameters) viewer.getCameraParameters().clone();
        float radius = 100;
        if(viewer instanceof OrthogonalExaminerViewer) {
            radius = ((OrthogonalExaminerViewer) viewer).getRadius();
        }
        Writer w = null;
        try {
            File file = new File(cameraPath);
            file.delete();
            file.createNewFile();
            w = new BufferedWriter(new FileWriter(file));
            Vec3f position = savedCameraParameters.getPosition();
            w.write(position.get(0) + "\n");
            w.write(position.get(1) + "\n");
            w.write(position.get(2) + "\n");
            Mat4f mat = new Mat4f();
            savedCameraParameters.getOrientation().toMatrix(mat);
            w.write(mat.get(0, 0) + "\n");
            w.write(mat.get(0, 1) + "\n");
            w.write(mat.get(0, 2) + "\n");
            w.write(mat.get(1, 0) + "\n");
            w.write(mat.get(1, 1) + "\n");
            w.write(mat.get(1, 2) + "\n");
            w.write(mat.get(2, 0) + "\n");
            w.write(mat.get(2, 1) + "\n");
            w.write(mat.get(2, 2) + "\n");
            float vertFOV = savedCameraParameters.getVertFOV();
            w.write(vertFOV + "\n");
            w.write(radius + "");
            w.close();
//            System.out.println("Position: " + savedCameraParameters.getPosition());
//            System.out.println("Orientation: " + savedCameraParameters.getOrientation());
//            System.out.println("VertFOV: " + savedCameraParameters.getVertFOV());
        } 
        catch (IOException ex) {
            
        } 
        finally {
            try {
                w.close();
            } 
            catch (Exception ex) {}
        }
    }

    public void loadCameraParameters() {
        BufferedReader r = null;
        if(savedCameraParameters == null) {
            savedCameraParameters = new CameraParameters();
        }
        try {
            File file = new File(cameraPath);
            if(file.exists()) {
                r = new BufferedReader(new FileReader(file));
                Vec3f position = new Vec3f();
                position.set(0, Float.parseFloat(r.readLine()));
                position.set(1, Float.parseFloat(r.readLine()));
                position.set(2, Float.parseFloat(r.readLine()));
                savedCameraParameters.setPosition(position);
                Mat4f mat = new Mat4f();
                mat.set(0, 0, Float.parseFloat(r.readLine()));
                mat.set(0, 1, Float.parseFloat(r.readLine()));
                mat.set(0, 2, Float.parseFloat(r.readLine()));
                mat.set(1, 0, Float.parseFloat(r.readLine()));
                mat.set(1, 1, Float.parseFloat(r.readLine()));
                mat.set(1, 2, Float.parseFloat(r.readLine()));
                mat.set(2, 0, Float.parseFloat(r.readLine()));
                mat.set(2, 1, Float.parseFloat(r.readLine()));
                mat.set(2, 2, Float.parseFloat(r.readLine()));
                Rotf orientation = new Rotf();
                orientation.fromMatrix(mat);
                savedCameraParameters.setOrientation(orientation);
                savedCameraParameters.setVertFOV(Float.parseFloat(r.readLine()));
                if(viewer instanceof OrthogonalExaminerViewer) {
                    float radius = 100;
                    try {
                        radius = Float.parseFloat(r.readLine());
                    }
                    finally {
                        ((OrthogonalExaminerViewer) viewer).setRadius(radius);
                    }
                }
                r.close();
                loadCameraParameters = true;
            }
        } 
        catch (IOException ex) {
        } 
        finally {
            try {
                r.close();
            } 
            catch (Exception ex) {}
        }
        
    }
    
    private void add(String key, long time) {
        Long t = stats.get(key);
        if(t == null) {
            t = 0L;
        }
        stats.put(key, t + time);
    }
    
    public void create() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("The atempt to set system Look&Feel failed. Continuing with default.");
        }
        
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        menuBar = new JMenuBar();
        
        if(addRecordingMenu) { 
        JMenu recordingMenu = new JMenu("Recording");
        menuBar.add(recordingMenu);
        BooleanMenuItem screenShotMenuItem = new BooleanMenuItem("Make screenshot", captureScreenShot);
        recordingMenu.add(screenShotMenuItem);
        }
        
        LayoutManager layout = new BoxLayout(params, BoxLayout.Y_AXIS);
        params.setLayout(layout);
        scrolling = new JScrollPane(params, verticalScrollBar, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame = new JFrame("");
        frame.setIconImage(new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR));
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.getContentPane().add(scrolling, BorderLayout.EAST);
        //frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void build() {
        canvas.addGLEventListener(new WindowGlListener(this));
        if(keyListener != null) {
            canvas.addKeyListener(keyListener);
        }

        // Add custom menus
        for(JMenu menu : menus) {
            menuBar.add(menu);
        }
    }

    public void run() {
        if(!slaves.isEmpty()) {
            canvas.display();
            for(GLAutoDrawable slave : slaves) {
                if(slave instanceof GLSharedContextSetter) {
                    ((GLSharedContextSetter) slave).setSharedAutoDrawable(canvas);
                }
            }      
        }

        frame.pack();
        frame.setVisible(true);
        
        if(!slaves.isEmpty()) {
            canvas.display();
            for(GLAutoDrawable slave : slaves) {    
                slave.display();
            }        
        }
        
        if(animator == null) animator = new Animator(animatorType);
        animator.addCanvas(canvas);

        if(runFastAsPosible) {
            animator.setRunAsFastAsPossible(true);
            animator.setUpdateOnlyOnChange(false);
        }
        else {
            animator.setRunAsFastAsPossible(false);
            animator.setUpdateOnlyOnChange(true);
        }
        animator.start();
    }

    public void start() {
        build();
        run();
    }
    
    public void printStats() {
        long frames = stats.remove("frames");
        float frametime = 0;
        for(String pass : stats.keySet()) {
            float time = (float) stats.get(pass) / (float) frames;
            frametime += time;
            System.out.println(pass + ": " + time);
        }
        System.out.println("frametime: " + frametime);
    }
    
    public void setCameraPath(String path) {
        cameraPath = path;
    }
    
    public void hideParameters() {
        //scrolling.setVisible(false);
        frame.getContentPane().remove(scrolling);
        //Dimension d = canvas.getSize();
        //d.width += scrolling.getSize().width;
        //canvas.setMinimumSize(d);
        //canvas.setPreferredSize(d);
        //canvas.setMaximumSize(d);
        frame.pack();
        frame.repaint();
    }
    
    public void showParameters() {
        //scrolling.setVisible(true);
        frame.getContentPane().add(scrolling, BorderLayout.EAST);
        //Dimension d = canvas.getSize();
        //d.width -= scrolling.getSize().width;
        //canvas.setMinimumSize(d);
        //canvas.setPreferredSize(d);
        //canvas.setMaximumSize(d);
        //frame.notify();
        frame.pack();
        frame.repaint();
    }
    
    public void setInteractionAlowed(boolean interactionAlowed) {
        interaction = interactionAlowed;
        if(interaction) {
            viewer.setupIntaractionListeners();
            canvas.repaint();
        }
        else {
            viewer.removeInteractionListeners();
            canvas.repaint();
        }
    }
    
    public void addSlave(GLAutoDrawable slave) {
        slaves.add(slave);
    }
    
    int width;
    int height;
    int bands;
    int size;
    ByteBuffer buffer;
    
    public void initScreenShotBuffer(GLAutoDrawable drawable) {
        width = drawable.getSurfaceWidth();
        height = drawable.getSurfaceHeight();
        //if(width % 2 != 0) width--;
        //if(height % 2 != 0) height--;
        bands = 4;
        size = width * height * bands;
        buffer = Buffers.newDirectByteBuffer(size);
        screenShot = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    }
    
    public BufferedImage getScreenShot(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
        gl.glReadPixels(0, 0, width, height, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
        
        for(int x = 0; x < width; x++) 
        {
            for(int y = 0; y < height; y++)
            {
                int i = (x + (width * y)) * bands;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                screenShot.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }
        
        return screenShot;
        
    }
}

