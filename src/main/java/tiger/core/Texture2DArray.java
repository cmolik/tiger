/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.nio.Buffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 *
 * @author cmolikl
 */
public class Texture2DArray extends Texture {

    public Texture2DArray(int depth) {
        this.depth = depth;

        internalFormat = GL.GL_RGBA8;
        //internalFormat = GL.GL_RGBA16F;
        format = GL.GL_RGBA;
        type = GL.GL_UNSIGNED_BYTE;
        //type = GL.GL_FLOAT;
        //textureType = GL.GL_TEXTURE_3D;
        textureType = GL2.GL_TEXTURE_2D_ARRAY;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }

    public Texture2DArray(int width, int height, int depth) {
        this(depth);
        this.width = width;
        this.height = height;
        this.resizable = false;
    }

    public Texture2DArray(int depth, int type, int format, int internalFormat) {
        this.depth = depth;

        this.internalFormat = internalFormat;
        this.format = format;
        this.type = type;
        textureType = GL2.GL_TEXTURE_2D_ARRAY;

        params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
        params.put(GL2.GL_TEXTURE_WRAP_R, GL.GL_CLAMP_TO_EDGE);
    }

    public Texture2DArray(int width, int height, int depth, int type, int format, int internalFormat) {
        this(depth, type, format, internalFormat);
        this.width = width;
        this.height = height;
        this.resizable = false;
    }

    public static void bindNothing(GL gl) {
        gl.glBindTexture(GL2.GL_TEXTURE_2D_ARRAY, 0);
    }

    public void loadImage(GL gl, int mipLevel, int internalFormat,
        int format, int type, int border, int width, int height, int depth, Buffer data) {

        GL2 gl2 = gl.getGL2();

        bind(gl);
        gl2.glTexImage3D(textureType, mipLevel, internalFormat, width,
            height, depth, border, format, type, data);
    }
    
    public int getDepth() {
        return depth;
    }

}
