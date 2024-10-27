/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import scene.Scene;
import scene.surface.Surface;
import scene.surface.mesh.Mesh;

/**
 *
 * @author cmolikl
 */
public class Pass implements GLEventListener {

    public HashMap<String, Link<Texture>> textures = new HashMap<>();
    public ArrayList<DefaultParameter> glslDefaultParameters = new ArrayList<>();
    public ArrayList<GlslProgramParameter> glslUniformParameters = new ArrayList<>();
    public ArrayList<GlslProgramParameter> glslVaryingParameters = new ArrayList<>();
    public GlslVertexShader vertexShader;
    public GlslGeometryShader geometryShader;
    public GlslFragmentShader fragmentShader;
    public GlslProgram glslProgram;
    public Link<FrameBuffer> frameBuffer;
    public RenderState renderState;
    public Scene<? extends Surface> scene;
    
    public boolean renderToScreen = false;
    public boolean disabled = false;
    public boolean processSurfaceParameters = false;
    
    private static Integer counter = 0;
    private boolean initialized = false;
    private String name;
    private boolean displayNormals = false;
    
    public Pass() {
        synchronized(counter) {
            counter++;
            name = "Pass " + counter;
        }
    }

    /**
     * @deprecated Use Pass(InputStream vsStream, InputStream fsStream) instead.
     * @param vsPath
     * @param fsPath
     */
    public Pass(URL vsPath, URL fsPath) {
        this();
        vertexShader = new GlslVertexShader(vsPath);
        fragmentShader = new GlslFragmentShader(fsPath);
    }

    /**
     * @deprecated Use Pass(InputStream vsStream, InputStream fsStream,
     *      InputStream gsStream, int inputType, int outputType) instead.
     * @param vsPath
     * @param fsPath
     * @param gsPath
     * @param inputType
     * @param outputType
     */
    public Pass(URL vsPath, URL fsPath, URL gsPath, int inputType, int outputType) {
        this();
        vertexShader = new GlslVertexShader(vsPath);
        geometryShader = new GlslGeometryShader(gsPath, inputType, outputType);
        fragmentShader = new GlslFragmentShader(fsPath);
    }
    
    public Pass(InputStream vsStream, InputStream fsStream) {
        this();
        vertexShader = new GlslVertexShader(vsStream);
        fragmentShader = new GlslFragmentShader(fsStream);
    }

    public Pass(InputStream vsStream, InputStream fsStream, InputStream gsStream, int inputType, int outputType) {
        this();
        vertexShader = new GlslVertexShader(vsStream);
        geometryShader = new GlslGeometryShader(gsStream, inputType, outputType);
        fragmentShader = new GlslFragmentShader(fsStream);
    }
    
    public void renderScene(GLAutoDrawable drawable) {
        renderScene(drawable, scene.getAllMeshes());
    } 

    public void renderScene(GLAutoDrawable drawable, Scene<? extends Surface> scene) {
        renderScene(drawable, scene.getAllMeshes());
    }
    
    public void renderScene(GLAutoDrawable drawable, Collection<? extends Surface> surfaces) {
        GL2 gl = drawable.getGL().getGL2();
        for (Surface s : surfaces) {
            renderSurface(drawable, s);
            if(s instanceof Mesh) {
                Mesh mesh = (Mesh) s;
                for (DefaultParameter parameter : glslDefaultParameters) {
                    switch (parameter) {
                        case MESH_ID:
                            gl.glUniform1f(parameter.location, (mesh.getId() - 0.5f) / ((float) surfaces.size()));
                    }
                }
            }
        }
    }
    
//    public void renderScene(GLAutoDrawable drawable, Mesh[] meshes) {
//        GL2 gl = drawable.getGL().getGL2();
//        
//        for (Mesh mesh : meshes) {
//            for (DefaultParameter parameter : glslDefaultParameters) {
//                switch (parameter) {
//                    case MESH_ID:
//                        gl.glUniform1f(parameter.location, (mesh.getId() - 0.5f) / ((float) meshes.length));
//                }
//            }
//            mesh.getRenderer().render(drawable, mesh);
//        }
//    }

