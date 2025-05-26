/*
 * Shader.java
 * 
 * Created on 25.10.2007, 15:16:53
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 *
 * @author cmolikl
 */
public class GlslVertexShader extends GlslShader {
    
    private String filePath;
    private String[] program;
    //private Map<String, Object> parameters = new HashMap<String ,Object>();
    
    public GlslVertexShader(String vertexProgram) {
        this.program = new String[] {vertexProgram};
    }

    public GlslVertexShader(BufferedReader reader) {
        try {
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line).append("\n");
            }
            reader.close();
            program = new String[] {buffer.toString()};
        }
        catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    public GlslVertexShader(InputStream is) {
        this(new BufferedReader(new InputStreamReader(is)));
    }
    
    public GlslVertexShader(URL url) {
        try {
            filePath = url.toString();
            System.out.println("Vertex shader: " + filePath);
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line).append("\n");
            }
            reader.close();
            program = new String[] {buffer.toString()};
        }
        catch(FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(1);
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
    }
    
    public void init(GL gl) {

        GL2 gl2 = gl.getGL2();
        //Obtain the number of fragment program
        glNumber = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
        
        gl2.glShaderSource(glNumber, 1, program, (int[]) null, 0);
        
        gl2.glCompileShader(glNumber);
        
        // Check for errors
        int[] tmp = new int[1];
        gl2.glGetShaderiv(glNumber, GL2.GL_COMPILE_STATUS, tmp, 0);
        if (tmp[0] == 0) {
          System.out.println("Vertex program " + filePath + " failed to compile:");
          byte[] buffer = new byte[100];
          gl2.glGetShaderInfoLog(glNumber, 100, (int[]) null, 0, buffer, 0);
          System.out.println("Error message: " + new String(buffer));
        }
    }
    
    
}
