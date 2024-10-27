/**
 * 
 */
package mesh.loaders;

import gleem.BSphere;
import gleem.linalg.Vec3f;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.vecmath.Point3d;
import javax.vecmath.Point4d;
import scene.surface.Appearance;
import scene.surface.mesh.Face;
import scene.surface.mesh.Mesh;
import scene.surface.mesh.MeshGeometry;
import scene.Scene;
import scene.surface.mesh.Vertex;

/**
 * @author David Cornette
 *
 */
public class ObjLoader {

    private ArrayList<Point4d> vertices = new ArrayList<Point4d>();
    private ArrayList<V3> normals = new ArrayList<V3>();
    private ArrayList<V3> texcoords = new ArrayList<V3>();
    private int objectCounter = 0;
    private int faceCounter = 0;

    /**
     * @author David Cornette
     *
     */
    private class V3 {

        private double x = 0;
        private double y = 0;
        private double z = 0;

        public V3() {
            super();
        }

        /**
         * @param x
         */
        public V3(double x) {
            super();
            // TODO Auto-generated constructor stub
            this.x = x;
        }

        /**
         * @param x
         * @param y
         */
        public V3(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        /**
         * @param x
         * @param y
         * @param z
         */
        public V3(double x, double y, double z) {
            super();
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }
    }
    
    public Scene<Mesh> loadFile(String filename) {
        File file = new File(filename);
        return loadFile(file);
    }
    
    public Scene<Mesh> loadFile(URL fileURL) {
        return loadFile(fileURL.getPath());
    }

