package tiger.example.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import com.jogamp.opengl.GL;
import tiger.core.Effect;
import tiger.core.Pass;
import tiger.core.RenderState;
import tiger.core.Texture2D;
import tiger.core.Window;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class DisplayLoadedImage {
    public static void main(String[] args) throws IOException {

        RenderState rs = new RenderState();
        rs.clearBuffers(true);
        rs.disable(GL.GL_DEPTH_TEST);

        Texture2D texture = new Texture2D();
        BufferedImage image = ImageIO.read(new File("C:/Users/cmolikl/Projects/PanoramaLabeling/data/vid02/annotations_background0000.png"));
        texture.loadData(image);

        InputStream fragmentStream = ClassLoader.getSystemResourceAsStream("tiger/util/saq/YReversedScreenAlignedQuad.frag");
        Pass pass = new Saq(fragmentStream, texture);
        pass.renderState = rs;

        Effect e = new Effect();
        e.addGLEventListener(pass);
        e.addTexture(texture);

        Window w = new Window(texture.getWidth(), texture.getHeight());
        //w.debug = true;
        w.setEffect(e);
        w.start();
    }
}
