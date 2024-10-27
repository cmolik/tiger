/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface.mesh;

import scene.Scene;
import gleem.BSphere;
import gleem.linalg.Vec3f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import mesh.collisions.Box;
import mesh.collisions.Sphere;
import mesh.loaders.ObjLoader;
import quickhull3d.Point3d;
import quickhull3d.QuickHull3D;

/**
 *
 * @author cmolikl
 */
public class MeshUtils {
    
    private static int hash(int index1, int index2, int size) {
        return size*Math.min(index1, index2) + Math.max(index1, index2);
    }
    
    private static void insertVertex(Vertex v1, Vertex v2, Vertex av, Face f) {
        Vertex[] verts6 = f.getVerts();
        if(v2.index == verts6[0].index && v1.index == verts6[2].index) {
            verts6[1] = av;
        }
        else if(v2.index == verts6[2].index && v1.index == verts6[4].index) {
            verts6[3] = av;
        }
        else if(v2.index == verts6[4].index && v1.index == verts6[0].index) {
            verts6[5] = av;
        }
    }
    
    public static void calculateAdjacency(MeshGeometry geom) {
        HashMap<Integer, Vertex> vertexAdjacency = new HashMap<Integer, Vertex>();
        HashMap<Integer, Face> faceAdjacency = new HashMap<Integer, Face>();
        ArrayList<Face> faces = geom.getFaceList();
        for(int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            Vertex[] verts = face.getVerts();
            Vertex[] verts6 = null;
            if(verts.length == 4) {
                Vertex[] nfVerts = new Vertex[] {verts[2], verts[3], verts[0]};
                faces.add(new Face(nfVerts));
                verts6 = new Vertex[] {verts[0], null, verts[1], null, verts[2], null};
            }
            else {
                verts6 = new Vertex[] {verts[0], null, verts[1], null, verts[2], null};
            }
            face.setVerts(verts6);
            int size = geom.getVertexList().size();
            int key = hash(verts[0].index, verts[1].index, size);
            Face value = faceAdjacency.get(key);
            if(value == null) {
                vertexAdjacency.put(key, verts[2]);
                faceAdjacency.put(key, face);
            }
            else {
                Vertex av = vertexAdjacency.get(key);
                verts6[1] = av;
                insertVertex(verts[0], verts[1], verts[2], value);
            }
            key = hash(verts[1].index, verts[2].index, size);
            value = faceAdjacency.get(key);
            if(value == null) {
                vertexAdjacency.put(key, verts[0]);
                faceAdjacency.put(key, face);
            }
            else {
                Vertex av = vertexAdjacency.get(key);
                verts6[3] = av;
                insertVertex(verts[1], verts[2], verts[0], value);
            }
            key = hash(verts[2].index, verts[0].index, size);
            value = faceAdjacency.get(key);
            if(value == null) {
                vertexAdjacency.put(key, verts[1]);
                faceAdjacency.put(key, face);
            }
            else {
                Vertex av = vertexAdjacency.get(key);
                verts6[5] = av;
                insertVertex(verts[2], verts[0], verts[1], value);
            }
        }
    }

