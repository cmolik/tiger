/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.core;

import java.util.StringTokenizer;
import com.jogamp.opengl.GL;

/**
 *
 * @author cmolikl
 */
public class GlslProgramIntArrayParameter extends GlslProgramParameter {

    int[] value;
    private int length;
    private int offset = 0;

    public GlslProgramIntArrayParameter(String name, int[] value) {
        super(name);
        this.value = value;
        length = value.length;
    }

    public GlslProgramIntArrayParameter(String name, int[] value, int offset, int length) {
        super(name);
        this.value = value;
        this.offset = offset;
        this.length = length;
    }

    protected void initValue(GL gl, int location) {
        switch (length) {
            case 1:
                gl.getGL2().glUniform1i(location, value[offset]);
                break;
            case 2:
                gl.getGL2().glUniform2i(location, value[offset], value[offset + 1]);
                break;
            case 3:
                gl.getGL2().glUniform3i(location, value[offset], value[offset + 1], value[offset + 2]);
                break;
            case 4:
                gl.getGL2().glUniform4i(location, value[offset], value[offset + 1], value[offset + 2], value[offset + 3]);
                break;
        }
    }
    
    public void setValue(int[] value) {
        this.value = value;
        length = value.length;
    }
    
    public void setValue(int[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.length = length;
    }
    
    public boolean parseValue(String string) {
       try {
           StringTokenizer tokenizer = new StringTokenizer(string, " ");
           length = tokenizer.countTokens();
           if(length == 0) {
               return false;
           } 
           if(value.length != length) {
               value = new int[length];
           }
           for(int i = 0; i < length; i++) {
                value[i] = Integer.parseInt(tokenizer.nextToken());
           }
       }
       catch(Exception e) {
           return false;
       }
       return true;
    }
    
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < length; i++) {
            buffer.append(value[i]);
            if(i + 1 == length) break;
            buffer.append(" ");
        }
        return buffer.toString();
    }
}
