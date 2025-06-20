package tiger.example.scattering;

import java.io.InputStream;
import java.nio.FloatBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.GlslProgramUIntParameter;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.util.saq.Saq;
import tiger.util.scattering.LineScatteringPassVBO;

public class LineScatteringPassVBOExample extends Pass {

    FloatBuffer readBuffer;
    boolean readFromBuffer = true;

    public LineScatteringPassVBOExample() {
        super();
        readBuffer = FloatBuffer.allocate(4 * 32);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.setSwapInterval(0);
    }

    public void display(GLAutoDrawable drawable) {
        if(readFromBuffer) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glReadPixels(0, 0, 32, 1, GL2.GL_RED, GL.GL_FLOAT, readBuffer);
            System.out.println("ID_SUM");
            float[] readArray = readBuffer.array();
            for(int i = 0; i < 32; i++) {
                System.out.println(i + ": " + readArray[i]);
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String... args) {

        int width = 200;
        int height = 200;

        //We expect the id to be from 0 to 31

        //In this parameter, the ids are represented as bits of the integer. The value 9 means that 1st and 4th bits are one. 
        //This way the parameter stores ids 0 and 3.
        GlslProgramUIntParameter ids = new GlslProgramUIntParameter("ids", 9);

        Texture2D idTexture = new Texture2D(width, height, GL2.GL_RGBA32UI, GL2.GL_RGBA_INTEGER, GL2.GL_UNSIGNED_INT);
        idTexture.setParameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        idTexture.setParameter(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        FrameBuffer idBuffer = new FrameBuffer(false, idTexture);

        Texture2D idSumTexture = new Texture2D(32, 1);
        FrameBuffer idSumBuffer = new FrameBuffer(false, idSumTexture);

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.disable(GL.GL_DEPTH_TEST);
        rs.disable(GL.GL_BLEND);

        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/writeId.frag");
        Pass writeId = new Saq(fragmentStream);
        writeId.setTarget(idBuffer);
        writeId.renderState = rs;
        writeId.glslUniformParameters.add(ids);

        fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/displayId.frag");
        Pass displayId = new Saq(fragmentStream, idTexture);
        displayId.glslUniformParameters.add(ids);

        rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(0f, 0f, 0f, 0f);
        rs.disable(GL.GL_DEPTH_TEST);
        rs.enable(GL.GL_BLEND);
        rs.setBlendFunc(GL.GL_ONE, GL.GL_ONE);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/idSum.vert");
        fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/idSum.frag");
        Pass lineScattering = new LineScatteringPassVBO(vertexStream, fragmentStream, 32, 1);
        lineScattering.addTexture(idTexture, "idTexture");
        lineScattering.setTarget(idSumBuffer);
        lineScattering.renderState = rs;

        LineScatteringPassVBOExample readFromBuffer = new LineScatteringPassVBOExample();

        Effect effect = new Effect();
        effect.addTexture(idTexture);
        effect.addTexture(idSumTexture);
        effect.addTarget(idBuffer);
        effect.addTarget(idSumBuffer);
        effect.addGLEventListener(writeId);
        effect.addGLEventListener(lineScattering);
        effect.addGLEventListener(readFromBuffer);
        effect.addGLEventListener(displayId);
        

        Window w = new Window(width, height);
        w.debug = true;
        w.setEffect(effect);
        w.runFastAsPosible = false;
        w.printFps = false;
        w.start();

    }
}
