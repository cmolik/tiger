/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.example.occlusion;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import mesh.loaders.ObjLoader;
import mesh.occlusions.OcclusionQuery;
import scene.Scene;
import scene.surface.mesh.Mesh;
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
public class OcclusionQueriesExample implements GLEventListener {
    
    Scene<Mesh> scene;
    Pass pass;
    OcclusionQuery oq;
    
    
    public OcclusionQueriesExample(Scene<Mesh> scene, Pass pass) {
        this.pass = pass;
        this.scene = scene;
        this.oq = new OcclusionQuery();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        pass.init(glad);
        oq.init(glad);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        //GL3 gl = glad.getGL().getGL3();
        
        oq.beginQuery(glad);
        pass.display(glad);
        oq.endQuery(glad);
        
        int result = oq.getResult(glad);
        System.out.println(result + " fragments written on screen.");
        
    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {
        pass.reshape(glad, i, i1, i2, i3);
    }
    
    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/cow_triangles.obj");
        for(Mesh m : scene.getAllMeshes()) {
            m.renderMethod = Mesh.VERTEX_BUFFER;
        }
        
        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(1f, 1f, 1f, 1f);
        rs.enable(GL.GL_DEPTH_TEST);
        
        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        Pass pass = new Pass(vertexStream, fragmentStream);
        pass.scene = scene;
        pass.renderState = rs;
        
        OcclusionQueriesExample effect = new OcclusionQueriesExample(scene, pass);

        Window w = new Window(scene, 512, 512, true);
        w.setEffect(effect);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
        
    }
    
}
