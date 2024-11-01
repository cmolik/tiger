/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tiger.recording;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author cmolikl
 */
public class ImageWriter extends Thread {
    
    public String screenShotPath = "c:/Temp/screenShot.png";
    
    private BufferedImage screenShot;
    
    public ImageWriter(BufferedImage screenShot) {
        this.screenShot = screenShot;
    }
    
    public ImageWriter(BufferedImage screenShot, String path) {
        this.screenShot = screenShot;
        this.screenShotPath = path;
    }
    
    @Override
    public void run() {
        try {
            ImageIO.write(screenShot, "PNG", new File(screenShotPath));
        } catch (IOException ex) {
            System.out.println("Saving screenshot failed.");
            ex.printStackTrace();
        }
    }
}
