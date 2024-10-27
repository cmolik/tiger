/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import scene.Scene;
import mesh.loaders.ObjLoader;
import scene.surface.mesh.Mesh;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.GlslProgramFloatParameter;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.ui.FloatSlider;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class Example5 {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("The atempt to set system Look&Feel failed. Continuing with default.");
        }

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("D:/Projects/Data/cow_triangles.obj");

        //FrameBuffer.depthBufferType = GL2.GL_DEPTH_COMPONENT;
        Texture2D texture1 = new Texture2D(GL.GL_RGBA8, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE);
        FrameBuffer fbo1 = new FrameBuffer(true, texture1);

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(0f, 0f, 0f, 1f);
        rs.enable(GL.GL_DEPTH_TEST);
        
        GlslProgramFloatParameter darknes = new GlslProgramFloatParameter("darknes", 0.15f);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong2.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        Pass pass1 = new Pass(vertexStream, fragmentStream);
        pass1.scene = scene;
        pass1.renderState = rs;
        pass1.glslVaryingParameters.add(darknes);
        pass1.setTarget(fbo1);

        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL.GL_DEPTH_TEST);

        Pass pass2 = new Saq(texture1);
        pass2.renderState = rs;

        Effect effect = new Effect();
        effect.addGLEventListener(pass1);
        effect.addGLEventListener(pass2);
        effect.addTexture(texture1);
        effect.addTarget(fbo1);
        
        FloatSlider darknesSlider = new FloatSlider(darknes, false, JSlider.HORIZONTAL, 0f, 1f);

        Window w = new Window(scene, 512, 512, true);
        w.setEffect(effect);
        w.params.add(new JLabel("Darknes"));
        w.params.add(darknesSlider);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
    }
}
