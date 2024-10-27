/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example.texture;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import scene.Scene;
import mesh.loaders.ObjLoader;
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
public class RenderToTexture {
    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("D:/Projects/Data/cow_triangles.obj");
        for(Mesh m : scene.getAllMeshes()) {
            m.renderMethod = Mesh.VERTEX_BUFFER;
        }

        Texture2D texture = new Texture2D(GL.GL_RGBA8, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE);
        
        FrameBuffer fbo = new FrameBuffer(true, texture);
        //FrameBuffer.depthBufferType = GL2.GL_DEPTH_COMPONENT;

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setBuffersToClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        rs.setClearColor(0f, 0f, 0f, 0f);
        rs.enable(GL.GL_DEPTH_TEST);
        rs.setDepthFunc(GL.GL_LEQUAL);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        Pass pass1 = new Pass(vertexStream, fragmentStream);
        pass1.scene = scene;
        pass1.renderState = rs;
        pass1.setTarget(fbo);

        rs = new RenderState();
        rs.clearBuffers(false);
        rs.disable(GL.GL_DEPTH_TEST);

        Pass pass2 = new Saq(texture);
        pass2.renderState = rs;

        Effect effect = new Effect();
        effect.addGLEventListener(pass1);
        effect.addGLEventListener(pass2);
        effect.addTexture(texture);
        effect.addTarget(fbo);

        Window w = new Window(scene, 512, 512, true);
        w.setEffect(effect);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
        
    }
}
