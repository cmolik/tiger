/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tiger.util.convolution;

import gleem.linalg.Mat3f;
import java.net.URL;
import tiger.core.GlslFragmentShader;
import tiger.core.GlslVertexShader;
import tiger.core.Texture2D;
import tiger.util.saq.Saq;

/**
 *
 * @author cmolikl
 */
public class DoubleConvolution extends Saq {
    
    //protected Mat3f kernel;
    
    protected DoubleConvolution() {}

    public DoubleConvolution(Texture2D texture1, Texture2D texture2) {//, Mat3f kernel) {
        ClassLoader classLoader = Convolution.class.getClassLoader();
        URL vertexUrl = classLoader.getResource("tiger/effects/saq/ScreenAlignedQuad.vert");
        URL fragmentUrl = classLoader.getResource("tiger/effects/convolution/DoubleConvolution.frag");
        vertexShader = new GlslVertexShader(vertexUrl);
        fragmentShader = new GlslFragmentShader(fragmentUrl);
        addTexture(texture1, "texture1");
        addTexture(texture2, "texture2");
        //this.kernel = kernel;
    }
    
}
