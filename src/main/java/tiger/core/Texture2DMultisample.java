/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.nio.Buffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

/**
 * 
 * @author cmolikl
 */
public class Texture2DMultisample extends Texture {

        protected static String[] imageTypes = new String[] {
            "TYPE_CUSTOM", // 3 byte RGB
            "TYPE_INT_RGB",
            "TYPE_INT_ARGB",
            "TYPE_INT_ARGB_PRE",
            "TYPE_INT_BGR",
            "TYPE_3BYTE_BGR",
            "TYPE_4BYTE_ABGR",
            "TYPE_4BYTE_ABGR_PRE",
            "TYPE_USHORT_565_RGB",
            "TYPE_USHORT_555_RGB",
            "TYPE_BYTE_GRAY",
            "TYPE_USHORT_GRAY",
            "TYPE_BYTE_BINARY",
            "TYPE_BYTE_INDEXED"
        };

        protected static int[] pixelTypes = new int[] {
            GL.GL_UNSIGNED_BYTE,
            GL2.GL_UNSIGNED_INT_8_8_8_8,
            GL2.GL_UNSIGNED_INT_8_8_8_8,
            GL2.GL_UNSIGNED_INT_8_8_8_8,
            GL2.GL_UNSIGNED_INT_8_8_8_8_REV,
            GL.GL_UNSIGNED_BYTE,
            GL.GL_UNSIGNED_BYTE,
            GL.GL_UNSIGNED_BYTE,
            GL.GL_UNSIGNED_SHORT_5_6_5,
            GL.GL_UNSIGNED_SHORT_5_5_5_1,
            GL.GL_UNSIGNED_BYTE,
            GL.GL_UNSIGNED_SHORT,
            GL2.GL_BITMAP,
            GL.GL_UNSIGNED_BYTE
        };

        protected static int[] pixelFormats = new int[] {
            GL.GL_RGBA,
            GL2.GL_BGR,
            GL.GL_BGRA,
            GL.GL_BGRA,
            GL.GL_RGBA,
            GL2.GL_BGR,
            GL.GL_BGRA,
            GL.GL_BGRA,
            GL2.GL_BGR,
            GL2.GL_BGR,
            GL.GL_LUMINANCE,
            GL.GL_LUMINANCE,
            GL.GL_LUMINANCE,
            GL.GL_LUMINANCE
        };

	public Texture2DMultisample() {
                internalFormat = GL.GL_RGBA32F;
                format = GL.GL_RGBA;
                type = GL.GL_FLOAT;
                textureType = GL3.GL_TEXTURE_2D_MULTISAMPLE;

		//params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		//params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		//params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		//params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	}
	
	public Texture2DMultisample(int width, int height) {
		this();
		this.width = width;
		this.height = height;
                this.resizable = false;
	}
	
	public Texture2DMultisample(int internalFormat, int format, int type) {
		this();
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
	}
	
	public Texture2DMultisample(int width, int height, int internalFormat, int format, int type) {
		this(width, height);
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
	}

        public static void bindNothing(GL gl) {
            gl.glBindTexture(GL3.GL_TEXTURE_2D_MULTISAMPLE, 0);
        }

	public void loadImage(GL gl, int mipLevel, int internalFormat,
			int format, int type, int border, int width, int height, int depth, Buffer data) {
		bind(gl);
                
                // The following line is here as workaround of Frame Buffer Object
		// errors on ATI graphic cards
                //gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
		
                
                GL3 gl3 = gl.getGL3();
                gl3.glTexImage2DMultisample(GL3.GL_TEXTURE_2D_MULTISAMPLE, 4, internalFormat, width, height, false);
	}
}