    public static Map<Vertex, Set<Vertex>> calculateVertexAdjacency(MeshGeometry geom) {
        Set<Integer> edges = new HashSet<Integer>();
        Map<Vertex, Set<Vertex>> vertexAdjacency = new HashMap<Vertex, Set<Vertex>>();
        ArrayList<Face> faces = geom.getFaceList();
        int size = geom.getVertexList().size();
        for(int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);
            Vertex[] verts = face.getVerts();
            testAdjacency(edges, vertexAdjacency, verts[0], verts[1], size);
            testAdjacency(edges, vertexAdjacency, verts[1], verts[2], size);
            testAdjacency(edges, vertexAdjacency, verts[2], verts[0], size);
        }
        return vertexAdjacency;
    }

    private static void testAdjacency(Set<Integer> edges, Map<Vertex, Set<Vertex>> vertexAdjacency, Vertex v1, Vertex v2, int size) {
        int key = hash(v1.index, v2.index, size);
        if(!edges.contains(key)) {
            edges.add(key);

            Set<Vertex> neighbours = vertexAdjacency.get(v1);
            if(neighbours == null) {
                neighbours = new HashSet<Vertex>();
                vertexAdjacency.put(v1, neighbours);
            }
            neighbours.add(v2);
            
            neighbours = vertexAdjacency.get(v2);
            if(neighbours == null) {
                neighbours = new HashSet<Vertex>();
                vertexAdjacency.put(v2, neighbours);
            }
            neighbours.add(v1);
        }
    }
    
    public static void calculateBoundingShapes(Mesh mesh) {
        Point4d min = new Point4d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0); 
        Point4d max = new Point4d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0);
        for(Vertex v : mesh.getGeometry().getVertexList()) {
            min.x = Math.min(min.x, v.getLocation().x);
            max.x = Math.max(max.x, v.getLocation().x);
            min.y = Math.min(min.y, v.getLocation().y);
            max.y = Math.max(max.y, v.getLocation().y);
            min.z = Math.min(min.z, v.getLocation().z);
            max.z = Math.max(max.z, v.getLocation().z);
        }
        mesh.boundingBox = new Box(min, max);
        Point4d center = (Point4d) min.clone();
        center.interpolate(max, 0.5);
        center.w = 1.0;
        double r = -1.0;
        for(Vertex v : mesh.getGeometry().getVertexList()) {
            r = Math.max(r, center.distance(v.getLocation()));
        }
        //double r = center.distance(max);
        mesh.boundingSphere = new Sphere(center, r);
    }

    public static BSphere getBoundingSphere(Mesh mesh) {
        Point4d min = new Point4d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0);
        Point4d max = new Point4d(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0);
        for(Vertex v : mesh.getGeometry().getVertexList()) {
            min.x = Math.min(min.x, v.getLocation().x);
            max.x = Math.max(max.x, v.getLocation().x);
            min.y = Math.min(min.y, v.getLocation().y);
            max.y = Math.max(max.y, v.getLocation().y);
            min.z = Math.min(min.z, v.getLocation().z);
            max.z = Math.max(max.z, v.getLocation().z);
        }
        Point4d center = (Point4d) min.clone();
        center.interpolate(max, 0.5);
        center.w = 1.0;
        double r = -1.0;
        for(Vertex v : mesh.getGeometry().getVertexList()) {
            r = Math.max(r, center.distance(v.getLocation()));
        }

        Vec3f c = new Vec3f((float) center.x, (float) center.y, (float) center.z);
        return new BSphere(c, (float) r);
    }
    
    public static Mesh calculateConvexHull(Collection<Mesh> meshes, boolean triangulate) {
        ArrayList<Vertex> verts = new ArrayList<Vertex>();
        ArrayList<Point3d> points = new ArrayList<Point3d>();
        for (Mesh mesh : meshes) {
            if(mesh.geometry != null) {
                for (Vertex v : mesh.getGeometry().getVertexList()) {
                    verts.add(v);
                    Point4d p = v.getLocation();
                    points.add(new Point3d(p.x, p.y, p.z));
                }
            }
        }
        
        MeshGeometry geometry = new MeshGeometry();
        
        if(points.size() >= 4) {
            QuickHull3D hull = new QuickHull3D();
            hull.build(points.toArray(new Point3d[] {}));
            if(triangulate) {
                hull.triangulate();
            }


            int[] indices = hull.getVertexPointIndices();
            for (int i = 0; i < indices.length; i++) {
                int index = indices[i];
                Vertex v = verts.get(index);
                geometry.add(v);
            }

            int[][] faceIndices = hull.getFaces();
            for (int i = 0; i < faceIndices.length; i++) {
                Vertex[] faceVerts = new Vertex[faceIndices[i].length];
                for (int k = 0; k < faceIndices[i].length; k++) {
                    int vertexIndex = indices[faceIndices[i][k]];
                    faceVerts[k] = verts.get(vertexIndex);
                }
                Face face = new Face(faceVerts);
                geometry.add(face);
            }
        }
        
        Mesh convexHull = new Mesh();
        convexHull.setGeometry(geometry);
        //convexHull.setId(1);
        convexHull.setName("convexHull");
        
        return convexHull;
    }

    public static double calculateVolume(Mesh mesh) {
        double volume = 0.0;
        for(Face face : mesh.getGeometry().getFaceList()) {
            Vertex[] verts = face.getVerts();
            Point4d p = verts[0].getLocation();
            Vector3d v1 = new Vector3d(p.x, p.y, p.z);
            p = verts[1].getLocation();
            Vector3d v2 = new Vector3d(p.x, p.y, p.z);
            p = verts[2].getLocation();
            Vector3d v3 = new Vector3d(p.x, p.y, p.z);
            
            Vector3d cross = new Vector3d();
            cross.cross(v1, v2);
            volume += v3.dot(cross);
        }
        return volume;
    }

    public static Vector3d getNormal(Point4d p1, Point4d p2, Point4d p3) {
        Vector3d n01 = new Vector3d(
                p2.x - p1.x,
                p2.y - p1.y,
                p2.z - p1.z
        );
        Vector3d n10 = new Vector3d(
                p3.x - p1.x,
                p3.y - p1.y,
                p3.z - p1.z
        );
        n01.normalize();
        n10.normalize();
        n01.cross(n01, n10);
        n01.normalize();
        return n01;
    }

    public static void main(String... args) {
        ObjLoader loader = new ObjLoader();
        Scene<Mesh> scene = loader.loadFile("C:/Users/cmolikl/Projects/Data/double_squere.obj");

        Mesh mesh = scene.getMesh("Cube");
        double volume = MeshUtils.calculateVolume(mesh);
        volume /= 6.0;
        System.out.println("Volume of mesh is: " + volume);
    }
}
