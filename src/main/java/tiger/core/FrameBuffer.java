/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferShort;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 *
 * @author cmolikl
 */
public class FrameBuffer implements Link<FrameBuffer> {

    public int depthBufferType = GL3.GL_DEPTH_COMPONENT32;
    public int[] drawBuffers = null;
    
    protected IntBuffer glNumber = null;
    public ArrayList<Texture> targets = new ArrayList<>();
    
    public HashMap<Integer, Boolean> blending = new HashMap<>();
    
    public HashMap<Integer, Integer> blendFuncSrc = new HashMap<>();
    public HashMap<Integer, Integer> blendFuncDst = new HashMap<>();
    public HashMap<Integer, Integer> blendFuncAlphaSrc = new HashMap<>();
    public HashMap<Integer, Integer> blendFuncAlphaDst = new HashMap<>();
    
    public HashMap<Integer, Integer> blendEquation = new HashMap<>();
    public HashMap<Integer, Integer> blendEquationAlpha = new HashMap<>();
    
    protected Texture2D depthBuffer;
    protected boolean enableDepthTest = true;
    protected int width = 0;
    protected int height = 0;

    public boolean useTextureArrayLayers = true;

    protected boolean initialized = false;
    
    public FrameBuffer(Texture... targets) {
    	if(targets.length > 0) {
    		this.width = targets[0].width;
    		this.height = targets[0].height;
    	}
        for(Texture target : targets) {
            this.targets.add(target);
        }
    }
    
    public FrameBuffer(boolean enableDepthTest, Texture... targets) {
        this(targets);
        this.enableDepthTest = enableDepthTest;
    }
    
    public FrameBuffer(int width, int height, Texture... targets) {
        this(targets);
        this.width = width;
        this.height = height;
    }
    
    public FrameBuffer(boolean enableDepthTest, int width, int height, Texture... targets) {
        this(targets);
        this.enableDepthTest = enableDepthTest;
        this.width = width;
        this.height = height;
    }

