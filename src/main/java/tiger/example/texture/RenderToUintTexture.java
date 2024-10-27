/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.example.texture;

import java.io.InputStream;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import mesh.loaders.ObjLoader;
import scene.Scene;
import scene.surface.mesh.Mesh;
import tiger.core.Effect;
import tiger.core.FrameBuffer;
import tiger.core.GlslProgramUIntParameter;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class RenderToUintTexture implements GLEventListener {
    
    Scene<Mesh> scene;
    FrameBuffer buffer;
    Pass pass;
    
    GlslProgramUIntParameter bit = new GlslProgramUIntParameter("bit", 0);
    GlslProgramUIntParameter index = new GlslProgramUIntParameter("index", 0);
    
    public RenderToUintTexture(Scene<Mesh> scene, FrameBuffer buffer) {
        this.scene = scene;
        this.buffer = buffer;
        
        RenderState rs = new RenderState();
        rs.enable(GL.GL_DEPTH_TEST);
        rs.disable(GL.GL_BLEND);
        //rs.enable(GL.GL_COLOR_LOGIC_OP);
        //rs.setLogicOp(GL.GL_OR);

        InputStream vertex = ClassLoader.getSystemResourceAsStream("tiger/example/texture/projection.vert");
        InputStream fragment = ClassLoader.getSystemResourceAsStream("tiger/example/texture/projection.frag");
        pass = new Pass(vertex, fragment);
        pass.scene = scene;
        pass.renderState = rs;
        pass.setTarget(buffer);
    }

    @Override
    public void init(GLAutoDrawable glad) {
        pass.init(glad);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL3();
        
        buffer.bind(gl);
        gl.glDrawBuffer(GL3.GL_COLOR_ATTACHMENT0);
        buffer.checkStatus(gl);
        int[] clearColor = {0, 0, 0, 0};
        gl.glClearBufferuiv(GL3.GL_COLOR, 0, clearColor, 0);
        
        gl.glClearDepthf(1f);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
        
        pass.prepare(glad);
        for(Mesh mesh : scene.getAllMeshes()) {
            bit.setValue(mesh.id % 32);
            index.setValue(mesh.id / 32);
            bit.init(pass.glslProgram);
            index.init(pass.glslProgram);

            mesh.getRenderer().render(glad, mesh);
        }
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

        Texture2D texture = new Texture2D(GL2.GL_RGBA32UI, GL2.GL_RGBA_INTEGER, GL.GL_UNSIGNED_INT);
        texture.setParameter(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
	texture.setParameter(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        FrameBuffer buffer = new FrameBuffer(true, texture);
        //buffer.depthBufferType = GL3.GL_DEPTH_COMPONENT;
        
        RenderToUintTexture pass = new RenderToUintTexture(scene, buffer);
        
        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.disable(GL.GL_DEPTH_TEST);
        rs.disable(GL.GL_BLEND);

        InputStream fragment = ClassLoader.getSystemResourceAsStream("tiger/example/texture/inttofloat.frag");
        Saq saq = new Saq(fragment, texture);
        saq.renderState = rs;
        
        Effect effect = new Effect();
        effect.addTexture(texture);
        effect.addTarget(buffer);
        effect.addGLEventListener(pass);
        effect.addGLEventListener(saq);

        Window w = new Window(scene, 512, 512, true);
        w.setEffect(effect);
        w.runFastAsPosible = true;
        w.printFps = true;
        w.start();
        
    }
    
}
