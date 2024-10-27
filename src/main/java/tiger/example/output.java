package tiger.example;

import scene.Scene;
import javax.imageio.ImageIO;
import mesh.loaders.ObjLoader;
import java.io.*;
import com.jogamp.opengl.GL;
import java.awt.image.BufferedImage;
import tiger.core.*;

public class output {

    public static void main(String[] args) {
        //FrameBuffer.depthBufferType = GL.GL_DEPTH_COMPONENT;
        Effect e = new Effect();
//-------------------------------EFFECT----------------------------;
        Texture2D Texture0 = new Texture2D();
        try {
            BufferedImage image = ImageIO.read(new File("C:/Users/cmolikl/Projects/New Project 0/Pics/7109222-chess-or-checkers-background-texture-in-black-and-white.png"));
            Texture0.loadData(image);
            e.addTexture(Texture0);
        } catch (IOException ex0) {
            System.out.println(ex0);
        }
        Texture2D RenderableTexture0 = new Texture2D(/*GL.GL_RGBA8, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE*/);
        e.addTexture(RenderableTexture0);
//--------------------------------PASS+(0)-------------------------------;
        RenderState rs0 = new RenderState();
        rs0.setClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        rs0.clearBuffers(true);
        rs0.enable(GL.GL_DEPTH_TEST);
        InputStream vertexStream0 = null;
        try {
            vertexStream0 = new FileInputStream("C:/Users/cmolikl/Projects/New Project 0/Shaders/New(0).vert");
        } catch (Exception e1) {
        }
        InputStream fragmentStream0 = null;
        try {
            fragmentStream0 = new FileInputStream("C:/Users/cmolikl/Projects/New Project 0/Shaders/New(0).frag");
        } catch (Exception e2) {
        }
        Pass pass0 = new Pass(vertexStream0, fragmentStream0);
        pass0.addTexture(Texture0, "Texture0");
        ObjLoader loader0 = new ObjLoader();
        Scene scene0 = null;
        try {
            scene0 = loader0.loadFile("C:/Users/cmolikl/Projects/New Project 0/Objs/KRYCHLE2.obj");
        } catch (Exception e3) {
        }
        FrameBuffer fbo0 = new FrameBuffer(true, RenderableTexture0);
        e.addTarget(fbo0);
        pass0.setTarget(fbo0);
        pass0.scene = scene0;
        pass0.renderState = rs0;
        e.addGLEventListener(pass0);
//--------------------------------PASS+(1)-------------------------------;
        RenderState rs1 = new RenderState();
        rs1.setClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        rs1.clearBuffers(true);
        rs1.enable(GL.GL_DEPTH_TEST);

        InputStream vertexStream1 = null;
        try {
            vertexStream1 = new FileInputStream("C:/Users/cmolikl/Projects/New Project 0/Shaders/New(1).vert");
        } catch (Exception e4) {
        }
        InputStream fragmentStream1 = null;
        try {
            fragmentStream1 = new FileInputStream("C:/Users/cmolikl/Projects/New Project 0/Shaders/New(1).frag");
        } catch (Exception e5) {
        }

        Pass pass1 = new Pass(vertexStream1, fragmentStream1);
        ObjLoader loader1 = new ObjLoader();
        Scene scene1 = null;
        try {
            scene1 = loader1.loadFile("C:/Users/cmolikl/Projects/New Project 0/Objs/plane.obj");
        } catch (Exception e6) {
        }
        pass1.addTexture(RenderableTexture0, "RenderableTexture0");
        pass1.scene = scene1;
        pass1.renderState = rs1;
        e.addGLEventListener(pass1);

        ObjLoader loader2 = new ObjLoader();
        Window w = null;
        try {
            w = new Window(scene0, 512, 512);
        } catch (Exception e7) {
        }
        w.setEffect(e);
        w.start();
    }
}
