/*
 * gleem -- OpenGL Extremely Easy-To-Use Manipulators.
 * Copyright (C) 1998-2003 Kenneth B. Russell (kbrussel@alum.mit.edu)
 *
 * Copying, distribution and use of this software in source and binary
 * forms, with or without modification, is permitted provided that the
 * following conditions are met:
 *
 * Distributions of source code must reproduce the copyright notice,
 * this list of conditions and the following disclaimer in the source
 * code header files; and Distributions of binary code must reproduce
 * the copyright notice, this list of conditions and the following
 * disclaimer in the documentation, Read me file, license file and/or
 * other materials provided with the software distribution.
 *
 * The names of Sun Microsystems, Inc. ("Sun") and/or the copyright
 * holder may not be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," WITHOUT A WARRANTY OF ANY
 * KIND. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, NON-INTERFERENCE, ACCURACY OF
 * INFORMATIONAL CONTENT OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. THE
 * COPYRIGHT HOLDER, SUN AND SUN'S LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL THE
 * COPYRIGHT HOLDER, SUN OR SUN'S LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES. YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT
 * DESIGNED, LICENSED OR INTENDED FOR USE IN THE DESIGN, CONSTRUCTION,
 * OPERATION OR MAINTENANCE OF ANY NUCLEAR FACILITY. THE COPYRIGHT
 * HOLDER, SUN AND SUN'S LICENSORS DISCLAIM ANY EXPRESS OR IMPLIED
 * WARRANTY OF FITNESS FOR SUCH USES.
 */
package gleem;

import gleem.linalg.Vec3f;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import javax.vecmath.Point4d;
import scene.surface.mesh.Mesh;
import scene.Scene;
import scene.surface.mesh.Vertex;
import mesh.loaders.ObjLoader;
import scene.surface.Surface;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Window;


/** Tests the Translate2 Manip. */
public class TestTranslate2 extends Pass implements BSphereProvider, KeyListener {

    private ExaminerViewer viewer;
    private CameraParameters cameraParameters;
    private Mesh mesh;
    private BSphere bsphere = new BSphere(new Vec3f(0,0,0), 10);
    private boolean rotate = true;

    ArrayList<Translate2Manip> manip;

    public BSphere getBoundingSphere() {
        return bsphere;
    }

    public TestTranslate2(InputStream v, InputStream f) {
        super(v, f);
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        super.init(drawable);
        GL gl = drawable.getGL();

        // Register the window with the ManipManager
        ManipManager manager = ManipManager.getManipManager();
        manager.registerWindow(drawable);

        // Instantiate a Translate2Manip
        manip = new ArrayList<Translate2Manip>();
        HashSet<Point4d> coords = new HashSet<Point4d>();
        for(Vertex v : mesh.getGeometry().getVertexList()) {
            if(!coords.contains(v.getLocation())) {
                Translate2Manip m = new Translate2Manip(v);
                //m.setTranslation(new Vec3f(v.getLocation()));
                m.setScale(new Vec3f(0.1f, 0.1f, 0.1f));
                manager.showManipInWindow(m, drawable);
                manip.add(m);
                coords.add(v.getLocation());
            }
        }

        viewer = new ExaminerViewer(MouseButtonHelper.numMouseButtons());
        viewer.attach(drawable, this);
        viewer.viewAll(gl);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        viewer.update(gl);
        cameraParameters = viewer.getCameraParameters();

        super.display(drawable);
        
        Vec3f dir = cameraParameters.getForwardDirection();
        for(Translate2Manip m : manip) {
            m.setNormal(dir);
        }
        ManipManager.getManipManager().updateCameraParameters(drawable, cameraParameters);
        ManipManager.getManipManager().render(drawable, gl);
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {

        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/Cube.obj");
        for(Mesh mesh : scene.getAllMeshes()) {
            mesh.renderMethod = Mesh.VERTEX_BUFFER;
        }

        RenderState rs = new RenderState();
        rs.enable(GL.GL_DEPTH_TEST);
        rs.setClearColor(0, 0, 0, 0);
        rs.clearBuffers(true);
        rs.setBuffersToClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        InputStream vertex = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.vert");
        InputStream fragment = ClassLoader.getSystemResourceAsStream("tiger/example/Phong.frag");
        TestTranslate2 test = new TestTranslate2(vertex, fragment);
        test.setMesh(scene.getMesh("Cube"));
        test.scene = scene;
        test.renderState = rs;
        
        Window w = new Window(600, 600);
        w.setEffect(test);
        w.setKeyListener(test);
        w.runFastAsPosible = true;
        w.start();
    }
}
