/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface.mesh;

import com.jogamp.common.nio.Buffers;
import gleem.linalg.Vec3f;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import scene.surface.Appearance;
import scene.surface.SurfaceRenderer;
import tiger.core.VertexBuffer;
import tiger.core.VertexBuffer.DataType;

/**
 *
 * @author cmolikl
 */
public class MeshRenderer implements SurfaceRenderer<Mesh> {

    private final static MeshRenderer renderer = new MeshRenderer();

    protected boolean displayNormals = false;

    protected MeshRenderer() {}

    public static SurfaceRenderer<Mesh> getRenderer() {
        return renderer;
    }
    
    public void init(GLAutoDrawable drawable, Mesh mesh) {
        
    }

    public void render(GLAutoDrawable drawable, Mesh mesh) {
        if(!mesh.getVisibility()) return;
        if(mesh.geometry == null) return;
        GL2 gl = drawable.getGL().getGL2();

        if(mesh.renderMethod == Mesh.CALL_LIST) {
            if (mesh.callList == 0) {
                initCallList(gl, mesh);
            }
        }
        else {
            if(mesh.isModified() || mesh.indices == null || mesh.indices.getGlNumber() == 0) {
                initVertexBuffer(gl, mesh);
            }
        }

        gl.glPushMatrix();

        Appearance a = mesh.appearance != null ? mesh.appearance : new Appearance(null);
        Vec3f tmp = a.getAmbient();
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{tmp.x(), tmp.y(), tmp.z(), 1.0f}, 0);
        tmp = a.getDiffuse();
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[]{tmp.x(), tmp.y(), tmp.z(), 1.0f}, 0);
        gl.glColor4d(tmp.x(), tmp.y(), tmp.z(), 1.0);
        if (a.getModel() == Appearance.SPECULAR_MODEL) {
            tmp = a.getSpecular();
            gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{tmp.x(), tmp.y(), tmp.z(), 1.0f}, 0);
            if(a.getShines() > 100) {
                gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 100);
            }
            else {
                gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, a.getShines());
            }
        }

        if(mesh.renderMethod == Mesh.CALL_LIST) {
            gl.glCallList(mesh.callList);
        }
        else {
            mesh.indices.bind(gl);
            mesh.vertices.enable(gl, 0, false, 0);
            mesh.normals.enable(gl, 1, true, 0);
            for(int location : mesh.getAttributeLocations()) {
                VertexBuffer buffer = mesh.getAttribute(location);
                buffer.enable(gl, location, false, 0);
            }

            // Draw the elements
            gl.glDrawElements(
                GL.GL_TRIANGLES,          // mode
                mesh.geometry.numberOfFaces() * 3,  // count
                GL.GL_UNSIGNED_INT,     // type
                0                      // element array buffer offset
            );

            // unbind buffers
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
            gl.glDisableVertexAttribArray(0);
            gl.glDisableVertexAttribArray(1);
            for(int location : mesh.getAttributeLocations()) {
               gl.glDisableVertexAttribArray(location);
            }
        }

        gl.glPopMatrix();
    }

    protected void initVertexBuffer(GL gl, Mesh mesh) {
        int numberOfVertices = mesh.geometry.numberOfVertices();
        int numberOfCoords = 3*numberOfVertices;
        int numberOfFaces = mesh.geometry.numberOfFaces();
        int numberOfIds = 3*numberOfFaces;

        FloatBuffer points = ByteBuffer.allocateDirect(numberOfCoords*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer normals = ByteBuffer.allocateDirect(numberOfCoords*Buffers.SIZEOF_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

        int count = 0;
        int index = 0;
        for(Vertex v : mesh.geometry.getVertexList()) {
            points.put((float) v.getLocation().x);
            points.put((float) v.getLocation().y);
            points.put((float) v.getLocation().z);

            normals.put((float) v.getNormal().x);
            normals.put((float) v.getNormal().y);
            normals.put((float) v.getNormal().z);

            v.setIndex(index);

            count += 3;
            index++;
        }
        points.rewind();
        normals.rewind();

        IntBuffer ids = ByteBuffer.allocateDirect(numberOfIds*Buffers.SIZEOF_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
        count = 0;
        for(Face f : mesh.geometry.getFaceList()) {
            Vertex[] v = f.getVerts();
            for(int i = 0; i < 3;i++) {
                ids.put(v[i].getIndex());
                count++;
            }
        }
        ids.rewind();

        if(mesh.vertices == null) {
            mesh.vertices = new VertexBuffer(GL.GL_ARRAY_BUFFER, DataType.FLOAT, 3, 0, points);
            mesh.vertices.init(gl);
        }
        else {
            mesh.vertices.update(gl, points);
        }

        if(mesh.normals == null) {
            mesh.normals = new VertexBuffer(GL.GL_ARRAY_BUFFER, DataType.FLOAT, 3, 0, normals);
            mesh.normals.init(gl);
        }

        for(int location : mesh.getAttributeLocations()) {
            VertexBuffer buffer  = mesh.getAttribute(location);
            if(buffer.getGlNumber() == 0) {
                buffer.init(gl);
            }
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);

        if(mesh.indices == null) {
            mesh.indices = new VertexBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, DataType.INT, 3, 0, ids);
            mesh.indices.init(gl);
        }

        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, 0);


    }

    protected void initCallList(GL2 gl, Mesh mesh) {
        //System.out.println("Initializing mesh " + name);

        mesh.callList = gl.glGenLists(1);
        Appearance a = mesh.appearance != null ? mesh.appearance : new Appearance(null);
        Vec3f d = a.getDiffuse();

        gl.glNewList(mesh.callList, GL2.GL_COMPILE);

        for(Face f : mesh.getGeometry().getFaceList()) {
            Vertex[] verts;
            if(mesh.smoothShading) {
                verts = f.getVerts();
            }
            else {
                verts = f.getFlatShadingVerts();
            }
            int drawtype = GL.GL_TRIANGLES;
            if (verts.length == 4) {
                drawtype = GL2.GL_QUADS;
            } else if (verts.length == 6) {
                drawtype = GL2.GL_TRIANGLES_ADJACENCY_EXT;
                for (int i = 0; i < verts.length; i++) {
                    Vertex v = verts[i];
                    if (v == null) {
                        verts[i] = verts[(i + 3)%6];
                    }
                }
            }
            gl.glBegin(drawtype);
            for (int i = 0; i < verts.length; i++) {
                Vertex v = verts[i];
                Vector3d n = v.getNormal();
                if(n != null) {
                    gl.glNormal3d(n.getX(), n.getY(), n.getZ());
                }
                Vector4d tc = v.getTexcoords();
                if (tc != null) {
                    //gl.glTexCoord1d(tc.getX());
                    //gl.glTexCoord2d(tc.getX(), tc.getY());
                    //gl.glTexCoord3d(tc.getX(), tc.getY(), tc.getZ());
                    gl.glTexCoord4d(tc.getX(), tc.getY(), tc.getZ(), tc.getW());
                }
                Vector4d c = v.getColor();
                if(c != null) {
                    gl.glColor4d(c.x, c.y, c.z, c.w);
                }
                else {
                    gl.glColor4d(d.x(), d.y(), d.z(), a.getAlpha());
                }
                Point4d loc = v.getLocation();
                gl.glVertex3d(loc.getX(), loc.getY(), loc.getZ());
            }
            gl.glEnd();
        }

        if(displayNormals) {
            ArrayList<Vertex> verts = mesh.getGeometry().getVertexList();
            gl.glBegin(GL.GL_LINES);
            gl.glColor3f(1f, 0f, 0f);
            for (Vertex v : verts) {
                    Vector3d n = v.getNormal();
                    if(n != null) {
                        Point4d loc = v.getLocation();
                        gl.glVertex3d(loc.getX(), loc.getY(), loc.getZ());
                        gl.glVertex3d(loc.getX() + n.getX(), loc.getY() + n.getY(), loc.getZ() + n.getZ());
                    }
            }
            gl.glEnd();
        }

        gl.glEndList();
    }

}