    public void init(GLAutoDrawable drawable) {
        if(!initialized) {
            initialized = true;
            GL3 gl = drawable.getGL().getGL3();
            glNumber = IntBuffer.allocate(1);
            gl.glGenFramebuffers(1, glNumber);

            if (!targets.isEmpty()) {
                bind(gl);
                IntBuffer tmp = IntBuffer.allocate(1);
                gl.glGetIntegerv(GL2.GL_MAX_DRAW_BUFFERS, tmp);
                
                int maxTargets = tmp.get(0);
                //if(enableDepthTest) maxTargets--;
                int targetNumber = 0;

                for (Texture target : targets) {
                    if(target instanceof Texture2DArray && useTextureArrayLayers) {
                        for (int layer = 0; layer < target.depth; layer++) {
                            if (targetNumber >= maxTargets) {
                                break;
                            }
                            attachImage(gl, (Texture2DArray) target, GL.GL_COLOR_ATTACHMENT0 + targetNumber, 0, layer);
                            targetNumber++;
                        }
                    }
                    else {
                        if (targetNumber >= maxTargets) {
                            break;
                        }
                        attachImage(gl, target, GL.GL_COLOR_ATTACHMENT0 + targetNumber, 0);
                        targetNumber++;
                    }
                }
            }
            if(enableDepthTest) {
                if(width > 0 && height > 0) {
                    depthBuffer = new Texture2D(width, height, depthBufferType, GL3.GL_DEPTH_COMPONENT, GL3.GL_FLOAT);
                }
                else {
                    depthBuffer = new Texture2D(depthBufferType, GL3.GL_DEPTH_COMPONENT, GL3.GL_FLOAT);
                }
                depthBuffer.setParameter(GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
                depthBuffer.setParameter(GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
                depthBuffer.setParameter(GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
                depthBuffer.setParameter(GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
                //depthBuffer.setParameter(GL3.GL_DEPTH_TEXTURE_MODE, GL3.GL_INTENSITY);
                //depthBuffer.setParameter(GL3.GL_TEXTURE_COMPARE_MODE, GL3.GL_COMPARE_REF_TO_TEXTURE);
                //depthBuffer.setParameter(GL3.GL_TEXTURE_COMPARE_FUNC, GL3.GL_LEQUAL);
                depthBuffer.init(drawable);
                
                //depthBuffer = RenderBuffer.init(gl, depthBufferType, w, h);
                //depthBuffer.attachStorage(gl, depthBufferType, w, h);
                attachImage(gl, depthBuffer, GL.GL_DEPTH_ATTACHMENT, 0);
            }
        }
    }
    
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        if(depthBuffer != null && this.width <= 0 && this.height <= 0) {
            //depthBuffer.attachStorage(gl, depthBufferType, width, height);
            depthBuffer.reshape(drawable, x, y, width, height);
        }
    }

    public BufferedImage getData(Texture target) {
        GL2 gl = GLU.getCurrentGL().getGL2();
        int index = targets.indexOf(target);
        BufferedImage image = null;
        if(index >= 0) {
            bind(gl);
            gl.glReadBuffer(GL.GL_COLOR_ATTACHMENT0 + index);
            int bands = 3;
            int[] bandIndices = null;
            switch(target.format) {
                case GL.GL_RGBA:
                case GL.GL_BGRA:
                    bands = 4;
                    bandIndices = new int[] {0, 1, 2, 3};
                    break;
                case GL.GL_RGB:
                case GL2.GL_BGR:
                    bands = 3;
                    bandIndices = new int[] {0, 1, 2};
                    break;
                case GL.GL_LUMINANCE:
                    bands = 1;
                    bandIndices = new int[] {0};
                    break;
            }
            int size = target.width * target.height * bands;
            //int bandBytes = 1;
            DataBuffer dataBuffer = null;
            switch(target.internalFormat) {
                case GL2.GL_RGB8:
                case GL2.GL_RGBA8:    
                case GL2.GL_LUMINANCE:
                case GL2.GL_LUMINANCE8:
                    //bandBytes = 1;
                    ByteBuffer bbuffer = ByteBuffer.allocate(size);
                    gl.glReadPixels(0, 0, target.width, target.height, target.format, target.type, bbuffer);
                    dataBuffer = new DataBufferByte(bbuffer.array(), size);
                    break;
                case GL2.GL_LUMINANCE16:
                case GL2.GL_RGB16:
                case GL2.GL_RGB16F:
                case GL2.GL_RGB16I:
                case GL2.GL_RGB16UI:
                    //bandBytes = 2;
                    ShortBuffer sbuffer = ShortBuffer.allocate(size);
                    gl.glReadPixels(0, 0, target.width, target.height, target.format, target.type, sbuffer);
                    dataBuffer = new DataBufferShort(sbuffer.array(), size);
                    break;
                case GL2.GL_LUMINANCE32F:
                case GL2.GL_LUMINANCE32I:
                case GL2.GL_LUMINANCE32UI:
                case GL3.GL_RGB32F:
                case GL2.GL_RGB32I:
                case GL2.GL_RGB32UI:
                case GL2.GL_RGBA32F:
                    //bandBytes = 4;
                    FloatBuffer fbuffer = FloatBuffer.allocate(size);
                    gl.glReadPixels(0, 0, target.width, target.height, target.format, target.type, fbuffer);
//                    for(int y = 0; y < target.getHeight(); y++) {
//                        for(int x = 0; x < target.getWidth(); x++) {
//                            System.out.print("[");
//                            System.out.print(fbuffer.get() + " ");
//                            System.out.print(fbuffer.get() + " ");
//                            System.out.print(fbuffer.get() + " ");
//                            System.out.print(fbuffer.get() + " ");
//                            System.out.print("] ");
//                        }
//                        System.out.println("");
//                    }
                    dataBuffer = new DataBufferFloat(fbuffer.array(), size);
                    break;
            }
            
            
            WritableRaster raster = WritableRaster.createInterleavedRaster(dataBuffer, target.width, target.height, target.width * bands, bands, bandIndices, null);
            ComponentColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), bands>3?true:false, false, bands>3?Transparency.TRANSLUCENT:Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            image = new BufferedImage(cm, raster, false, null);
        }
        return image;
    }
    
    public int getGlNumber() {
        return glNumber.get(0);
    }
    
    public void setBlending(boolean value, int... targets) {
        for(int target : targets) {
            blending.put(target, value);
        }
    }
    
    /*public void setBlendFunc(int target, int src, int dst) {
        blendFuncSrc.put(target, src);
        blendFuncDst.put(target, dst);
        blendFuncAlphaSrc.put(target, src);
        blendFuncAlphaDst.put(target, dst);
    }
    
    public void setBlendFuncSeparate(int target, int src, int dst, int alphaSrc, int alphaDst) {
        blendFuncSrc.put(target, src);
        blendFuncDst.put(target, dst);
        blendFuncAlphaSrc.put(target, alphaSrc);
        blendFuncAlphaDst.put(target, alphaDst);
    }
    
    public void setBlendEquation(int target, int equation) {
        blendEquation.put(target, equation);
        blendEquationAlpha.put(target, equation);
    }
    
    public void setBlendEquationSeparate(int target, int equation, int equationAlpha) {
        blendEquation.put(target, equation);
        blendEquationAlpha.put(target, equationAlpha);
    }*/
    
    public void bind(GL gl) {
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, glNumber.get(0));
    }
    
    public static void bindScreen(GL gl) {
        gl.glBindFramebuffer(GL.GL_FRAMEBUFFER, 0);
    }
    
    public void attachImage(GL3 gl, Texture texture, int attachmentPoint, int mipLevel) {
        gl.glFramebufferTexture(GL.GL_FRAMEBUFFER, attachmentPoint, texture.getGlNumber(), mipLevel);
    }

    public void attachImage(GL3 gl, Texture2DArray texture, int attachmentPoint, int mipLevel, int level) {
        gl.glFramebufferTextureLayer(GL.GL_FRAMEBUFFER, attachmentPoint, texture.getGlNumber(), mipLevel, level);
    }
    
    public void attachImage(GL gl, RenderBuffer buffer, int attachmentPoint) {
        gl.glFramebufferRenderbuffer(GL.GL_FRAMEBUFFER, attachmentPoint, GL.GL_RENDERBUFFER, buffer.getGlNumber());
    }
    
    public void delete(GL gl) {
        gl.glDeleteFramebuffers(1, glNumber);
    }
    
    public FrameBuffer get() {
        return this;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        if(width > 0) {
            return width;
        }
        else if(targets.size() > 0) {
            return targets.get(0).width;
    	}
        else {
            return 0;
        }
    }

    /**
     * @return the height
     */
    public int getHeight() {
        if(height > 0) {
            return height;
        }
        else if(targets.size() > 0) {
            return targets.get(0).height;
    	}
        else {
            return 0;
        }
    }
    
    public void setDrawBuffers(int[] drawBuffers) {
        this.drawBuffers = drawBuffers;
    }

    public void setupDrawBuffers(GL gl) {
        int[] db; 
        if(drawBuffers == null) {
            int t = 0;
            if(useTextureArrayLayers) {
                for (Texture target : targets) {
                    if(target instanceof Texture2DArray) {
                        t += target.depth;
                    }
                    else {
                        t++;
                    }
                }
            }
            else {
                t = targets.size();
            }
            db = new int[t];
            for(int i = 0; i < t; i++) {
                db[i] = GL.GL_COLOR_ATTACHMENT0 + i;
            }
        }
        else {
            db = drawBuffers;
        }
        gl.getGL2().glDrawBuffers(db.length, db, 0);
    }
    
    public void setupBlending(GL gl) {
//        if(gl.isGL3()) {
//            GL3 gl3 = gl.getGL3();
//            for(int target : blending.keySet()) {
//                if(blending.get(target) == true) {
//                    gl3.glEnablei(target, GL.GL_BLEND);
//                }
//                else {
//                    gl3.glDisablei(target, GL.GL_BLEND);
//                }
//            }
//            /*GL4 gl4 = gl.getGL4();
//            for(int target : blendEquation.keySet()) {
//                gl4.glBlendEquationSeparatei(target, blendEquation.get(target), blendEquationAlpha.get(target));
//            }
//            for(int target : blendFuncSrc.keySet()) {
//                gl4.glBlendFuncSeparatei(target, blendFuncSrc.get(target), blendFuncDst.get(target), blendFuncAlphaSrc.get(target), blendFuncAlphaDst.get(target));
//            }*/
//        }
    }

    public int checkStatus(GL gl) {
        int fboStatus = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
        if(fboStatus != GL.GL_FRAMEBUFFER_COMPLETE) {
            //System.out.print(name + ":");
            switch(fboStatus) {
                case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT");
                    break;
                case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT");
                    break;
                case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT");
                    break;
                case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT");
                    break;
                case GL2.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT");
                    break;
                case GL2.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                    System.out.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT");
                    break;
                case GL.GL_FRAMEBUFFER_UNSUPPORTED:
                    System.out.println("GL_FRAMEBUFFER_UNSUPPORTED_EXT");
                    break;
                default:
                    System.out.println("UNKNOWN FRAMEBUFFER ERROR");
                    break;
            }
        }
        return fboStatus;
    }
}
