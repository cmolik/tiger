package scene.surface.mesh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import scene.surface.mesh.Edge.HalfEdge;

import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;

public class Face implements Iterable<HalfEdge> {

    private class FaceEdgesIterator implements Iterator<HalfEdge> {
        boolean first = true;
        HalfEdge nextEdge;

        private FaceEdgesIterator() {
            nextEdge = edge;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = nextEdge != null;
            if (first) {
                first = false;
            } else {
                hasNext &= nextEdge != edge;
            }
            return hasNext;
        }

        @Override
        public HalfEdge next() {
            HalfEdge e = nextEdge;
            nextEdge = nextEdge.next();
            return e;
        }
    }

    private Vertex[] verts;
    private HalfEdge edge;

    public Face(Vertex... faceverts) {
        verts = faceverts;
    }

    /**
     * @return Returns the verts.
     */
    public Vertex[] getVerts() {
        if (edge != null) {
            ArrayList<Vertex> v = new ArrayList<Vertex>();
            HalfEdge e = edge;
            HalfEdge first = e;
            do {
                v.add(e.v0());
                e = e.next();
            }
            while (e != first);
            verts = v.toArray(new Vertex[v.size()]);
        }
        return verts;
    }

    public Vertex[] getFlatShadingVerts() {
        if (edge != null) {
            ArrayList<Vertex> v = new ArrayList<Vertex>();
            HalfEdge e = edge;
            HalfEdge first = e;
            HalfEdge second = e.next();
            Vector3d n = MeshUtils.getNormal(second.v0.getLocation(), first.v0.getLocation(), second.v1.getLocation());
            n.normalize();
            do {
                Point4d l = e.v0.getLocation();
                Vertex vertex = new Vertex(l, n);
                //Vertex vertex = new Vertex(l.x, l.y, l.z, l.w);
                //vertex.setNormal(n.x, n.y, n.z);
                v.add(vertex);
                e = e.next();
            }
            while (e != first);
            verts = v.toArray(new Vertex[v.size()]);
        }
        return verts;
    }

    /**
     * @param verts The verts to set.
     */
    public void setVerts(Vertex... verts) {
        this.verts = verts;
    }

    /**
     * @return Returns the edge.
     */
    public HalfEdge getEdge() {
        return edge;
    }

    /**
     * @param e The edge to set.
     */
    public void setEdge(HalfEdge e) {
        this.edge = e;
    }

    @Override
    public Iterator<HalfEdge> iterator() {
        return new FaceEdgesIterator();
    }

}