    public Scene<Mesh> loadFile(File file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            return loadFile(br, file.getParent());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if (br != null) br.close();
            }
            catch (IOException ex) {}
        }
    }

    public Scene<Mesh> loadFile(BufferedReader br, String parentPath) throws IOException {
            // Some ugly regular expressions
            Pattern commentpat = Pattern.compile("^#");
            Pattern mtllibpat = Pattern.compile("^mtllib\\s+(\\S*)$");
            Pattern usemtlpat = Pattern.compile("^usemtl\\s+(\\S*)$");
            Pattern opat = Pattern.compile("^o\\s+(\\S*)$");
            Pattern vpat = Pattern.compile("^v\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
            Pattern vnpat = Pattern.compile("^vn\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)$");
            Pattern vtpat = Pattern.compile("^vt\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)(?:\\s+(-?\\d*.?\\d+(?:e-?\\d+)?)(?:\\s+(-?\\d*.?\\d+(?:e-?\\d+)?))?)?$");
            Pattern fpat = Pattern.compile("^f\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?(?:\\s+(\\d+)(?:/(\\d*))?(?:/(\\d+))?)?$");
            Pattern spat = Pattern.compile("^s\\s+(\\S*)$");
            Pattern blankpat = Pattern.compile("^\\s*$");

            vertices.add(new Point4d()); // obj uses an array index base of 1, so just add a dummy
            normals.add(new V3()); // obj uses an array index base of 1, so just add a dummy
            texcoords.add(new V3()); // obj uses an array index base of 1, so just add a dummy
            Scene scene = new Scene();
            if(parentPath != null) {
                scene.path = parentPath;
            }
            //String fn = file.getName();
            //int dot = fn.indexOf('.');
            //scene.fileName = fn.substring(0, dot);
            //scene.extension = fn.substring(dot + 1);
            HashMap<String, Appearance> appearances = new HashMap<String, Appearance>();

            Mesh mesh = new Mesh();
            MeshGeometry meshGeometry = new MeshGeometry();
            meshGeometry.parent = mesh;

            double minX = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double minY = Double.MAX_VALUE;
            double maxY = Double.MIN_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxZ = Double.MIN_VALUE;

            String line;
            while ((line = br.readLine()) != null) {

                Matcher commentm = commentpat.matcher(line);
                Matcher mtllibm = mtllibpat.matcher(line);
                Matcher usemtlm = usemtlpat.matcher(line);
                Matcher om = opat.matcher(line);
                Matcher vm = vpat.matcher(line);
                Matcher vnm = vnpat.matcher(line);
                Matcher vtm = vtpat.matcher(line);
                Matcher fm = fpat.matcher(line);
                Matcher sm = spat.matcher(line);
                Matcher blankm = blankpat.matcher(line);


                if (commentm.find()) {
                //System.out.println("Comment line.");
                } else if (mtllibm.find()) {
                    try {
                        String mtllib = mtllibm.group(1);
                        //System.out.println("mtllib:" + mtllib);
                        if (mtllib != null && parentPath != null) {
                            appearances.putAll(MtlLoader.loadFile(parentPath + "/" + mtllib));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (usemtlm.find()) {
                    String usemtl = usemtlm.group(1);
                    //System.out.println("usemtl:" + usemtl);
                    if (usemtl != null && !"(null)".equals(usemtl)) {
                        Appearance a = appearances.get(usemtl);
                        if (a != null) {
                            mesh.setAppearance(a);
                        }
                    }
                } else if (om.find()) {
                    //System.out.println("Object line:" + om.group(1));
                    // Might as well set the name
                    if (meshGeometry.numberOfFaces() > 0) {
                        mesh.setGeometry(meshGeometry);
                        scene.addMesh(mesh);
                        mesh = new Mesh();
                        meshGeometry = new MeshGeometry();
                        meshGeometry.parent = mesh;
                    }
                    mesh.setName(om.group(1));
                    objectCounter++;
                //mesh.setAppearance(appearance);
                //appearance = deffaultAppearance;
                } else if (vm.find()) {
                    Point4d v = new Point4d(Double.parseDouble(vm.group(1)), Double.parseDouble(vm.group(2)), Double.parseDouble(vm.group(3)), 1.0);

                    maxX = Math.max(maxX, v.getX());
                    maxY = Math.max(maxY, v.getY());
                    maxZ = Math.max(maxZ, v.getZ());

                    minX = Math.min(minX, v.getX());
                    minY = Math.min(minY, v.getY());
                    minZ = Math.min(minZ, v.getZ());
                    
//                    center.x += v.x;
//                    center.y += v.y;
//                    center.z += v.z;

                    vertices.add(v);
                //System.out.println("vertex line:" + vm.group(1) + " " + vm.group(2) + " " + vm.group(3));
                } else if (vnm.find()) {
                    // vertex normal
                    double x = Double.parseDouble(vnm.group(1));
                    double y = Double.parseDouble(vnm.group(2));
                    double z = Double.parseDouble(vnm.group(3));
                    normals.add(new V3(x, y, z));
                //System.out.println("vertex normal:" + vnm.group(1) + " " + vnm.group(2) + " " + vnm.group(3));
                } else if (vtm.find()) {
                    // Texture Coordinates
                    double u;
                    double v;
                    double w;
                    u = Double.parseDouble(vtm.group(1));
                    if (vtm.groupCount() == 2) {
                        v = 0;
                        w = 0;
                    } else if (vtm.groupCount() == 3) {
                        v = Double.parseDouble(vtm.group(2));
                        w = 0;
                    } else {
                        // must be 3, then
                        v = Double.parseDouble(vtm.group(2));
                        w = Double.parseDouble(vtm.group(3));
                    }
                    texcoords.add(new V3(u, v, w));
                //System.out.println("3D texture coord:" + vtm.group(1) + " " + vtm.group(2) + vtm.group(3));
                } else if (fm.find()) {
                    String tc1s = fm.group(2);
                    String tc2s = fm.group(5);
                    String tc3s = fm.group(8);
                    String tc4s = fm.group(11);

                    boolean hastexcoords = (!tc1s.equals("")) && (!tc2s.equals("")) && (!tc3s.equals("")) && ((tc4s == null) || (!tc4s.equals("")));

                    String n1s = fm.group(3);
                    String n2s = fm.group(6);
                    String n3s = fm.group(9);
                    String n4s = fm.group(12);
                    boolean hasnormals = (!n1s.equals("")) && (!n2s.equals("")) && (!n3s.equals("")) && ((n4s == null) || (!n4s.equals("")));

                    int vert1 = Integer.parseInt(fm.group(1));
                    int vert2 = Integer.parseInt(fm.group(4));
                    int vert3 = Integer.parseInt(fm.group(7));
                    int vert4 = 0;
                    if (fm.group(11) != null) {
                        vert4 = Integer.parseInt(fm.group(10));
                    }

                    int tc1 = 0;
                    int tc2 = 0;
                    int tc3 = 0;
                    int tc4 = 0;
                    if (hastexcoords) {
                        tc1 = Integer.parseInt(tc1s);
                        tc2 = Integer.parseInt(tc2s);
                        tc3 = Integer.parseInt(tc3s);
                        if (tc4s != null) {
                            tc4 = Integer.parseInt(tc4s);
                        }
                    }

                    int n1 = 0;
                    int n2 = 0;
                    int n3 = 0;
                    int n4 = 0;
                    if (hasnormals) {
                        n1 = Integer.parseInt(n1s);
                        n2 = Integer.parseInt(n2s);
                        n3 = Integer.parseInt(n3s);
                        if (n4s != null) {
                            n4 = Integer.parseInt(n4s);
                        }
                    }

                    Vertex v1 = createVertex(vert1, tc1, n1);
                    v1.mesh = mesh;
                    meshGeometry.add(v1);
                    Vertex v2 = createVertex(vert2, tc2, n2);
                    v2.mesh = mesh;
                    meshGeometry.add(v2);
                    Vertex v3 = createVertex(vert3, tc3, n3);
                    v3.mesh = mesh;
                    meshGeometry.add(v3);
                    Vertex v4 = null;
                    Vertex[] faceverts;
                    if (vert4 == 0) {
                        faceverts = new Vertex[]{v1, v2, v3};
                    } else {
                        v4 = createVertex(vert4, tc4, n4);
                        v4.mesh = mesh;
                        meshGeometry.add(v4);
                        faceverts = new Vertex[]{v1, v2, v3, v4};
                    }

                    Face f = new Face(faceverts);

                    meshGeometry.add(f);
                    //System.out.println("face number " + mesh.numberOfFaces() + " faces");
                    faceCounter++;
                } else if (sm.find()) {
                //System.out.println("smoothing group:" + sm.group(1));
                } else if (blankm.find()) {
                //System.out.println("blank line...");
                } else {
                //System.out.println("Other type of line");
                }
            }
            mesh.setGeometry(meshGeometry);
            scene.addMesh(mesh);
            Vec3f c1 = new Vec3f((float) maxX, (float) maxY, (float) maxZ);
            Vec3f c2 = new Vec3f((float) minX, (float) minY, (float) minZ);

            Vec3f c = c1.plus(c2);
            c.scale(0.5f);

            //Vec3f d = c1.minus(c2);
            
            double dist = -1.0;
            Point3d center = new Point3d(c.x(), c.y(), c.z());
            for(Point4d v : vertices) {
                dist = Math.max(dist, center.distance(new Point3d(v.x, v.y, v.z)));
            }

            BSphere sphere = new BSphere(c, (float) dist);
            scene.setBoundingSphere(sphere);

            System.out.println(objectCounter + " objects loaded.");
            System.out.println(faceCounter + " faces loaded");

            return scene;
    }

    private Vertex createVertex(int vertnum, int tcnum, int nnum) {
        if (vertnum == 0) {
            return null;
        }
        Point4d location = vertices.get(vertnum);
        Vertex v = new Vertex(location);
        v.index = vertnum;
        if (tcnum != 0) {
            V3 tc = texcoords.get(tcnum);
            v.setTexCoords(tc.getX(), tc.getY(), tc.getZ());
        }
        if (nnum != 0) {
            V3 n = normals.get(nnum);
            v.setNormal(n.getX(), n.getY(), n.getZ());
        }
        return v;
    }
}
