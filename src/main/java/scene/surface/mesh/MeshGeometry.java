/**
 *
 */
package scene.surface.mesh;

import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import scene.surface.mesh.Edge.HalfEdge;

/**
 * @author David Cornette
 *
 */
public class MeshGeometry {

    private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private ArrayList<Face> faces = new ArrayList<Face>();

    public Mesh parent;

    public void add(Face f) {
        faces.add(f);
    }

    public void add(Vertex v) {
        v.setIndex(vertices.size());
        vertices.add(v);
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public int numberOfFaces() {
        return faces.size();
    }

    public int numberOfVertices() {
        return vertices.size();
    }

    public ArrayList<Face> getFaceList() {
        return faces;
    }

    public ArrayList<Vertex> getVertexList() {
        return vertices;
    }

    public ArrayList<Edge> getEdgeList() {
        return edges;
    }

    public Edge makeVertexEdge(Vertex v0, Vertex v1, Face f) {
        Edge e = new Edge();
        HalfEdge edge = e.createHalfEdge(v0, v1, Edge.DIR);
        edge.setFace(f);
        HalfEdge twin = e.createHalfEdge(v1, v0, Edge.INV);
        twin.setFace(f);
        edge.setTwin(twin);
        twin.setTwin(edge);

        HalfEdge e0 = v0.getEdge();
        if (e0 != null) {
            while (e0.face() != f) {
                e0 = e0.twin().next();
            }
            edge.setPrev(e0.prev());
            twin.setNext(e0);
            e0.prev().setNext(edge);
            e0.setPrev(twin);
        } else {
            edge.setPrev(twin);
            twin.setNext(edge);
            edge.v0().setEdge(edge);
        }

        HalfEdge e1 = v1.getEdge();
        if (e1 != null) {
            while (e1.face() != f) {
                e1 = e1.twin().next();
            }
            edge.setNext(e1);
            twin.setPrev(e1.prev());
            e1.prev().setNext(twin);
            e1.setPrev(edge);
        } else {
            edge.setNext(twin);
            twin.setPrev(edge);
            edge.v1().setEdge(twin);
        }

        return e;
    }

    public Face makeEdgeFace(HalfEdge e, Face face) {
        if (face.getEdge() == null) {
            face.setEdge(e);
        }
        for (HalfEdge he : face) {
            ;
            he.setFace(face);
        }
        return face;
    }

    public void determineEdges() {
        HashMap<String, Edge> edgeMap = new HashMap<String, Edge>();
        for (Face f : faces) {
            Vertex[] v = f.getVerts();
            HalfEdge first = null;
            HalfEdge prev = null;
            for (int i = 0; i < v.length; i++) {
                Vertex v0 = v[i];
                Vertex v1 = v[(i + 1) % v.length];
                String hash = getEdgeHash(v0, v1);
                Edge e = edgeMap.get(hash);
                HalfEdge he = null;
                if (e == null) {
                    e = new Edge();
                    edgeMap.put(hash, e);
                    addEdge(e);
                    he = e.createHalfEdge(v0, v1, Edge.DIR);
                    if (v0.getEdge() == null) {
                        v0.setEdge(he);
                    }
                    e.twin[Edge.INV] = he;
                } else {
                    he = e.createHalfEdge(v0, v1, Edge.INV);
                    if (v0.getEdge() == null) {
                        v0.setEdge(he);
                    }
                    e.twin[Edge.DIR] = he;
                }
                he.setFace(f);
                if (first == null) {
                    first = he;
                    f.setEdge(he);
                }
                if (prev != null) {
                    he.setPrev(prev);
                    prev.setNext(he);
                }
                if (i == v.length - 1) {
                    first.setPrev(he);
                    he.setNext(first);
                }
                prev = he;
            }
        }
    }

    private String getEdgeHash(Vertex v0, Vertex v1) {
        String h0 = v0.hash();
        String h1 = v1.hash();
        String hash;
        if (v0.getLocation().equals(v1.getLocation())) System.out.println("Error corrupted edge");
        if (!v0.getLocation().equals(v1.getLocation()) && h0.equals(h1)) {
            System.out.println("Error wrong hash");
            System.out.println("V0: " + v0);
            System.out.println("V1: " + v1);
        }
        if (h0.compareTo(h1) < 0) hash = "[" + h0 + "][" + h1 + "]";
        else hash = "[" + h1 + "][" + h0 + "]";
        return hash;
    }

    public void testGeometry() {
        System.out.println("Number of vertices: " + vertices.size());
        for (Vertex v : vertices) {
            if (v.getEdge() == null) {
                System.out.println("Error: vertex without edge");
            } else {
                int edges = 0;
                for (HalfEdge he : v) {
                    edges++;
                }
                System.out.println("Vertex incides with " + edges + " edges");
            }
        }

        System.out.println("Number of edges: " + edges.size());
        for (Edge e : edges) {
            if (e.f[0] == null || e.f[1] == null) {
                System.out.println("Error: edge without face");
            }
            if (e.prev[0] == null || e.prev[1] == null) {
                System.out.println("Error: edge without prev");
            }
            if (e.next[0] == null || e.next[1] == null) {
                System.out.println("Error: edge without next");
            }
            if (e.twin[0] == null || e.twin[1] == null) {
                System.out.println("Error: edge without twin");
            }
        }

        System.out.println("Number of faces: " + faces.size());
        for (Face f : faces) {
            if (f.getEdge() == null) {
                System.out.println("Error: face without edge");
            } else {
                int edges = 0;
                for (HalfEdge he : f) {
                    edges++;
                }
                System.out.println("Face has " + edges + " edges");
            }
        }
    }

    private Vertex copyVertex(Vertex v, HashMap<Vertex, Vertex> vertexCash) {
        Vertex copy = vertexCash.get(v);
        if(copy == null) {
            Point4d l = v.getLocation();
            copy = new Vertex(l.x, l.y, l.z, l.w);
            Vector3d n = v.getNormal();
            if (n != null) {
                copy.setNormal(n.x, n.y, n.z);
            }
            Vector4d tc = v.getTexcoords();
            if (tc != null) {
                copy.setTexCoords(tc.x, tc.y, tc.z);
            }
            Vector4d color = v.getColor();
            if (color != null) {
                copy.setColor(color.x, color.y, color.z, color.w);
            }
            vertexCash.put(v, copy);
        }
        return copy;
    }

    private Face copyFace(Face f, HashMap<Face, Face> faceCache, HashMap<Vertex, Vertex> vertexCache) {
        Face copy = faceCache.get(f);
        if (copy == null) {
            Vertex[] verts = f.getVerts();
            Vertex[] vertsCopy = new Vertex[verts.length];
            for (int i = 0; i < verts.length; i++) {
                vertsCopy[i] = copyVertex(verts[i], vertexCache);
            }
            copy = new Face(vertsCopy);
            faceCache.put(f, copy);
        }
        return copy;
    }

    public MeshGeometry getDeepCopy() {
        MeshGeometry geometryCopy = new MeshGeometry();

        HashMap<Vertex, Vertex> vertexCache = new HashMap<>();
        HashMap<Face, Face> faceCache = new HashMap<>();

        for(Vertex v : vertices) {
            Vertex vertexCopy = copyVertex(v, vertexCache);
            geometryCopy.add(vertexCopy);
        }

        for(Face f : faces) {
            Face faceCopy = copyFace(f, faceCache, vertexCache);
            geometryCopy.add(faceCopy);
        }

        return geometryCopy;
    }

}
