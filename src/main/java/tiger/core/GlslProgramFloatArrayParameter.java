/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.util.StringTokenizer;
import com.jogamp.opengl.GL;
import tiger.animation.Interpolable;

/**
 *
 * @author cmolikl
 */
public class GlslProgramFloatArrayParameter extends GlslProgramParameter implements Interpolable<GlslProgramFloatArrayParameter>{
    float[] value;
    private int length;
    private int offset = 0;

    public GlslProgramFloatArrayParameter(String name, float[] value) {
        super(name);
        this.value = value;
        length = value.length;
    }

    public GlslProgramFloatArrayParameter(String name, float[] value, int offset, int length) {
        super(name);
        this.value = value;
        this.offset = offset;
        this.length = length;
    }

    protected void initValue(GL gl, int location) {
        switch (length) {
            case 1:
                gl.getGL2().glUniform1f(location, value[offset]);
                break;
            case 2:
                gl.getGL2().glUniform2f(location, value[offset], value[offset + 1]);
                break;
            case 3:
                gl.getGL2().glUniform3f(location, value[offset], value[offset + 1], value[offset + 2]);
                break;
            case 4:
                gl.getGL2().glUniform4f(location, value[offset], value[offset + 1], value[offset + 2], value[offset + 3]);
                break;
        }
    }
    
    public float[] getValue() {
        return value;
    }
    
    public void setValue(float[] value) {
        this.value = value;
        length = value.length;
    }
    
    public void setValue(float[] value, int offset, int length) {
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
                value = new float[length];
            }
            for(int i = 0; i < length; i++) {
                value[i] = Float.parseFloat(tokenizer.nextToken());
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

    @Override
    public void interpolate(GlslProgramFloatArrayParameter value1, GlslProgramFloatArrayParameter value2, float t) {
        for(int i = 0; i < Math.min(value1.length, value2.length); i++) {
            if(t <= 0) {
                this.value[i] = value1.getValue()[i];
            }
            else if (t >= 1){
                this.value[i] = value2.getValue()[i];
            }
            else {
                this.value[i] = (1f - t)*value1.getValue()[i] + t*value2.getValue()[i];
            }
        }
    }
}
