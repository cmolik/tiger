package tiger.util.readpixels;

import java.nio.Buffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class ReadPixels implements GLEventListener {

    int x, y, width, height, channels, type;
    Buffer readBuffer; 
    public boolean readFromBuffer = true;
    public boolean printBuffer = true;

    public ReadPixels(int x, int y, int width, int height, int channels, int type, Buffer readBuffer) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.type = type;
        this.readBuffer = readBuffer;
    }

    @Override
    public void init(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        if(readFromBuffer) {
            GL2 gl = drawable.getGL().getGL2();
            gl.glReadPixels(x, y, width, height, channels, type, readBuffer);
            if(printBuffer) {
                readBuffer.toString();
            }
        }
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}

    @Override
    public void dispose(GLAutoDrawable drawable) {}

}
