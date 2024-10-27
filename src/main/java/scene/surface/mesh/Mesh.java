/**
 *
 */
package scene.surface.mesh;

import scene.surface.Appearance;
import gleem.linalg.Vec3f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import java.util.Set;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import mesh.collisions.Box;
import mesh.collisions.Sphere;
import scene.surface.Surface;
import scene.surface.SurfaceRenderer;
import tiger.core.VertexBuffer;

/**
 * @author David Cornette
 *
 */
public class Mesh extends Surface<Mesh> {
    public static final int CALL_LIST = 0;
    public static final int VERTEX_BUFFER = 1;

    public MeshGeometry geometry;
    public Sphere boundingSphere;
    public Box boundingBox;
    public Appearance appearance;

    public int renderMethod = CALL_LIST;
    public boolean smoothShading = true;

    public int callList = 0;

    boolean modified = false;
    public VertexBuffer vertices;
    public VertexBuffer normals;
    public VertexBuffer indices;

    public Map<Integer, VertexBuffer> attributes = new HashMap<Integer, VertexBuffer>();

    public Mesh parent;
    public HashSet<Mesh> children = new HashSet<Mesh>();

    public Mesh() {
        setRenderer(MeshRenderer.getRenderer());
    }

    public Mesh getDeepCopy() {
        Mesh copy = new Mesh();
        copy.geometry = this.geometry.getDeepCopy();
        copy.appearance = this.appearance;
        return copy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addChild(Mesh child) {
        child.parent = this;
        children.add(child);
    }

    public Set<Mesh> getChildren() {
        return children;
    }

    public boolean isChild(Mesh mesh) {
        return children.contains(mesh);
    }

    public void addAttribute(int location, VertexBuffer buffer) {
        attributes.put(location, buffer);
    }

    public VertexBuffer getAttribute(int location) {
        return attributes.get(location);
    }

    public Set<Integer> getAttributeLocations() {
        return attributes.keySet();
    }

    public MeshGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(MeshGeometry geometry) {
        this.geometry = geometry;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public void setAppearance(Appearance appearance) {
        this.appearance = appearance;
    }

    public void markModified() {
        modified = true;
    }

    public boolean isModified() {
        return modified;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Mesh createUnitCube() {

        Mesh mesh = new Mesh();
        mesh.name = "cube";

        Vertex v0 = new Vertex(-0.5, -0.5, -0.5);
        Vertex v1 = new Vertex(0.5, -0.5, -0.5);
        Vertex v2 = new Vertex(-0.5, 0.5, -0.5);
        Vertex v3 = new Vertex(-0.5, -0.5, 0.5);
        Vertex v4 = new Vertex(0.5, 0.5, -0.5);
        Vertex v5 = new Vertex(0.5, -0.5, 0.5);
        Vertex v6 = new Vertex(-0.5, 0.5, 0.5);
        Vertex v7 = new Vertex(0.5, 0.5, 0.5);

        v0.setTexCoords(0.0, 0.0, 0.0);
        v1.setTexCoords(1.0, 0.0, 0.0);
        v2.setTexCoords(0.0, 1.0, 0.0);
        v3.setTexCoords(0.0, 0.0, 1.0);
        v4.setTexCoords(1.0, 1.0, 0.0);
        v5.setTexCoords(1.0, 0.0, 1.0);
        v6.setTexCoords(0.0, 1.0, 1.0);
        v7.setTexCoords(1.0, 1.0, 1.0);

        Face f0 = new Face(v0, v2, v4, v1);
        Face f1 = new Face(v3, v5, v7, v6);
        Face f2 = new Face(v5, v1, v4, v7);
        Face f3 = new Face(v0, v3, v6, v2);
        Face f4 = new Face(v6, v7, v4, v2);
        Face f5 = new Face(v3, v5, v1, v0);

        MeshGeometry geometry = new MeshGeometry();
        geometry.add(v0);
        geometry.add(v1);
        geometry.add(v2);
        geometry.add(v3);
        geometry.add(v4);
        geometry.add(v5);
        geometry.add(v6);
        geometry.add(v7);

        geometry.add(f0);
        geometry.add(f1);
        geometry.add(f2);
        geometry.add(f3);
        geometry.add(f4);
        geometry.add(f5);

        mesh.geometry = geometry;

        return mesh;
    }
}
