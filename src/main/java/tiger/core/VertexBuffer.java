/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.core;


import com.jogamp.common.nio.Buffers;
import java.nio.Buffer;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 *
 * @author cmolikl
 */
public class VertexBuffer {

    public static enum DataType {
        BYTE(Buffers.SIZEOF_BYTE, GL2.GL_BYTE),
        SHORT(Buffers.SIZEOF_SHORT, GL2.GL_SHORT),
        INT(Buffers.SIZEOF_INT, GL2.GL_INT),
        FLOAT(Buffers.SIZEOF_FLOAT, GL2.GL_FLOAT);

        int size;
        int glType;
        DataType(int size, int glType) {
            this.size = size;
            this.glType = glType;
        }
    }

    private int[] glNumber = {0};
    private Buffer buffer;
    private DataType dataType;
    private int size;
    private int stride;
    private int bufferType;

    public VertexBuffer(int bufferType, DataType type, int size, int stride, Buffer buffer) {
        this.bufferType = bufferType;
        this.dataType = type;
        this.buffer = buffer;
        this.size = size;
        this.stride = stride;
    }

    public void init(GL gl) {
        gl.glGenBuffers(1, glNumber, 0);
        update(gl, buffer);
        buffer = null;
    }

    public void update(GL gl, Buffer buffer) {
        gl.glBindBuffer(bufferType, glNumber[0]);
        gl.glBufferData(bufferType, buffer.capacity()*dataType.size, buffer, GL.GL_DYNAMIC_DRAW);
    }

    public int getGlNumber() {
        return glNumber[0];
    }

    public DataType getType() {
        return dataType;
    }

    public int getSize() {
        return size;
    }

    public int getStride() {
        return stride;
    }

    public void bind(GL gl) {
        gl.glBindBuffer(bufferType, glNumber[0]);
    }

    public void enable(GL2 gl, int location, boolean normalize, int offset) {
        bind(gl);
        gl.glEnableVertexAttribArray(location);
        gl.glVertexAttribPointer(location, getSize(), getType().glType, false, getStride(), offset);
    }
}
