/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scene.surface.mesh;

import javax.vecmath.Point4d;

/**
 *
 * @author cmolikl
 */
public class Edge {
    
    public static int DIR = 0;
    public static int INV = 1;
    
    public Edge e;
    public Face[] f;
    public HalfEdge[] prev;
    public HalfEdge[] next;
    public HalfEdge[] twin;
    
    public class HalfEdge {
        int dir;
        Vertex v0;
        Vertex v1;
        private HalfEdge(Vertex v0, Vertex v1, int dir) {
            this.dir = dir;
            this.v0 = v0;
            this.v1 = v1;
        }
        
        public Vertex v0() {
            return v0;
        }
        
        public void setV0(Vertex v0) {
            this.v0 = v0;
        }
        
        public Vertex v1() {
            return v1;
        }
        
        public void setV1(Vertex v1) {
            this.v1 = v1;
        }
        
        public Edge edge() {
            return e;
        }
        
        public Face face() {
            return f[dir];
        }
        
        public void setFace(Face face) {
            f[dir] = face;
        }
        
        public HalfEdge prev() {
            return prev[dir];
        }
        
        public void setPrev(HalfEdge edge) {
            prev[dir] = edge;
        }
        
        public HalfEdge next() {
            return next[dir];
        }
        
        public void setNext(HalfEdge edge) {
            next[dir] = edge;
        }
        
        public HalfEdge twin() {
            return twin[dir];
        }
        
        public void setTwin(HalfEdge edge) {
            twin[dir] = edge;
        }
        
        /*public String getHalfHash() {
            return getHash(v0(), v1());
        }
        
        public String getTwinHash() {
            return getHash(v1(), v0());
        }
        
        public String getHash(Vertex v0, Vertex v1) {
            String h0 = v0.hash();
            String h1 = v1.hash();
            return  h0 + "," + h1;
        }*/
    }
    
    public Edge() {
        f = new Face[2];
        prev = new HalfEdge[2];
        next = new HalfEdge[2];
        twin = new HalfEdge[2];
        e = this;
    }
    
    public HalfEdge createHalfEdge(Vertex v0, Vertex v1, int dir) {
        return new HalfEdge(v0, v1, dir);
    }
    
    public HalfEdge getHalfEdge(int dir) {
        return twin[(dir + 1)%2];
    }
}
