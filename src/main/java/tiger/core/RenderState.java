/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;

import java.util.HashMap;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

/**
 *
 * @author cmolikl
 */
public class RenderState extends GLEventAdapter {
    
    //gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    //gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    //gl.glEnable(GL.GL_DEPTH_TEST);
    
    private HashMap<Integer, Integer> states = new HashMap<Integer, Integer>();
    
    private float[] clearColor = new float[] {0f, 0f, 0f, 1f};
    private double clearDepth = 1.0;
    private boolean clear = false;
    private int clearBuffers = GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT;
    
    private int colorBlendEquation = GL.GL_FUNC_ADD;
    private int alphaBlendEquation = GL.GL_FUNC_ADD;
    private int sourceColorBlendFunc;
    private int destColorBlendFunc;
    private int sourceAlphaBlendFunc;
    private int destAlphaBlendFunc;
    private int logicOp;

    private int cullFaces = GL.GL_BACK;

    private int depthFunc = GL.GL_LESS;
    
    public void setClearColor(float red, float green, float blue, float alpha) {
        clearColor[0] = red;
        clearColor[1] = green;
        clearColor[2] = blue;
        clearColor[3] = alpha;  
    }
    
    public void setClearColor(float[] color) {
        clearColor = color;
    }
    
    public void setClearDepth(double depth) {
        clearDepth = depth;
    }
    
    public void clearBuffers(boolean clear) {
        this.clear = clear;
    }
    
    public void setBuffersToClear(int buffers) {
        clear = true;
        clearBuffers = buffers;
    }
    
    public void setStateValue(int state, int value) {
        states.put(state, value);
    }
    
    public void enable(int state) {
        states.put(state, 1);
    }
    
    public void disable(int state) {
        states.put(state, 0);
    }
    
    public int getStateValue(int state) {
        Integer value = states.get(state);
        if(value == null) return -1;
        else return value;
    }
    
    public void removeStateValue(int state) {
        states.remove(state);
    }
    
    public void setBlendEquation(int equation) {
        colorBlendEquation = equation;
        alphaBlendEquation = equation;
    }
    
    public void setBlendEquationSeparate(int colorEquation, int alphaEquation) {
        colorBlendEquation = colorEquation;
        alphaBlendEquation = alphaEquation;
    }
    
    public void setBlendFunc(int source, int dest) {
        sourceColorBlendFunc = source;
        sourceAlphaBlendFunc = source;
        destColorBlendFunc = dest;
        destAlphaBlendFunc = dest;
    }
    
    public void setBlendFuncSeparate(int colorSource, int colorDest, int alphaSource, int alphaDest) {
        sourceColorBlendFunc = colorSource;
        sourceAlphaBlendFunc = alphaSource;
        destColorBlendFunc = colorDest;
        destAlphaBlendFunc = alphaDest;
    }
    public void setLogicOp(int logicOp) {
        this.logicOp = logicOp;
    }

    public void setDepthFunc(int depthFunc) {
        this.depthFunc = depthFunc;
    }

    public void setCullFaces(int faces) {
        cullFaces = faces;
    }
    
    public void apply(GL gl) {
        if(clear) {
            gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
            gl.glClearDepth(clearDepth);
            gl.glClear(clearBuffers);
        }
        for(int state : states.keySet()) {
            Integer value = states.get(state);
            if(value == 0) {
                gl.glDisable(state);
            }
            else {
                gl.glEnable(state);
            }
        }   
        Integer logic = states.get(GL.GL_COLOR_LOGIC_OP);
        if(logic != null && logic == 1) {
            GL2 gl2 = gl.getGL2();
            gl2.glEnable(GL.GL_COLOR_LOGIC_OP);
            gl2.glLogicOp(logicOp);
        }
        Integer blend = states.get(GL.GL_BLEND);
        if(blend != null && blend == 1) {     
            gl.glBlendEquationSeparate(colorBlendEquation, alphaBlendEquation);
            gl.glBlendFuncSeparate(sourceColorBlendFunc, destColorBlendFunc, sourceAlphaBlendFunc, destAlphaBlendFunc);
        }
        Integer cull = states.get(GL.GL_CULL_FACE);
        if(cull != null && cull == 1) {
            gl.glCullFace(cullFaces);
        }
        Integer depth = states.get(GL.GL_DEPTH_TEST);
        if(depth != null && depth == 1) {
            gl.glDepthFunc(depthFunc);
        }
    }

    public void display(GLAutoDrawable drawable) {
        apply(drawable.getGL());
    }
    
}
