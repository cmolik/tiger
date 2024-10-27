/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import scene.Scene;
import mesh.loaders.ObjLoader;
import scene.surface.mesh.Mesh;
import tiger.core.BlitFramebuffer;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Texture2DMultisample;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class Example2ms {
    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = null;
        try {
            scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/anatomy1.obj");
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        //FrameBuffer.depthBufferType = GL2.GL_DEPTH_COMPONENT;
        Texture2DMultisample texture1 = new Texture2DMultisample(GL.GL_RGBA8, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE);
        Texture2D texture2 = new Texture2D(GL.GL_RGBA8, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE);
        //Texture2D texture2 = new Texture2D(GL.GL_RGBA32UI_EXT, GL.GL_RGBA_INTEGER_EXT, GL.GL_UNSIGNED_INT);
        FrameBuffer fbo1 = new FrameBuffer(false, texture1);
        FrameBuffer fbo2 = new FrameBuffer(false, texture2);

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.enable(GL.GL_DEPTH_TEST);
        rs.enable(GL.GL_MULTISAMPLE);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong2.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong2.frag");
        Pass pass1 = new Pass(vertexStream, fragmentStream);
        pass1.scene = scene;
        pass1.renderState = rs;
        pass1.setTarget(fbo1);

        BlitFramebuffer blit = new BlitFramebuffer(fbo1, fbo2, 0, 0, 512, 512, 0, 0, 512, 512, GL2.GL_COLOR_BUFFER_BIT, GL2.GL_LINEAR);

        rs = new RenderState();
        rs.clearBuffers(true);
        rs.disable(GL.GL_DEPTH_TEST);

        Pass pass2 = new Saq(texture2);
        pass2.renderState = rs;

        Effect effect = new Effect();
        effect.addGLEventListener(pass1);
        effect.addGLEventListener(blit);
        effect.addGLEventListener(pass2);
        effect.addTexture(texture1);
        effect.addTexture(texture2);
        effect.addTarget(fbo1);
        effect.addTarget(fbo2);

        Window w = new Window(scene, 512, 512, true);
        w.setEffect(effect);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
    }
}
