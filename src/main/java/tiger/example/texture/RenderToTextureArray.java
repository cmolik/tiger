/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example.texture;

import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.*;
import tiger.core.Window;
import tiger.ui.IntSlider;
import tiger.ui.TigerChangeEvent;
import tiger.util.saq.Saq;

import com.jogamp.opengl.GL;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

/**
 *
 * @author cmolikl
 */
public class RenderToTextureArray {
    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("D:/Projects/Data/cow_triangles.obj");
        for(Mesh m : scene.getAllMeshes()) {
            m.renderMethod = Mesh.VERTEX_BUFFER;
        }

        Texture2DArray texture = new Texture2DArray(3);
        
        FrameBuffer fbo = new FrameBuffer(true, texture);

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setBuffersToClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        rs.setClearColor(0f, 0f, 0f, 1f);
        rs.enable(GL.GL_DEPTH_TEST);
        rs.setDepthFunc(GL.GL_LEQUAL);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/texture/PhongTextureArray.frag");
        Pass pass1 = new Pass(vertexStream, fragmentStream);
        pass1.scene = scene;
        pass1.renderState = rs;
        pass1.setTarget(fbo);

        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL.GL_DEPTH_TEST);

        GlslProgramIntParameter layer = new GlslProgramIntParameter("layer", 0);
        InputStream saqFragmentStream = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuadTextureArray.frag");
        Pass pass2 = new Saq(saqFragmentStream, texture);
        pass2.renderState = rs;
        pass2.glslVaryingParameters.add(layer);

        Effect effect = new Effect();
        effect.addGLEventListener(pass1);
        effect.addGLEventListener(pass2);
        effect.addTexture(texture);
        effect.addTarget(fbo);

        Window w = new Window(scene, 512, 512, true);
        w.interaction = false;

        IntSlider layerSlider = new IntSlider(layer, JSlider.HORIZONTAL, 0, 2);
        w.params.add(layerSlider);

        OrthogonalCamera camera = new OrthogonalCamera(effect, scene);
        camera.addChangeListener((TigerChangeEvent e) -> {w.canvas.repaint();});
        w.canvas.addMouseWheelListener(camera.new CameraZoomListener());
        OrthogonalCamera.CameraOrbitListener l = camera.new CameraOrbitListener();
        w.canvas.addMouseListener(l);
        w.canvas.addMouseMotionListener(l);

        w.setEffect(camera);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
        
    }
}
