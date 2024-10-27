/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import com.jogamp.opengl.GL;
import javax.swing.*;
import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.*;
import tiger.ui.FloatSlider;
import tiger.ui.TigerChangeEvent;
import tiger.ui.TigerChangeListener;

/**
 *
 * @author cmolikl
 */
public class CameraInterpolationExample {

    private OrthogonalCamera camera;
    private OrthogonalCamera camera1;
    private OrthogonalCamera camera2;

    public CameraInterpolationExample(OrthogonalCamera camera) {
        this.camera = camera;
    }

    public void createUI(final Window w, GlslProgramFloatParameter t) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("The atempt to set system Look&Feel failed. Continuing with default.");
        }

        JPanel params = w.params;

        JButton saveButton1 = new JButton("Save camera 1");
        saveButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camera1 == null) {
                    camera1 = new OrthogonalCamera(camera);
                } else {
                    camera1.set(camera);
                }
            }
        });
        JButton loadButton1 = new JButton("Load camera 1");
        loadButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.set(camera1);
            }
        });

        JButton saveButton2 = new JButton("Save camera 2");
        saveButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (camera2 == null) {
                    camera2 = new OrthogonalCamera(camera);
                } else {
                    camera2.set(camera);
                }
            }
        });
        JButton loadButton2 = new JButton("Load camera 2");
        loadButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                camera.set(camera2);
            }
        });

        if(t == null) {
            t = new GlslProgramFloatParameter("t", 1f);
        }
        GlslProgramFloatParameter finalT = t;
        t.addChangeListener(new TigerChangeListener() {
            @Override
            public void stateChanged(TigerChangeEvent changeEvent) {
                camera.interpolate(camera1, camera2, finalT.getValue());
            }
        });
        FloatSlider iSlider = new FloatSlider(t, false, JSlider.HORIZONTAL, 0f, 1f);

        params.add(saveButton1);
        params.add(loadButton1);
        params.add(saveButton2);
        params.add(loadButton2);
        params.add(iSlider);
    }

    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("D:/Projects/Data/cow_triangles.obj");
        for (Mesh m : scene.getAllMeshes()) {
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
        w.interaction = false;
        OrthogonalCamera camera = new OrthogonalCamera(pass, scene);
        camera.addChangeListener((TigerChangeEvent e) -> {w.canvas.repaint();});


        w.canvas.addMouseWheelListener(camera.new CameraZoomListener());
        OrthogonalCamera.CameraOrbitListener l = camera.new CameraOrbitListener();
        w.canvas.addMouseListener(l);
        w.canvas.addMouseMotionListener(l);
        OrthogonalCamera.CameraPanListener p = camera.new CameraPanListener();
        w.canvas.addMouseListener(p);
        w.canvas.addMouseMotionListener(p);

        CameraInterpolationExample ex = new CameraInterpolationExample(camera);
        ex.createUI(w, null);
        w.setEffect(camera);
        w.runFastAsPosible = true;
        w.printFps = false;
        w.start();
    }
}
