/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example;

import java.io.InputStream;
import java.net.URL;

import com.jogamp.opengl.GL;

import scene.Scene;
import mesh.loaders.ObjLoader;
import scene.surface.mesh.Mesh;
import tiger.core.*;
import tiger.ui.swt.SwtWindow;

/**
 *
 * @author cmolikl
 */
public class Example {

    public static void main(String[] args) {

        URL objUrl = ClassLoader.getSystemResource("tiger/example/cow_triangles.obj");
        
        ObjLoader loader = new ObjLoader();
        //Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/tiger2-maven/data/cow_triangles.obj");
        Scene<Mesh> scene = loader.loadFile(objUrl);
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
        
        OrthogonalCamera camera = new OrthogonalCamera(pass, scene);

        Window w = new Window(scene, 512, 512);
        w.setEffect(pass);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();

        // SwtWindow w = new SwtWindow("Example 1", 512, 512, camera);
        // w.canvas.glCanvas.addMouseWheelListener(camera.new SwtCameraZoomListener());
        // OrthogonalCamera.SwtCameraOrbitListener l = camera.new SwtCameraOrbitListener();
        // w.canvas.glCanvas.addMouseListener(l);
        // w.canvas.glCanvas.addMouseMoveListener(l);
        // camera.addChangeListener(w.redrawListener);
        // w.start();

    }
}
