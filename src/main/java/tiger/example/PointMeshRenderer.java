/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.example;

import gleem.linalg.Vec3f;
import java.util.Iterator;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.vecmath.Point4d;
import javax.vecmath.Vector4d;
import scene.surface.Appearance;
import scene.surface.mesh.Mesh;
import scene.surface.mesh.MeshRenderer;
import scene.surface.mesh.Vertex;

/**
 *
 * @author cmolikl
 */
public class PointMeshRenderer extends MeshRenderer {
    @Override
    public void render(GLAutoDrawable drawable, Mesh mesh) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glPointSize(10f);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glUseProgram(0);
        //super.renderMesh(drawable, mesh);
        initCallList(gl, mesh);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPointSize(1f);
    }

    @Override
    protected void initCallList(GL2 gl, Mesh mesh) {
        //System.out.println("Initializing mesh " + name);

        //mesh.callList = gl.glGenLists(1);
        Appearance a = mesh.appearance != null ? mesh.appearance : new Appearance(null);
        Vec3f d = a.getDiffuse();

        //gl.glNewList(mesh.callList, GL2.GL_COMPILE);
        int drawtype = GL.GL_POINTS;
        gl.glBegin(drawtype);

        Iterator<Vertex> iterf = mesh.getGeometry().getVertexList().iterator();
        while (iterf.hasNext()) {
            Vertex v = iterf.next();
            
            /*Vector3d n = v.getNormal();
            if(n != null) {
                gl.glNormal3d(n.getX(), n.getY(), n.getZ());
            }
            Vector4d tc = v.getTexcoords();
            if (tc != null) {
                gl.glTexCoord1d(tc.getX());
                gl.glTexCoord2d(tc.getX(), tc.getY());
                gl.glTexCoord3d(tc.getX(), tc.getY(), tc.getZ());
                gl.glTexCoord4d(tc.getX(), tc.getY(), tc.getZ(), tc.getW());
            }*/
            Vector4d c = v.getColor();
            if(c != null) {
                gl.glColor4d(c.x, c.y, c.z, c.w);
            }
            else {
                gl.glColor4d(d.x(), d.y(), d.z(), 1.0);
            }
            Point4d loc = v.getLocation();
            gl.glVertex3d(loc.getX(), loc.getY(), loc.getZ());
        }
        gl.glEnd();

        //gl.glEndList();
    }
}

