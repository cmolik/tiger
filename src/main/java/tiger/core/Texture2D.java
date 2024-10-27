/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

/**
 * 
 * @author cmolikl
 */
public class Texture2D extends Texture {

        public static String[] imageTypes = new String[] {
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

        public static int[] pixelTypes = new int[] {
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

        public static int[] pixelFormats = new int[] {
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

	public Texture2D() {
        internalFormat = GL.GL_RGBA32F;
        format = GL.GL_RGBA;
        type = GL.GL_FLOAT;
        textureType = GL.GL_TEXTURE_2D;

		params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		params.put(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		params.put(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
	}
	
	public Texture2D(int width, int height) {
		this();
		this.width = width;
		this.height = height;
        this.resizable = false;
	}
	
	public Texture2D(int internalFormat, int format, int type) {
		this();
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
	}
	
	public Texture2D(int width, int height, int internalFormat, int format, int type) {
		this(width, height);
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
	}

        public static void bindNothing(GL gl) {
            gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        }

        public void loadData(BufferedImage image) {

            width = image.getWidth();
            height = image.getHeight();

            int imageType = image.getType();
            System.out.println("Image type: " + imageTypes[imageType]);
            DataBuffer dataBuffer = image.getRaster().getDataBuffer();

            resizable = false;
            type = pixelTypes[imageType];
            format = pixelFormats[imageType];
            params.put(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            params.put(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            switch(imageType) {
                case BufferedImage.TYPE_CUSTOM:
                    switch(image.getSampleModel().getNumBands()) {
                        case 1:
                            format = GL.GL_LUMINANCE;
                            break;
                        case 3:
                            format = GL.GL_RGB;
                            break;
                        case 4:
                            format = GL.GL_RGBA;
                            break;
                    }
                case BufferedImage.TYPE_3BYTE_BGR:
                case BufferedImage.TYPE_4BYTE_ABGR:
                case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                case BufferedImage.TYPE_BYTE_BINARY:
                case BufferedImage.TYPE_BYTE_GRAY:
                case BufferedImage.TYPE_BYTE_INDEXED:
                    imageData = ByteBuffer.wrap(((DataBufferByte) dataBuffer).getData());
                    break;
                case BufferedImage.TYPE_INT_ARGB:
                case BufferedImage.TYPE_INT_ARGB_PRE:
                case BufferedImage.TYPE_INT_BGR:
                case BufferedImage.TYPE_INT_RGB:
                    imageData = IntBuffer.wrap(((DataBufferInt) dataBuffer).getData());
                    break;
                case BufferedImage.TYPE_USHORT_GRAY:
                case BufferedImage.TYPE_USHORT_555_RGB:
                case BufferedImage.TYPE_USHORT_565_RGB:
                    internalFormat = GL2.GL_LUMINANCE16;
                    imageData = ShortBuffer.wrap(((DataBufferUShort) dataBuffer).getData());
                    break;
                default:
                    System.out.println("Unknown image type");
            }
            
            if(imageType == BufferedImage.TYPE_4BYTE_ABGR) {
                ByteBuffer buffer = (ByteBuffer) imageData;
                byte[] array;
                if(buffer.hasArray()) {
                    array = buffer.array();
                    for(int i = 0; i < array.length; i += 4) {
                        byte a = array[i];
                        byte b = array[i+1];
                        byte g = array[i+2];
                        byte r = array[i+3];
                        array[i] = b;
                        array[i+1] = g;
                        array[i+2] = r;
                        array[i+3] = a;
                    }
                }
                
            }
        }

	/*public void init(GL gl) {
                if(!initialized) {
                    initialized = true;
                    glNumber = IntBuffer.allocate(1);
                    gl.glGenTextures(1, glNumber);
                    bind(gl);
                    for (Entry<Integer, Integer> param : params.entrySet()) {
                            gl.glTexParameteri(textureType, param.getKey(), param.getValue());
                    }
                    // The following lines are here as workaround of Frame Buffer Object
                    // errors on ATI graphic cards
                    loadImage(gl, 0, internalFormat, format, type, 0, width, height, 0, imageData);
                }
	}

        public void init(GLAutoDrawable drawable) {
            if(!initialized) {
                initialized = true;
                GL gl = drawable.getGL();
		glNumber = IntBuffer.allocate(1);
		gl.glGenTextures(1, glNumber);
                bind(gl);
		for (Entry<Integer, Integer> param : params.entrySet()) {
			gl.glTexParameteri(textureType, param.getKey(), param.getValue());
		}
		// The following line is here as workaround of Frame Buffer Object
		// errors on ATI graphic cards
                loadImage(gl, 0, internalFormat, format, type, 0, drawable.getWidth(), drawable.getHeight(), 0, imageData);
            }
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		if(resizable) {
                        this.width = width;
                        this.height = height;
			GL gl = drawable.getGL();
			loadImage(gl, 0, internalFormat, format, type, 0, width, height, 0, imageData);
		}
	}*/

	public void loadImage(GL gl, int mipLevel, int internalFormat, int format, int type, int border, int width, int height, int depth, Buffer data) {
		bind(gl);
                
                // The following line is here as workaround of Frame Buffer Object
		// errors on ATI graphic cards
                //gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_GENERATE_MIPMAP, GL.GL_TRUE);
		
                gl.glTexImage2D(GL.GL_TEXTURE_2D, mipLevel, internalFormat, width, height, border, format, type, data);
                //GL3 gl3 = gl.getGL3();
                //gl3.glTexImage2DMultisample(GL3.GL_TEXTURE_2D_MULTISAMPLE, 4, internalFormat, width, height, false);
	}
        
        public void loadSubImage(GL gl, int mipLevel, int format, int type, int border, int xOffset, int yOffset, int zOffset, int width, int height, int depth, Buffer data) {
            bind(gl);
            gl.glTexSubImage2D(GL.GL_TEXTURE_2D, mipLevel, xOffset, yOffset, width, height, format, type, data);
        }
}