    public void renderSurface(GLAutoDrawable drawable, Surface s) {
        GL2 gl = drawable.getGL().getGL2();
        if(glslProgram != null && processSurfaceParameters) {
            Collection<GlslProgramParameter> parameters = s.getParameters();
            for(GlslProgramParameter parameter : parameters) {
                parameter.init(glslProgram);
            }
        }
        s.getRenderer().render(drawable, s);
    }

/*    
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

        Iterator<Face> iterf = mesh.getGeometry().getFaceList().iterator();
        while (iterf.hasNext()) {
            Face f = iterf.next();
            Vertex[] verts = f.getVerts();
            int drawtype = GL.GL_TRIANGLES;
            if (verts.length == 4) {
                drawtype = GL2.GL_QUADS;
            } else if (verts.length == 6) {
                drawtype = GL2.GL_TRIANGLES_ADJACENCY_ARB;
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
                    gl.glColor4d(d.x(), d.y(), d.z(), 1.0);
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
    }*/

    public synchronized void init(GLAutoDrawable drawable) {
        if(initialized) return;
        initialized = true;

        GL gl = drawable.getGL();
        
        // Create, init and link GLSL program
        if(vertexShader != null || fragmentShader != null) {            
            glslProgram = new GlslProgram();
            glslProgram.init(gl);
            if(vertexShader != null) {
                vertexShader.init(gl);
                glslProgram.attachGlslShader(gl, vertexShader);
            }
            if(fragmentShader != null) {
                fragmentShader.init(gl);
                glslProgram.attachGlslShader(gl, fragmentShader);
            }
            if (geometryShader != null) {
                geometryShader.init(gl);
                glslProgram.attachGlslShader(gl, geometryShader);
            }
            glslProgram.linkProgram(gl);
            glslProgram.useProgram(gl);

            // Send uniform variables to program
            for (GlslProgramParameter parameter : glslUniformParameters) {
                parameter.init(glslProgram);
            }

            // Init default variables
            for (DefaultParameter parameter : glslDefaultParameters) {
                parameter.init(glslProgram);
            }
        }
    }
    
    public void prepare(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        if (frameBuffer != null && !renderToScreen) {
            FrameBuffer fb = frameBuffer.get();
            fb.bind(gl);
            fb.setupDrawBuffers(gl);
            fb.checkStatus(gl);
        } else {
            FrameBuffer.bindScreen(gl);
        }
        
        if(renderState != null) {
            renderState.apply(gl);
        }
        
        if (frameBuffer != null && !renderToScreen) {
            frameBuffer.get().setupBlending(gl);
        }

        if(glslProgram != null) {
            glslProgram.useProgram(gl);
            for(GlslProgramParameter parameter : glslVaryingParameters) {
                parameter.init(glslProgram);
            }
        }
        else {
            gl.glUseProgram(0);
        }
        
        //if(textures.keySet().size() > 0) {
        //    gl.glEnable(GL.GL_TEXTURE_2D);
        //}
        
        int i = 0;
        for(String name : textures.keySet()) {
            int location = gl.glGetUniformLocation(glslProgram.getGlNumber(), name);
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            textures.get(name).get().bind(gl);
            gl.glUniform1i(location, i);  
            i++;
        }
    }
    
    public void prepare(GLAutoDrawable drawable, FrameBuffer frameBuffer, Link<Texture>[] textures, String[] textureNames) {
        GL2 gl = drawable.getGL().getGL2();
        
        if (frameBuffer != null && !renderToScreen) {
            frameBuffer.bind(gl);
            frameBuffer.setupDrawBuffers(gl);
            frameBuffer.checkStatus(gl);
        } else {
            FrameBuffer.bindScreen(gl);
        }
        
        if(renderState != null) {
            renderState.apply(gl);
        }
        
        if (frameBuffer != null && !renderToScreen) {
            frameBuffer.get().setupBlending(gl);
        }

        if(glslProgram != null) {
            glslProgram.useProgram(gl);
            for(GlslProgramParameter parameter : glslVaryingParameters) {
                parameter.init(glslProgram);
            }
        }
        else {
            gl.glUseProgram(0);
        }
        
        int i = 0;
        for(Link<Texture> texture : textures) {
            int location = gl.glGetUniformLocation(glslProgram.getGlNumber(), textureNames[i]);
            gl.glActiveTexture(GL.GL_TEXTURE0 + i);
            texture.get().bind(gl);
            gl.glUniform1i(location, i);  
            i++;
        }
    }

    public void display(GLAutoDrawable drawable) {
        if(!disabled) {
            prepare(drawable);
            renderScene(drawable);
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }
    
    public void addTexture(Link<Texture> texture, String name) {
        textures.put(name, texture);
    }
    
    public void removeTexture(String name) {
        textures.remove(name);
    }
    
    public void setTarget(Link<FrameBuffer> frameBuffer) {
        this.frameBuffer = frameBuffer;
    }
    
    public void removeTarget() {
        frameBuffer = null;
    }
    
    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }

    public void dispose(GLAutoDrawable arg0) {
    }
}
