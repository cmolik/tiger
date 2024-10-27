/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.convolution;

import java.io.InputStream;
import tiger.core.GlslFragmentShader;
import tiger.core.GlslVertexShader;
import tiger.core.Texture2D;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class ConvolutionUI extends Saq {
    
    //protected Mat3f kernel;
    
    protected ConvolutionUI() {}

    public ConvolutionUI(Texture2D texture) {//, Mat3f kernel) {
        InputStream vertex = ClassLoader.getSystemResourceAsStream("tiger/util/saq/ScreenAlignedQuad.vert");
        InputStream fragment = ClassLoader.getSystemResourceAsStream("tiger/util/convolution/ConvolutionUI.frag");
        vertexShader = new GlslVertexShader(vertex);
        fragmentShader = new GlslFragmentShader(fragment);
        addTexture(texture, "texture");
        //this.kernel = kernel;
    }
    
}
