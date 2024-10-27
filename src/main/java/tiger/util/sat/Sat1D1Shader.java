/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.util.sat;

import com.jogamp.opengl.util.GLBuffers;
import java.io.InputStream;
import java.nio.FloatBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.GlslProgramIntArrayParameter;
import tiger.core.RenderState;
import tiger.core.SwapingLink;
import tiger.core.Texture;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class Sat1D1Shader implements GLEventListener {
    
    SwapingLink<Texture> source;
    SwapingLink<FrameBuffer> target;
    
    int iterationsX = 1;
    int iterationsY = 1;
    GlslProgramIntArrayParameter jumpLength;
    RenderState rs;
    Saq jumpFloodingStep;
    
    public Sat1D1Shader(SwapingLink<Texture> source, SwapingLink<FrameBuffer> target) {
        this.source = source;
        this.target = target;
        
        jumpLength = new GlslProgramIntArrayParameter("jumpLength", new int[] {1, 0});
        
        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL.GL_DEPTH_TEST);
        rs.disable(GL.GL_BLEND);
        
        InputStream fs = ClassLoader.getSystemResourceAsStream("tiger/util/sat/jumpFloodingStep_xy.frag");
        jumpFloodingStep = new Saq(fs, source);
        jumpFloodingStep.setTarget(target);
        jumpFloodingStep.glslVaryingParameters.add(jumpLength);
        jumpFloodingStep.renderState = rs;
    }

    @Override
    public void init(GLAutoDrawable glad) {
        jumpFloodingStep.init(glad);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        jumpFloodingStep.dispose(glad);
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        target.get().bind(gl);
        gl.glClearColor(1f, 1f, 1f, 1f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        target.swap();
        target.get().bind(gl);
        gl.glClearColor(1f, 1f, 1f, 1f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        target.restart();
        
        gl.glViewport(0, 0, target.get().getWidth(), target.get().getHeight());
        
        int value = 1;
        for(int i = 0; i < iterationsX; i++) {
            jumpLength.setValue(new int[] {value, 0});
            jumpFloodingStep.display(glad);
            
            source.swap();
            
            /*int width = source.get().getHeight();
            int height = source.get().getHeight();
            gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
            int size = width * height;
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(size);
            target.get().bind(gl);
            gl.glReadBuffer(GL.GL_FRONT);
            gl.glReadPixels(0, 0, source.get().getWidth(), source.get().getHeight(), GL2.GL_RED, GL.GL_FLOAT, buffer);
                    
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    System.out.print(buffer.get(y*width + x) + ", ");
                }
                System.out.println("");
            }
            System.out.println("");*/
            
            target.swap();
            value *= 4;
        }
        //System.out.println("END of X");
        //System.out.println("");
        
        value = 1;
        for(int i = 0; i < iterationsY; i++) {
            jumpLength.setValue(new int[] {0, value});
            jumpFloodingStep.display(glad);
            
            source.swap();
            
            /*int width = source.get().getHeight();
            int height = source.get().getHeight();
            gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
            int size = width * height;
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(size);
            target.get().bind(gl);
            gl.glReadBuffer(GL.GL_FRONT);
            gl.glReadPixels(0, 0, source.get().getWidth(), source.get().getHeight(), GL2.GL_RED, GL.GL_FLOAT, buffer);
                    
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    System.out.print(buffer.get(y*width + x) + ", ");
                }
                System.out.println("");
            }
            System.out.println("");*/
            
            target.swap();
            value *= 4; 
        }
        //System.out.println("END of Y");
        //System.out.println("");
        
        source.restart();
        target.restart();
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        iterationsX = (int) Math.ceil(Math.log(target.get().getWidth()) / Math.log(4));
        iterationsY = (int) Math.ceil(Math.log(target.get().getHeight()) / Math.log(4));
        jumpFloodingStep.reshape(glad, i, i1, i2, i3);
    }
    
    public static void main(String... args) {
        Texture2D t1 = new Texture2D(1024, 1024);
        Texture2D t2 = new Texture2D(1024, 1024);
        SwapingLink<Texture> t = new SwapingLink<>(t1, t2);
        
        FrameBuffer f1 = new FrameBuffer(false, t1);
        FrameBuffer f2 = new FrameBuffer(false, t2);
        SwapingLink<FrameBuffer> f = new SwapingLink<>(f2, f1);
        
        Sat1D1Shader sat = new Sat1D1Shader(t, f);
        
        Effect e = new Effect();
        e.addTexture(t1);
        e.addTexture(t2);
        e.addTarget(f1);
        e.addTarget(f2);
        e.addGLEventListener(sat);
        
        Window w = new Window(1024, 1024);
        w.setEffect(e);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
    }   
}
