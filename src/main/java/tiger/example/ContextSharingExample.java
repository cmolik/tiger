/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.example;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class ContextSharingExample {
    public static void main(String[] args) {
        
        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/cow_triangles.obj");
        
        Texture2D texture = new Texture2D(100, 100);
        FrameBuffer buffer = new FrameBuffer(true, texture);
        
        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(1f, 1f, 1f, 1f);
        rs.enable(GL.GL_DEPTH_TEST);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong2.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        Pass pass = new Pass(vertexStream, fragmentStream);
        pass.setTarget(buffer);
        pass.scene = scene;
        pass.renderState = rs;
        
        rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(0f, 0f, 0f, 0f);
        rs.disable(GL.GL_DEPTH_TEST);
        
        Pass saq = new Saq(texture);
        saq.renderState = rs;
        
        Effect e = new Effect();
        e.addTexture(texture);
        e.addTarget(buffer);
        e.addGLEventListener(pass);
        
        //----------------------------------------------------------------------
        
        // GLProfile and GLCapabilities should be equal across all shared GL drawable/context.
        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        
        /*GLPanel glad2 = new GLPanel(caps);
        glad2.addGLEventListener(saq);
        Dimension d = new Dimension(100, 100);
        glad2.setMinimumSize(d);
        glad2.setMaximumSize(d);
        glad2.setPreferredSize(d);*/
        
        Window w = new Window(scene, 512, 512);
        w.setEffect(e);
        //w.addSlave(glad2);
        //w.params.add(glad2);
        w.start();
    }
}
