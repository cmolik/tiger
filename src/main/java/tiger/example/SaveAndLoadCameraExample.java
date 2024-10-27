/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.example;

import gleem.ExaminerViewer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.Properties;
import com.jogamp.opengl.GL;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;
import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.CameraProperties;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Window;

/**
 *
 * @author cmolikl
 */
public class SaveAndLoadCameraExample {
    
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

        Window w = new Window(scene, 512, 512);
        SaveAndLoadCameraExample ex = new SaveAndLoadCameraExample();
        ex.createUI(w);
        w.setEffect(pass);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
    }
    
    private Properties cameraProperties = new Properties();
    
    public void createUI(final Window w) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e) {
            System.err.println("The atempt to set system Look&Feel failed. Continuing with default.");
        }

        JPanel params = w.params;
        
        JButton saveButton = new JButton("Save camera");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saving camera");
                ExaminerViewer camera = w.getControler();
                CameraProperties.saveProperties(camera, cameraProperties);
            }
        });
        JButton loadButton = new JButton("Load camera");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Loading camera");
                ExaminerViewer viewer = w.getControler();
                CameraProperties.loadProperties(viewer, cameraProperties);
            }
        });
        
        params.add(saveButton);
        params.add(loadButton);
    }
}
