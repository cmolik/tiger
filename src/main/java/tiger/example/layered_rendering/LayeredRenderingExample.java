/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example.layered_rendering;

import java.io.InputStream;
import static com.jogamp.opengl.GL2.*;
import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.Effect;
import tiger.core.FrameBuffer2;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Texture2DArray;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class LayeredRenderingExample {
    
    public static void main(String... args) {
        
        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/cow_triangles.obj");
        
        Texture2DArray color0 = new Texture2DArray(512, 512, 2, GL_FLOAT, GL_RGBA, GL_RGBA32F);
        Texture2DArray color1 = new Texture2DArray(512, 512, 2, GL_FLOAT, GL_RGBA, GL_RGBA32F);
        Texture2DArray depth = new Texture2DArray(512, 512, 2, GL_FLOAT, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT32F);
        
	FrameBuffer2 fbo = new FrameBuffer2();
        fbo.addTarget(depth, GL_DEPTH_ATTACHMENT);
        fbo.addTarget(color0, GL_COLOR_ATTACHMENT0);
        fbo.addTarget(color1, GL_COLOR_ATTACHMENT1);
        
        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setBuffersToClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        rs.setClearColor(1f, 1f, 1f, 1f);
        rs.enable(GL_DEPTH_TEST);

        InputStream vs = ClassLoader.getSystemResourceAsStream("tiger/example/layered_rendering/layers.vert");
        InputStream gs = ClassLoader.getSystemResourceAsStream("tiger/example/layered_rendering/layers.geom");
        InputStream fs = ClassLoader.getSystemResourceAsStream("tiger/example/layered_rendering/layers.frag");
        Pass pass = new Pass(vs, fs, gs, GL_TRIANGLES, GL_TRIANGLE_STRIP);
        pass.scene = scene;
        pass.renderState = rs;
        pass.setTarget(fbo);
        
        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL_DEPTH_TEST);
        
        fs = ClassLoader.getSystemResourceAsStream("tiger/example/layered_rendering/layers_saq.frag");
        Pass saq = new Saq(fs, depth);
        saq.renderState = rs;
        
        Effect e = new Effect();
        e.addTexture(color0);
        e.addTexture(color1);
        e.addTexture(depth);
        e.addTarget(fbo);
        e.addGLEventListener(pass);
        e.addGLEventListener(saq);
        
        Window w = new Window(scene, 512, 512);
        w.setEffect(e);
        w.debug = true;
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
    }
    
}
