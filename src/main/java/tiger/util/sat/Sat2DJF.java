/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.util.sat;

import com.jogamp.opengl.util.GLBuffers;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import static javax.swing.Spring.height;
import static javax.swing.Spring.width;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.GlslProgramIntParameter;
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
public class Sat2DJF implements GLEventListener {
    
    SwapingLink<Texture> source;
    SwapingLink<FrameBuffer> target;
    
    int iterations = 1;
    GlslProgramIntParameter jumpLength;
    RenderState rs;
    Saq jumpFloodingStep;
    
    
    public Sat2DJF(SwapingLink<Texture> source, SwapingLink<FrameBuffer> target) {
        
        /*
        */
        //System.out.println("SAT init complete here");
        this.source = source;
        this.target = target;
        
        jumpLength = new GlslProgramIntParameter("jumpLength", 1);
        
        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL.GL_DEPTH_TEST);
        rs.disable(GL.GL_BLEND);
        
        InputStream fs = ClassLoader.getSystemResourceAsStream("tiger/util/sat/jumpFloodingStep_jf.frag");
        jumpFloodingStep = new Saq(fs, source);
        jumpFloodingStep.setTarget(target);
        jumpFloodingStep.glslVaryingParameters.add(jumpLength);
        jumpFloodingStep.renderState = rs;
        
        
   }/*
    public void test (SwapingLink<Texture> source, SwapingLink<FrameBuffer> target, GLAutoDrawable glad){
            GL2 gl = glad.getGL().getGL2();
            int width = source.get().getHeight();
            int height = source.get().getHeight();
            gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
            int size = width * height;
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(size);
            target.get().bind(gl);
            gl.glReadBuffer(GL.GL_FRONT);
            gl.glReadPixels(0, 0, source.get().getWidth(), source.get().getHeight(), GL2.GL_RED, GL.GL_FLOAT, buffer);
            int k=0;        
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    if(buffer.get(y*width + x)==1.0){k++;}
                    //System.out.print(buffer.get(y*width + x) + ", ");
                }
                //System.out.println("");
            }
            System.out.println("");
            
            System.out.println("Width: "+width+"Height"+height+" Average Texture value"+(double)k/height/width);
    }
    
    public void test2(GLAutoDrawable glad){
                GL2 gl = glad.getGL().getGL2();
            int width = source.get().getHeight();
            int height = source.get().getHeight();
            gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
            int size = width * height;
            FloatBuffer buffer = GLBuffers.newDirectFloatBuffer(size);
            target.get().bind(gl);
            gl.glReadBuffer(GL.GL_FRONT);
            gl.glReadPixels(0, 0, source.get().getWidth(), source.get().getHeight(), GL2.GL_RED, GL.GL_FLOAT, buffer);
            int k=0;        
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    if(buffer.get(y*width + x)==1.0){k++;}
                    //System.out.print(buffer.get(y*width + x) + ", ");
                }
                //System.out.println("");
            }
            System.out.println("");
            
            System.out.println("Width: "+width+"Height"+height+" Average Texture value"+(double)k/height/width);
    }
*/
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
          
        source.restart();
        target.restart();
        jumpLength.setValue(1);
          
        //testing fillup data for textures end
        if(target.get().getWidth() <= 0 || target.get().getHeight() <= 0) {
            gl.glViewport(0, 0, glad.getSurfaceWidth(), glad.getSurfaceHeight());
        }
        else {
            gl.glViewport(0, 0, target.get().getWidth(), target.get().getHeight());
        }
       
        for(int i = 0; i < iterations; i++) {
            jumpFloodingStep.display(glad);
            source.swap();                        
            target.swap();
            jumpLength.setValue(jumpLength.getValue() * 2);
        }
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        //iterations = (int) Math.ceil(Math.log(Math.max(target.get().getWidth(), target.get().getHeight())) / Math.log(2));
        if(target.get().getWidth() <= 0 || target.get().getHeight() <= 0) {
            iterations = (int) Math.ceil(Math.log(Math.max(i2, i3)) / Math.log(2));
        }
        else {
            iterations = (int) Math.ceil(Math.log(Math.max(target.get().getWidth(), target.get().getHeight())) / Math.log(2));
        }
        //System.out.println("target "+ target.get().getHeight()+ "width "+ target.get().getWidth()+"iterations" + iterations);
        jumpFloodingStep.reshape(glad, i, i1, i2, i3);
    }
    
    public static void main(String... args) {
//        Texture2D t1 = new Texture2D(1024, 1024);
//        Texture2D t2 = new Texture2D(1024, 1024);
/*LAB001-B*/         
        Texture2D t1 = new Texture2D(512, 512);
        Texture2D t2 = new Texture2D(512, 512);
        
//        Texture2D t1 = new Texture2D(256, 256);
//        Texture2D t2 = new Texture2D(256, 256);
        
//        Texture2D t1 = new Texture2D(31, 57);
//        Texture2D t2 = new Texture2D(31, 57);
        
//        Texture2D t1 = new Texture2D(64, 64);
//        Texture2D t2 = new Texture2D(64, 64);
        
//        Texture2D t1 = new Texture2D(32, 32);
//        Texture2D t2 = new Texture2D(32, 32);
        
//        Texture2D t1 = new Texture2D(16, 16);
//        Texture2D t2 = new Texture2D(16, 16);
        
//        Texture2D t1 = new Texture2D(8, 8);
//        Texture2D t2 = new Texture2D(8, 8);
//        Texture2D t1 = new Texture2D(2, 2);
//        Texture2D t2 = new Texture2D(2, 2);
/*LAB001-E*/         
        SwapingLink<Texture> t = new SwapingLink<>(t1, t2);
        
        FrameBuffer f1 = new FrameBuffer(false, t1);
        FrameBuffer f2 = new FrameBuffer(false, t2);
        SwapingLink<FrameBuffer> f = new SwapingLink<>(f2, f1);
        
        Sat2DJF sat = new Sat2DJF(t, f);

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
