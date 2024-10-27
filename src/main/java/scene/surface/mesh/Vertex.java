/**
 *
 */
package scene.surface.mesh;

import java.util.Iterator;
import javax.vecmath.Point4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import scene.surface.mesh.Edge.HalfEdge;

/**
 * @author David Cornette
 *
 */
public class Vertex implements Iterable<HalfEdge> {

    private class VertexEdgesIterator implements Iterator<HalfEdge> {
        boolean first = true;
        HalfEdge nextEdge;

        private VertexEdgesIterator() {
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
            if(nextEdge.twin() != null) {
                nextEdge = nextEdge.twin().next();
            }
            else {
                nextEdge = null;
            }
            return e;
        }
    }

    private Point4d location = null;
    private Vector4d texcoords = null;
    private Vector3d normal = null;
    private Vector4d color = null;

    private HalfEdge edge;

    public int index;

    public Mesh mesh;

    public Vertex(double x, double y, double z) {
        location = new Point4d(x, y, z, 1.0);
    }

    public Vertex(double x, double y, double z, double w) {
        location = new Point4d(x, y, z, w);
    }

    public Vertex(Point4d location) {
        this.location = location;
    }

    public Vertex(Point4d location, Vector3d normal) {
        this(location);
        this.normal = normal;
    }

    public void setTexCoords(double u, double v, double w) {
        texcoords = new Vector4d(u, v, w, 0.0);
    }

    public void setNormal(double x, double y, double z) {
        normal = new Vector3d(x, y, z);
    }

    public void setColor(double r, double g, double b, double a) {
        color = new Vector4d(r, g, b, a);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return Returns the location.
     */
    public Point4d getLocation() {
        return location;
    }

    public void setLocation(Point4d loc) {
        location = loc;
    }

    /**
     * @return Returns the normal.
     */
    public Vector3d getNormal() {
        return normal;
    }

    /**
     * @return Returns the texcoords.
     */
    public Vector4d getTexcoords() {
        return texcoords;
    }

    public Vector4d getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return location.x + ", " + location.y + ", " + location.z + ", " + location.w;
    }


    public String hash() {
        return toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        if (this.location != other.location && (this.location == null || !this.location.equals(other.location))) {
            return false;
        }
        return true;
    }

    public void setEdge(HalfEdge e) {
        this.edge = e;
    }

    public HalfEdge getEdge() {
        return edge;
    }

    @Override
    public Iterator<HalfEdge> iterator() {
        return new VertexEdgesIterator();
    }
}
