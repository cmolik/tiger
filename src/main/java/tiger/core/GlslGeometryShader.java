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
import com.jogamp.opengl.GL3;

/**
 *
 * @author cmolikl
 */
public class GlslGeometryShader extends GlslShader {
    
    private String[] program;
    //private Map<String, Object> parameters = new HashMap<String ,Object>();
    private int inputType;
    private int outputType;
    
    public GlslGeometryShader(String program, int inputType, int outputType) {
        this.program = new String[] {program};
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public GlslGeometryShader(BufferedReader reader, int inputType, int outputType) {
        try {
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line).append("\n");
            }
            reader.close();
            program = new String[] {buffer.toString()};
            this.inputType = inputType;
            this.outputType = outputType;
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

    public GlslGeometryShader(InputStream is, int inputType, int outputType) {
        this(new BufferedReader(new InputStreamReader(is)), inputType, outputType);
    }
    
    public GlslGeometryShader(URL url, int inputType, int outputType) {
        try {
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new FileReader(url.getPath().replaceAll("%20", " ")));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line).append("\n");
            }
            reader.close();
            program = new String[] {buffer.toString()};
            this.inputType = inputType;
            this.outputType = outputType;
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
        //GLU glu = new GLU();
        //GL gl = glu.getCurrentGL();
        GL3 gl2 = gl.getGL3();
        //Obtain the number of fragment program
        glNumber = gl2.glCreateShader(GL3.GL_GEOMETRY_SHADER_ARB);
        
        gl2.glShaderSource(glNumber, 1, program, (int[]) null, 0);
        
        gl2.glCompileShader(glNumber);
        
        // Check for errors
        int[] tmp = new int[1];
        gl2.glGetShaderiv(glNumber, GL2.GL_COMPILE_STATUS, tmp, 0);
        if (tmp[0] == 0) {
          System.out.println("Geometry program failed to compile:");
          byte[] buffer = new byte[100];
          gl2.glGetShaderInfoLog(glNumber, 100, (int[]) null, 0, buffer, 0);
          System.out.println("Error message: " + new String(buffer));
        }
    }
    
    public int getInputType() {
        return inputType;
    }
    
    public int getOutputType() {
        return outputType;
    }
}
