package tiger.example.scattering;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;

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
import tiger.util.scattering.LineScatteringPass;

public class LineScatteringExample extends Pass {

    FloatBuffer readBuffer;

    public LineScatteringExample() {
        super();
        readBuffer = FloatBuffer.allocate(4 * 32);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glReadPixels(0, 0, 32, 1, GL2.GL_RED, GL.GL_FLOAT, readBuffer);
        System.out.println("ID_SUM");
        float[] readArray = readBuffer.array();
        for(int i = 0; i < 32; i++) {
            System.out.println(i + ": " + readArray[i]);
        }
    }

    /**
     * @param args
     */
    public static void main(String... args) {
        //We expect the id to be from 0 to 31

        //In this parameter, the ids are represented as bits of the integer. The value 9 means that 1st and 4th bits are one. 
        //This way the parameter stores ids 0 and 3.
        GlslProgramUIntParameter ids = new GlslProgramUIntParameter("ids", 9);

        Texture2D idTexture = new Texture2D(200, 200, GL2.GL_RGBA32UI, GL2.GL_RGBA_INTEGER, GL2.GL_UNSIGNED_INT);
        idTexture.setParameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        idTexture.setParameter(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        FrameBuffer idBuffer = new FrameBuffer(false, idTexture);

        Texture2D idSumTexture = new Texture2D(32, 1);
        FrameBuffer idSumBuffer = new FrameBuffer(false, idSumTexture);

        RenderState rs = new RenderState();
        rs.clearBuffers(false);
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

        InputStream VertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/idSum.vert");
        fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/scattering/idSum.frag");
        Pass lineScattering = new LineScatteringPass(VertexStream, fragmentStream, 32, 1);
        lineScattering.setTarget(idSumBuffer);
        writeId.renderState = rs;

        LineScatteringExample readFromBuffer = new LineScatteringExample();

        Effect effect = new Effect();
        effect.addTexture(idTexture);
        effect.addTexture(idSumTexture);
        effect.addTarget(idBuffer);
        effect.addTarget(idSumBuffer);
        effect.addGLEventListener(writeId);
        effect.addGLEventListener(displayId);
        effect.addGLEventListener(lineScattering);
        effect.addGLEventListener(readFromBuffer);

        Window w = new Window(200, 200);
        w.debug = true;
        w.setEffect(effect);
        w.runFastAsPosible = false;
        w.printFps = false;
        w.start();

    }
}
