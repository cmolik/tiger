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
import tiger.ui.TigerChangeEvent;

/**
 *
 * @author cmolikl
 */
public class CameraExample {

    public static void main(String[] args) {

        URL objUrl = ClassLoader.getSystemResource("tiger/example/cow_triangles.obj");
        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile(objUrl);
        for(Mesh m : scene.getAllMeshes()) {
            m.renderMethod = Mesh.VERTEX_BUFFER;
        }
        
        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.setClearColor(1f, 1f, 1f, 1f);
        rs.enable(GL.GL_DEPTH_TEST);

        InputStream vertexStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong2.vert");
        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        Pass pass = new Pass(vertexStream, fragmentStream);
        pass.scene = scene;
        pass.renderState = rs;

        Window w = new Window(scene, 512, 512);
        w.interaction = false;
        
//        PerspectiveCamera camera = new PerspectiveCamera(pass, scene);
//        camera.addChangeListener((ChangeEvent e) -> {w.canvas.repaint();});
//        w.canvas.addMouseWheelListener(camera.new CameraZoomListener());
//        PerspectiveCamera.CameraOrbitListener l = camera.new CameraOrbitListener();
//        w.canvas.addMouseListener(l);
//        w.canvas.addMouseMotionListener(l);

        OrthogonalCamera camera = new OrthogonalCamera(pass, scene);
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
